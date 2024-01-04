package com.demo.auth;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;
import com.demo.utils.AuthUtil;
import com.demo.utils.OrgUtil;
import com.demo.utils.PasswordEncrypter;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.Role;
import entities.User;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.ErrorMessageException;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@DependsOn("roleService")
@Component
public class UserService implements BaseService{

	public static final UUID SUPERUSERID = UUID.fromString("2890a526-5dfa-4f71-9b66-5681b2119427");

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	public static final String PATH = "/users";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	UserModel model;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@PostConstruct
	public void start() {
		router.registerRoute(User.RESOURCE, this);
		
		if(model.getById(SUPERUSERID) == null) {
			ResponseMessage response = router.sendAndReceive(
					new RequestMessage(HttpMethod.GET, Role.RESOURCE, 
							RoleService.SUPER_ADMIN_ROLE_ID, null, null, null, Location.LOCAL, Location.LOCAL));
			Role role = (Role)response.getBody();
			//create super admin user
			User user = new User(SUPERUSERID, "Super", "GEYSERDATA", null, "admin@geyserdata.com", PasswordEncrypter.encrypt("abc123"), role, null);
			model.post(user);
		}
	}
		
	public ResponseMessage post(RequestMessage request) {
		User user = (User)request.getBody();
		user.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateUser(user, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(user.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_USERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		user.setPassword(PasswordEncrypter.encrypt(user.getPassword()));
		
		// make sure there isn't another user with the same email
		Params emailCheck = new Params();
		emailCheck.setQuery("emailAddress==" + user.getEmailAddress()
						+ ";id!=" + user.getId());
		List<User> users = model.get(emailCheck);
		if(users != null && !users.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another user already exists for email: " 
					+ user.getEmailAddress());
		}
			
		user = model.post(user);
		user.setPassword(null);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), user);
	}
	
	private FieldValidationErrorMessage validateUser(User user, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(user.getEmailAddress() == null || user.getEmailAddress().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "emailAddress");
		}
		
		if(user.getFirstName() == null || user.getFirstName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "firstName");
		}
		
		if(user.getLastName() == null || user.getLastName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "lastName");
		}
		
		if(user.getPassword() == null || user.getPassword().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "password");
		}
		
		if(user.getRole() == null || user.getRole().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "role.id");
		}

		if(user.getOrg() == null || user.getOrg().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "org.id");
		}

		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		User user = model.getById(request.getId());
		if(user == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"User not found with Id: " + request.getId());
		}
		
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(user.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_USERS, Permission.VIEW_USERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		user.setPassword(null);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), user);
	}
	
	public ResponseMessage put(RequestMessage request) {
				
		User user = (User) request.getBody();
		user.setId(request.getId());
		FieldValidationErrorMessage fvem = validateUser(user, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
		Organization org = orgUtil.getOrgfromOrgId(user.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_USERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}

		// make sure there isn't another user with the same email
		Params emailCheck = new Params();
		emailCheck.setQuery("emailAddress==" + user.getEmailAddress()
						+ ";id!=" + user.getId());
		List<User> users = model.get(emailCheck);
		if(users != null && !users.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another user already exists for email: " 
					+ user.getEmailAddress());
		}
		
		user.setPassword(PasswordEncrypter.encrypt(user.getPassword()));

		user = model.put(user);
		user.setPassword(null);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), user);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		User existingUser = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingUser.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_USERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		model.delete(request.getId());
		return new ResponseMessage(HttpStatus.NO_CONTENT, request.getHeaders(), null);
	}

	public ResponseMessage get(RequestMessage request) throws ErrorMessageException{
		if (request.getId() != null) {
			return getById(request);
		}
		
		Params query = request.getQuery();
		if(request.getSource() != Location.LOCAL) {	
			query = authUtil.appendQueryForOrgPermissions(request);
		}
		List<User> users = model.get(query);
		users.stream().forEach(user -> user.setPassword(null));
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), users, 
				new Count((long)users.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		User user = (User) request.getBody();
		user.setId(request.getId());
		
		User existingUser = model.getById(user.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingUser.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_USERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
				
		// make sure there isn't another user with the same email address
		if(user.getEmailAddress() != null && !user.getEmailAddress().isEmpty()) {
			Params emailCheck = new Params();
			emailCheck.setQuery("emailAddress==" + user.getEmailAddress()
							+ ";id!=" + user.getId());
			List<User> users = model.get(emailCheck);
			if(users != null && !users.isEmpty()) {
				return new ErrorMessage(HttpStatus.CONFLICT, 
						request.getHeaders(), 
						"Another users already exists with email: " 
						+ user.getEmailAddress());
			}
		}

		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(PasswordEncrypter.encrypt(user.getPassword()));
		}
		user = model.patch(user);
		user.setPassword(null);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), user);
	}
}
