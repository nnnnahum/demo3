package com.demo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.auth.RoleService;
import com.demo.auth.SessionService;
import com.demo.router.MessageRouter;

import entities.Customer;
import entities.HostingProvider;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.PermissionOnEntity;
import entities.Reseller;
import entities.Session;
import entities.User;
import entities.requests.ErrorMessageException;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class AuthUtil {
	
	@Autowired
	SessionService sessionService;
	
	@Autowired
	OrgUtil orgUtil;
	
	
	@Autowired
    MessageRouter router;
	
	private static final Logger log = LoggerFactory.getLogger(AuthUtil.class);

	public boolean hasPermissionInOrg(RequestMessage request, Organization org, List<Permission> acceptablePermission) {
		User user = getUserFromSession(request);
		if(user == null) return false;
		// if this is a super user then return true;
		if(user.getRole().getId().equals(RoleService.SUPER_ADMIN_ROLE_ID)) return true;
		
		if(org == null) return false;
		// get the org associated with the user and check if the role can perform the permission
		return doesOrgHavePermissionForRole(org.getPerms(), 
				user.getRole().getId(), acceptablePermission);
	}

	private static boolean doesOrgHavePermissionForRole(List<PermissionOnEntity> perms, UUID roleId,
			List<Permission> acceptablePermissions) {
		for (PermissionOnEntity perm: perms) {
			for(Permission accepted: acceptablePermissions) {
				if(perm.getRoleId().equals(roleId.toString()) && perm.getPermission() == accepted) {
					return true;
				}
			}
		}
		return false;
	}

	public Params appendQueryForOrgPermissions(RequestMessage request) throws ErrorMessageException{
		return this.appendQueryForTenantPermissions(request, "org") ;
	}

	public Params appendQueryForTenantPermissions(RequestMessage request, String parentField) throws ErrorMessageException{
		User user = getUserFromSession(request);
		
		// this is the super admin, no need to append anything
		if(user.getRole().getId().equals(RoleService.SUPER_ADMIN_ROLE_ID)) {
			return request.getQuery();
		}
		
		// fetch all the orgs I have access to with my session Id,
		// providers, customers, resellers
		List<UUID> orgIdsUserHasAccessTo = new ArrayList<UUID>();
		Params headers = new Params();
		headers.put("authId", request.getHeaders().get("authId"));
		RequestMessage fetchProviders = new RequestMessage(HttpMethod.GET, HostingProvider.RESOURCE, null, null, headers, null, Location.MGMTAPI, Location.LOCAL);
		ResponseMessage response = router.sendAndReceive(fetchProviders);
		if(response.getStatus() == HttpStatus.OK) {
			List<HostingProvider> providers = (List<HostingProvider>)response.getBody();
			orgIdsUserHasAccessTo.addAll(providers.stream().map(x -> x.getId()).toList());
		}
		RequestMessage fetchCustomers = new RequestMessage(HttpMethod.GET, Customer.RESOURCE, null, null, headers, null, Location.MGMTAPI, Location.LOCAL);
		response = router.sendAndReceive(fetchCustomers);
		if(response.getStatus() == HttpStatus.OK) {
			List<Customer> customers = (List<Customer>)response.getBody();
			orgIdsUserHasAccessTo.addAll(customers.stream().map(x -> x.getId()).toList());
		}
		RequestMessage fetchResellers = new RequestMessage(HttpMethod.GET, Reseller.RESOURCE, null, null, headers, null, Location.MGMTAPI, Location.LOCAL);
		response = router.sendAndReceive(fetchResellers);
		if(response.getStatus() == HttpStatus.OK) {
			List<Reseller> resellers = (List<Reseller>)response.getBody();
			orgIdsUserHasAccessTo.addAll(resellers.stream().map(x -> x.getId()).toList());
		}
		
		if(orgIdsUserHasAccessTo.isEmpty()) {
			log.error("This user has no access.");
			throw new ErrorMessageException("Auth-001", "This user has no access");
		}
	
		Params query = request.getQuery();
		if(query.get(Params.QUERY) == null) {
			query.put(Params.QUERY, parentField + ".id=in=(" + orgIdsUserHasAccessTo.stream().map(x -> x.toString())
			.collect(Collectors.joining(",")) + ")");
		} else {
			query.put(Params.QUERY, "(" + query.get(Params.QUERY) + ");" + parentField + ".id=in=(" + orgIdsUserHasAccessTo.stream().map(x -> x.toString())
					.collect(Collectors.joining(",")) + ")");
		}
		return query;
	}
	
	public User getUserFromSession(RequestMessage request) {
		
		String auth = request.getHeaders().get("authId");
		if(auth == null) return null;
		UUID authId = UUID.fromString(auth);
		RequestMessage fetchSession = new RequestMessage(HttpMethod.GET, Session.RESOURCE, authId, null, null, null, Location.LOCAL, Location.LOCAL);
		ResponseMessage response = sessionService.getById(fetchSession);
		if(response.getStatus() != HttpStatus.OK) return null;
		Session session = (Session) response.getBody();
		RequestMessage fetchUser = new RequestMessage(HttpMethod.GET, User.RESOURCE, session.getUserId(), null, null, null, Location.LOCAL, Location.LOCAL);
		response = router.sendAndReceive(fetchUser);
		if(response.getStatus() != HttpStatus.OK) return null;
		User user = (User) response.getBody();
		return user;
	}

	public Params appendQueryForOrgWithPermission(RequestMessage request, Permission viewProviders) {
		User user = getUserFromSession(request);
		
		// this is the super admin, no need to append anything
		if(user.getRole().getId().equals(RoleService.SUPER_ADMIN_ROLE_ID)) {
			return request.getQuery();
		}
		
		//append to the query that the perms has an entry with the permission and role id of the user
		Params query = request.getQuery();
		query = query == null? new Params() : query;
		String queryStr = query.getQuery();
		if(queryStr == null || queryStr.isEmpty()) {
			queryStr = "perms=em=(permission:"+viewProviders.name() +",roleId:"+user.getRole().getId() +")" ;
//			queryStr = "name==cyxtera";
//			queryStr = "perms.permission==" + viewProviders.name() + ";perms.roleId=="+user.getRole().getId();
		} else {
			queryStr += ";perms=em=(permission:"+viewProviders.name() +",roleId:"+user.getRole().getId() +")";
		}
		query.put(Params.QUERY, queryStr);
		return query;
	}
}
