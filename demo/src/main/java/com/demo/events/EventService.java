package com.demo.events;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import entities.Event;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.ErrorMessageException;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class EventService implements BaseService{

	private static final Logger log = LoggerFactory.getLogger(EventService.class);

	public static final String PATH = "/events";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	EventModel model;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Override
	public void start() {
		router.registerRoute(Event.RESOURCE, this);
	}
	
	public ResponseMessage post(RequestMessage request) {
		Event event = (Event)request.getBody();
		event.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateEvent(event, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(event.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_EVENTS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		event = model.post(event);
		
		//TODO in the future pull notification rules and blast whatever needs to go out.
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), event);
	}
	
	private FieldValidationErrorMessage validateEvent(Event event, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(event.getName() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		
		if(event.getOrg() == null || event.getOrg().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "parentOrganization.id");
		}
		
		if(event.getCreated() == null) {
			event.setCreated(new Date());
		}

		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Event event = model.getById(request.getId());
		if(event == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Event not found with Id: " + request.getId());
		}
		
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(event.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_EVENTS, Permission.VIEW_EVENTS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), event);
	}
	
	public ResponseMessage put(RequestMessage request) {
				
		Event event = (Event) request.getBody();
		event.setId(request.getId());
		FieldValidationErrorMessage fvem = validateEvent(event, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
		Organization org = orgUtil.getOrgfromOrgId(event.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_EVENTS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}

		event = model.put(event);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), event);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		Event existingEvent = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingEvent.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_EVENTS))) {
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
		List<Event> events = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), events, 
				new Count((long)events.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Event event = (Event) request.getBody();
		event.setId(request.getId());
		
		Event existingEvent = model.getById(event.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingEvent.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_EVENTS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
			
		event = model.patch(event);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), event);
	}
}
