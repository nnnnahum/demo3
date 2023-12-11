package com.demo.auth;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;
import com.demo.utils.AuthUtil;
import com.demo.utils.OrgUtil;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.PermissionOnEntity;
import entities.HostingProvider;
import entities.Role;
import entities.Customer;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class RoleService implements BaseService{

	public static final UUID SUPER_ADMIN_ROLE_ID = UUID.fromString("c0c22317-d973-4a49-9b2e-cb71d00744c8");

	public static final String ADMIN_ROLE = "Admin";
	public static final String READ_ROLE = "Read-Only";

	private static final Logger log = LoggerFactory.getLogger(RoleService.class);

	public static final String PATH = "/roles";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	RoleModel model;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Autowired
	AuthUtil authUtil;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Role.RESOURCE, this);
		router.registerEventsOfInterest(EventsOfInterest.customer_created, this);
		router.registerEventsOfInterest(EventsOfInterest.provider_created, this);
		
		if(model.getById(SUPER_ADMIN_ROLE_ID) == null) {
			//create super admin role
			Role role = new Role(SUPER_ADMIN_ROLE_ID, "Super Baradaz", new Organization());
			model.post(role);
		}
	}
		
	public ResponseMessage post(RequestMessage request) {
		Role role = (Role) request.getBody();
		role.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateRole(role, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(role.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_ROLES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// make sure there isn't another role with the same name for this org
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + role.getName()
						+ ";organization.id==" + role.getOrg().getId()
						+ ";id!=" + role.getId());
		List<Role> roles = model.get(emailCheck);
		if(roles != null && !roles.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another role already exists with name: " 
					+ role.getName());
		}
			
		role = model.post(role);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), role);
	}

	private FieldValidationErrorMessage validateRole(Role role, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(role.getName() == null || role.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		if(role.getOrg() == null || role.getOrg().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "org.id");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Role role = model.getById(request.getId());
		if(role == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Role not found with Id: " + request.getId());
		}
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(role.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_ROLES, Permission.VIEW_ROLES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), role);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Role role = (Role) request.getBody();
		role.setId(request.getId());
		FieldValidationErrorMessage fvem = validateRole(role, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(role.getOrg().getId());
				if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_ROLES))) {
					return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
							request.getHeaders(), 
							"Unaurothized operation.");
				}
			}
		
		Role existingRole = model.getById(role.getId());
		// ensure a user can't modify the org of a role
		role.setOrg(existingRole.getOrg());
		
		// make sure there isn't another role with the same name for this org
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + role.getName()
						+ ";organization.id==" + existingRole.getOrg().getId()
						+ ";id!=" + role.getId());
		List<Role> roles = model.get(emailCheck);
		if(roles != null && !roles.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another role already exists with name: " 
					+ role.getName());
		}

		role = model.put(role);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), role);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		Role existingRole = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingRole.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_ROLES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		model.delete(request.getId());
		return new ResponseMessage(HttpStatus.NO_CONTENT, request.getHeaders(), null);
	}

	public ResponseMessage get(RequestMessage request) {
		if (request.getId() != null) {
			return getById(request);
		}
		Params query = request.getQuery();
		if(request.getSource() != Location.LOCAL) {	
			query = authUtil.appendQueryForOrgPermissions(request);
		}
		List<Role> roles = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), roles, 
				new Count((long)roles.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		switch(eventName) {
		case customer_created:
			Customer customer = (Customer) entity;
			createDefaultRolesForCustomer(customer);
			return;
		case provider_created:
			HostingProvider provider = (HostingProvider) entity;
			createDefaultRolesForProvider(provider);
			return;
		default:
			log.error("Received unknown event type: ", eventName);
				
		}
	}

	private void createDefaultRolesForProvider(HostingProvider provider) {
		// create admin and read only roles for the new provider. Patch the provider with the new permissions
		UUID adminRoleId = UUID.randomUUID();
		UUID viewRoleId = UUID.randomUUID();
		createRolesForOrg(provider, adminRoleId, viewRoleId);
		
		// patch provider permissions given the new roles.
		List<PermissionOnEntity> providerPerms = provider.getPerms();
		getPermsForAdminRoleForProvider(providerPerms, adminRoleId);
		getPermsForViewRoleForProvider(providerPerms, viewRoleId);
		provider.setPerms(providerPerms);
		ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.PATCH, HostingProvider.RESOURCE,
				provider.getId(),  null,  null,  provider, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			log.error("Error patching provider with new permissions");
		}
	}

	private void getPermsForViewRoleForProvider(List<PermissionOnEntity> providerPerms, UUID viewRoleId) {
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_PROVIDERS, viewRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_LIBRARIES, viewRoleId));		
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_USERS, viewRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_ROLES, viewRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_DATACENTERS, viewRoleId));		
	}

	private void getPermsForAdminRoleForProvider(List<PermissionOnEntity> providerPerms, UUID adminRoleId) {
		providerPerms.add(new PermissionOnEntity(Permission.MANAGE_PROVIDERS, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.MANAGE_LIBRARIES, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.MANAGE_USERS, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.MANAGE_ROLES, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.MANAGE_DATACENTERS, adminRoleId));

		providerPerms.add(new PermissionOnEntity(Permission.VIEW_PROVIDERS, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_LIBRARIES, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_USERS, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_ROLES, adminRoleId));
		providerPerms.add(new PermissionOnEntity(Permission.VIEW_DATACENTERS, adminRoleId));		
	}

	private void createRolesForOrg(Organization org, UUID adminUuid, UUID viewRoleId) {
		Role role = new Role(adminUuid, ADMIN_ROLE, org);
		model.post(role);
		role = new Role(viewRoleId, READ_ROLE, org);
		model.post(role);
	}

	private void createDefaultRolesForCustomer(Customer customer) {

		// create admin and read only roles for the new customer. Patch the customer with the new permissions
		UUID adminRoleId = UUID.randomUUID();
		UUID viewRoleId = UUID.randomUUID();
		createRolesForOrg(customer, adminRoleId, viewRoleId);
		
		// patch customer permissions given the new roles.
		List<PermissionOnEntity> customerPerms = customer.getPerms();
		getPermsForAdminRoleForCustomer(customerPerms, adminRoleId);
		getPermsForViewRoleForCustomer(customerPerms, viewRoleId);
		customer.setPerms(customerPerms);
		ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.PATCH, 
				Customer.RESOURCE, customer.getId(),  null,  null,  customer, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			log.error("Error patching customer with new permissions");
		}
	}

	private void getPermsForViewRoleForCustomer(List<PermissionOnEntity> customerPerms, UUID viewRoleId) {
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_USERS, viewRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_ROLES, viewRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_SITES, viewRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_CLOUD_LIBRARIES, viewRoleId));		
	}

	private void getPermsForAdminRoleForCustomer(List<PermissionOnEntity> customerPerms, UUID adminRoleId) {
		customerPerms.add(new PermissionOnEntity(Permission.MANAGE_USERS, adminRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.MANAGE_ROLES, adminRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.MANAGE_CLOUD_LIBRARIES, adminRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_USERS, adminRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_ROLES, adminRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_SITES, adminRoleId));
		customerPerms.add(new PermissionOnEntity(Permission.VIEW_CLOUD_LIBRARIES, adminRoleId));
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Role role = (Role) request.getBody();
		role.setId(request.getId());
		
		Role existingRole = model.getById(role.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingRole.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_ROLES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// make sure there isn't another role with the same name for this org
		if(role.getName() != null && !role.getName().isEmpty()) {
			Params emailCheck = new Params();
			emailCheck.setQuery("name==" + role.getName()
							+ ";organization.id==" + existingRole.getOrg().getId()
							+ ";id!=" + role.getId());
			List<Role> roles = model.get(emailCheck);
			if(roles != null && !roles.isEmpty()) {
				return new ErrorMessage(HttpStatus.CONFLICT, 
						request.getHeaders(), 
						"Another role already exists with name: " 
						+ role.getName());
			}
		}

		role = model.patch(role);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), role);
	}
}