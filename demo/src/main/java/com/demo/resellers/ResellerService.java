package com.demo.resellers;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;
import com.demo.utils.AuthUtil;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.Permission;
import entities.PermissionOnEntity;
import entities.Reseller;
import entities.User;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class ResellerService implements BaseService{

	public static final String PATH = "/resellers";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	ResellerModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Reseller.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		Reseller reseller = (Reseller) request.getBody();
		reseller.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateReseller(reseller, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
			
		User userCreatingReseller = authUtil.getUserFromSession(request);
		reseller.getPerms().add(new PermissionOnEntity(Permission.MANAGE_USERS, userCreatingReseller.getRole().getId().toString()));
		reseller = model.post(reseller);
		router.notify(EventsOfInterest.reseller_created, reseller);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), reseller);
	}
	
	private FieldValidationErrorMessage validateReseller(Reseller reseller, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(reseller.getName() == null || reseller.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Reseller reseller = model.getById(request.getId());
		if(reseller == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Reseller not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), reseller);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Reseller reseller = (Reseller) request.getBody();
		reseller.setId(request.getId());
		FieldValidationErrorMessage fvem = validateReseller(reseller, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		reseller = model.put(reseller);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), reseller);	
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
		List<Reseller> resellers = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), resellers, 
				new Count((long)resellers.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Reseller reseller = (Reseller) request.getBody();
		reseller.setId(request.getId());
		reseller = model.patch(reseller);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), reseller);
	}
}
