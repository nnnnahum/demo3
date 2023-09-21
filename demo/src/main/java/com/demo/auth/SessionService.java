package com.demo.auth;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;
import com.demo.utils.PasswordEncrypter;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.LoginRequest;
import entities.Session;
import entities.User;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class SessionService implements BaseService{

	public static final String PATH = "/sessions";
	
	private static final Logger log = LoggerFactory.getLogger(RoleService.class);
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	UserModel userModel;
	
	@Autowired
	SessionModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Session.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		LoginRequest loginRequest = (LoginRequest) request.getBody();
		FieldValidationErrorMessage fvem = validateLoginRequest(loginRequest, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		loginRequest.setPassword(PasswordEncrypter.encrypt(loginRequest.getPassword()));

		// look for a user with the same creds
		String query = "emailAddress==" + loginRequest.getEmailAddress() + ";password=="+loginRequest.getPassword();
		Params params = new Params(query, null,  null, null);
		List<User> users = userModel.get(params);
		if(users == null || users.size() == 0) {
			log.warn("Incorrect login with email: " + loginRequest.getEmailAddress());
			return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
					request.getHeaders(), 
					"Incorrect login with email: " 
					+ loginRequest.getEmailAddress());
		} else if (users.size() > 1) {
			log.error("More than one user with the same email account and password");
			return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, 
					request.getHeaders(), 
					"Internal server error for login with email: " 
					+ loginRequest.getEmailAddress());
		} else {
			// create a session with expiration an hour from now.
			Date created = new Date();
			Date expires = new Date(created.getTime() + 3600000);
			Session session = new Session(UUID.randomUUID(), users.get(0).getId(), created, expires);
			return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), model.post(session));
		}
	}
	
	private FieldValidationErrorMessage validateLoginRequest(LoginRequest loginRequest, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(loginRequest.getEmailAddress() == null || loginRequest.getEmailAddress().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "emailAddress");
		}
		
		if(loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "password");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Session session = model.getById(request.getId());
		if(session == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Session not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), session);
	}
	
	public ResponseMessage put(RequestMessage request) {
		// intentionally left blank.
		return new ErrorMessage(HttpStatus.UNAUTHORIZED, request.getHeaders(), "Unable to update a session");
	}
	
	public ResponseMessage delete(RequestMessage request) {
		model.delete(request.getId());
		return new ResponseMessage(HttpStatus.NO_CONTENT, request.getHeaders(), null);
	}

	public ResponseMessage get(RequestMessage request) {
		if (request.getId() != null) {
			return getById(request);
		}
		Params query = request.getQuery();
		List<Session> sessions = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), sessions, 
				new Count((long)sessions.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Session session = (Session) request.getBody();
		session.setId(request.getId());
		session = model.patch(session);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), session);
	}
}
