package com.demo.tapelibrary;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
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
import entities.TapeLibrary;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class TapeLibraryService implements BaseService{

	public static final String PATH = "/tapeLibraries";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Autowired
	TapeLibraryModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(TapeLibrary.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		TapeLibrary tapeLibrary = (TapeLibrary) request.getBody();
		tapeLibrary.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateTapeLibrary(tapeLibrary, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(tapeLibrary.getDatacenter().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TAPE_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		tapeLibrary = model.post(tapeLibrary);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), tapeLibrary);
	}
	
	private FieldValidationErrorMessage validateTapeLibrary(TapeLibrary tapeLibrary, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(tapeLibrary.getIp() == null || tapeLibrary.getIp().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "ip");
		}
		
		if(tapeLibrary.getDatacenter() == null || tapeLibrary.getDatacenter().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "datacenter.id");
		}
		
		if(tapeLibrary.getDrivesTotal() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "drivesTotal");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		TapeLibrary tapeLibrary = model.getById(request.getId());
		if(tapeLibrary == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"TapeLibrary not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tapeLibrary);
	}
	
	public ResponseMessage put(RequestMessage request) {
		TapeLibrary tapeLibrary = (TapeLibrary) request.getBody();
		tapeLibrary.setId(request.getId());
		FieldValidationErrorMessage fvem = validateTapeLibrary(tapeLibrary, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(tapeLibrary.getDatacenter().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TAPE_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		tapeLibrary = model.put(tapeLibrary);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tapeLibrary);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		TapeLibrary tapeLibrary = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(tapeLibrary.getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TAPE_LIBRARIES))) {
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
			query = authUtil.appendQueryForOrgWithPermission(request, Permission.VIEW_TAPE_LIBRARIES);
		}
		List<TapeLibrary> tapeLibrarys = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tapeLibrarys, 
				new Count((long)tapeLibrarys.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		TapeLibrary tapeLibrary = (TapeLibrary) request.getBody();
		tapeLibrary.setId(request.getId());
		TapeLibrary existingLibrary = model.getById(tapeLibrary.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingLibrary.getDatacenter().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TAPE_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		tapeLibrary = model.patch(tapeLibrary);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tapeLibrary);
	}
	
}
