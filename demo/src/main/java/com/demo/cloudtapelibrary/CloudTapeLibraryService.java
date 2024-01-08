package com.demo.cloudtapelibrary;

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
import entities.CloudTapeLibrary;
import entities.EventsOfInterest;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.TapeLibrary;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.ErrorMessageException;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class CloudTapeLibraryService implements BaseService {

	private static final Logger log = LoggerFactory.getLogger(CloudTapeLibraryService.class);
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Autowired
	CloudTapeLibraryModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(CloudTapeLibrary.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		CloudTapeLibrary cloudTapeLibrary = (CloudTapeLibrary)request.getBody();
		cloudTapeLibrary.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateInstance(cloudTapeLibrary, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(cloudTapeLibrary.getCustomer().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		Params query = new Params();
		query.setQuery("datacenter.id==" + cloudTapeLibrary.getDatacenter().getId() + ";sizeAvailable=gt=" + cloudTapeLibrary.getSize());
		query.setSort("sizeAvailable");
		query.setPage("1");
		query.setPageSize("1");
		ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.GET, TapeLibrary.RESOURCE, null, query, null, null, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			log.error("Couldn't find available tape library");
			return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, 
					request.getHeaders(), "Couldn't find available tape library");
		}
		List<TapeLibrary> tapeLibraries = (List<TapeLibrary>)response.getBody();
		if(tapeLibraries.isEmpty()) {
			log.error("Couldn't find available tape library");
			return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, 
					request.getHeaders(), "Insufficient tape library capacity");
		}
		
		//TODO maybe patch the tape library if it is 'thick' provisioned?
		// Ideally the available capacity comes from black pearl.
		cloudTapeLibrary = model.post(cloudTapeLibrary);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), cloudTapeLibrary);
	}
	
	private FieldValidationErrorMessage validateInstance(CloudTapeLibrary cloudTapeLibrary, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(cloudTapeLibrary.getDatacenter() == null || cloudTapeLibrary.getDatacenter().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "datacenter.id");
		}
		
		if(cloudTapeLibrary.getCustomer() == null || cloudTapeLibrary.getCustomer().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "customer.id");
		}
				
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		CloudTapeLibrary cloudTapeLibrary = model.getById(request.getId());
		if(cloudTapeLibrary == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Instance not found with Id: " + request.getId());
		}
		
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(cloudTapeLibrary.getCustomer().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES, Permission.VIEW_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), cloudTapeLibrary);
	}
	
	public ResponseMessage put(RequestMessage request) {
				
		CloudTapeLibrary cloudTapeLibrary = (CloudTapeLibrary) request.getBody();
		cloudTapeLibrary.setId(request.getId());
		FieldValidationErrorMessage fvem = validateInstance(cloudTapeLibrary, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
		Organization org = orgUtil.getOrgfromOrgId(cloudTapeLibrary.getCustomer().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		cloudTapeLibrary = model.put(cloudTapeLibrary);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), cloudTapeLibrary);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		CloudTapeLibrary existingInstance = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingInstance.getCustomer().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
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
			query = authUtil.appendQueryForTenantPermissions(request, "customer");
		}
		List<CloudTapeLibrary> cloudTapeLibrarys = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), cloudTapeLibrarys, 
				new Count((long)cloudTapeLibrarys.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		CloudTapeLibrary cloudTapeLibrary = (CloudTapeLibrary) request.getBody();
		cloudTapeLibrary.setId(request.getId());
		
		CloudTapeLibrary existingInstnace = model.getById(cloudTapeLibrary.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingInstnace.getCustomer().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
				
		cloudTapeLibrary = model.patch(cloudTapeLibrary);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), cloudTapeLibrary);
	}
}
