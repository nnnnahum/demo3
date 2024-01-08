package com.demo.domainengines;

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
import entities.Datacenter;
import entities.DomainEngine;
import entities.EventsOfInterest;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.ErrorMessageException;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class DomainEngineService implements BaseService{

	private static final Logger log = LoggerFactory.getLogger(DomainEngineService.class);
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Autowired
	DomainEngineModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(DomainEngine.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		DomainEngine domainEngine = (DomainEngine) request.getBody();
		domainEngine.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateDomainEngine(domainEngine, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(domainEngine.getDatacenter().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_DOMAIN_ENGINES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		ResponseMessage responseMessage = router.sendAndReceive(new RequestMessage(HttpMethod.GET, Datacenter.RESOURCE, 
				domainEngine.getDatacenter().getId(), null, null, null, Location.LOCAL, Location.LOCAL));
		if(responseMessage.getStatus() != HttpStatus.OK) {
			log.error("Datacenter with id {} not found.", domainEngine.getDatacenter().getId());
			return new ErrorMessage(HttpStatus.BAD_REQUEST, request.getHeaders(), responseMessage);
		}
		Datacenter datacenter = (Datacenter) responseMessage.getBody();
		domainEngine.setDatacenter(datacenter);
		domainEngine = model.post(domainEngine);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), domainEngine);
	}
	
	private FieldValidationErrorMessage validateDomainEngine(DomainEngine domainEngine, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(domainEngine.getIp() == null || domainEngine.getIp().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "ip");
		}
		
		if(domainEngine.getDatacenter() == null || domainEngine.getDatacenter().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "datacenter.id");
		}
				
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		DomainEngine domainEngine = model.getById(request.getId());
		if(domainEngine == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"DomainEngine not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), domainEngine);
	}
	
	public ResponseMessage put(RequestMessage request) {
		DomainEngine domainEngine = (DomainEngine) request.getBody();
		domainEngine.setId(request.getId());
		FieldValidationErrorMessage fvem = validateDomainEngine(domainEngine, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(domainEngine.getDatacenter().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_DOMAIN_ENGINES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		domainEngine = model.put(domainEngine);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), domainEngine);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		DomainEngine domainEngine = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(domainEngine.getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_DOMAIN_ENGINES))) {
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
			query = authUtil.appendQueryforParentOrgWithPermission(request, Permission.VIEW_DOMAIN_ENGINES, "datacenter");
		}
		List<DomainEngine> domainEngines = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), domainEngines, 
				new Count((long)domainEngines.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		DomainEngine domainEngine = (DomainEngine) request.getBody();
		domainEngine.setId(request.getId());
		DomainEngine existingLibrary = model.getById(domainEngine.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingLibrary.getDatacenter().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_DOMAIN_ENGINES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		domainEngine = model.patch(domainEngine);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), domainEngine);
	}
	
}
