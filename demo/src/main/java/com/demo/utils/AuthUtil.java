package com.demo.utils;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.auth.RoleService;
import com.demo.auth.SessionService;
import com.demo.router.MessageRouter;

import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.PermissionOnEntity;
import entities.Session;
import entities.User;
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

	public boolean hasPermissionInOrg(RequestMessage request, Organization org, List<Permission> acceptablePermission) {
		User user = getUserFromRequestHeader(request);
		if(user == null) return false;
		// if this is a super user then return true;
		if(RoleService.SUPER_ADMIN_ROLE_ID.equals(user.getRole().getId())) {
			return true;
		}
		// get the org associated with the user and check if the role can perform the permission
		return doesOrgHavePermissionForRole(org.getPerms(), user.getRole().getId(), acceptablePermission);
	}

	private User getUserFromRequestHeader(RequestMessage request) {
		if(request.getHeaders() == null || request.getHeaders().get("authId") == null) {
			return null;
		}
		request.getBody();
		String authId = request.getHeaders().get("authId");
		UUID authUuid = UUID.fromString(authId);
		RequestMessage requestForSession = new RequestMessage(HttpMethod.GET, Session.RESOURCE, 
				authUuid, null, null, null, Location.LOCAL, Location.LOCAL);
		ResponseMessage response = router.sendAndReceive(requestForSession);
		if(response.getStatus() != HttpStatus.OK) {
			return null;
		}
		Session session = (Session) response.getBody();
		response = router.sendAndReceive(new RequestMessage(HttpMethod.GET, User.RESOURCE, 
				session.getUserId(), null,  null, null, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			return null;
		}
		return (User)response.getBody();
	}

	private static boolean doesOrgHavePermissionForRole(List<PermissionOnEntity> perms, UUID roleId,
			List<Permission> acceptablePermissions) {
		for (PermissionOnEntity perm: perms) {
			for(Permission accepted: acceptablePermissions) {
				if(roleId.equals(perm.getRoleId()) && perm.getPermission() == accepted) {
					return true;
				}
			}
		}
		return false;
	}

	public Params appendQueryForOrgPermissions(RequestMessage request) {
		return this.appendQueryForTenantPermissions(request, "org") ;
	}

	public Params appendQueryForTenantPermissions(RequestMessage request, String parentField) {
		User user = getUserFromRequestHeader(request);
		
		if(RoleService.SUPER_ADMIN_ROLE_ID.equals(user.getRole().getId())) {
			// this is hte super admin, no need to append anything
			return request.getQuery();
		}
		
		// My user has a role. 
		// I need all the orgs that my role can view or manage
		// for now we are just going with "my org" since we can't share across orgs.
		// append org.id=in(anyof the roles)
		// for now assuming that if you are in the org you have at least view privilege on everything this 
		// may change.
		
		Organization org = orgUtil.getOrgfromOrgId(user.getOrg().getId());
		Params query = request.getQuery();
		if(query.get(Params.QUERY) == null) {
			query.put(Params.QUERY, parentField + ".id==" + org.getId());
		} else {
			query.put(Params.QUERY, "(" + query.get(Params.QUERY) + ");" + parentField + ".id==" + org.getId());
		}
		return query;
	}
}
