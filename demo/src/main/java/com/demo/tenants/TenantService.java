package com.demo.tenants;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.Tenant;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class TenantService implements BaseService{

	public static final UUID SUPERTENANTID = UUID.fromString("3fc52e06-95de-4247-9bb6-e07720b6f40d");

	public static final String PATH = "/tenants";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	TenantModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Tenant.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		Tenant tenant = (Tenant) request.getBody();
		tenant.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateTenant(tenant, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
			
		tenant = model.post(tenant);
		router.notify(EventsOfInterest.tenant_created, tenant);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), tenant);
	}
	
	private FieldValidationErrorMessage validateTenant(Tenant tenant, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(tenant.getName() == null || tenant.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Tenant tenant = model.getById(request.getId());
		if(tenant == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Tenant not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tenant);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Tenant tenant = (Tenant) request.getBody();
		tenant.setId(request.getId());
		FieldValidationErrorMessage fvem = validateTenant(tenant, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		tenant = model.put(tenant);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tenant);	
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
		List<Tenant> tenants = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tenants, 
				new Count((long)tenants.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Tenant tenant = (Tenant) request.getBody();
		tenant.setId(request.getId());
		tenant = model.patch(tenant);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tenant);
	}
}
