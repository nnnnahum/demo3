package com.demo.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.requests.ErrorMessage;
import entities.requests.ErrorMessageException;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Service
public class MessageRouter {
	private static final Logger log = LoggerFactory.getLogger(MessageRouter.class);
	
	private Map<String, BaseService> servicesAvailable = new HashMap<>();
	
	private Map<EventsOfInterest, List<BaseService>> eventsOfInterest = new HashMap<>();
	
	public void registerRoute(String resource, BaseService service) {
		servicesAvailable.put(resource, service);
	}
	
	public void registerEventsOfInterest(EventsOfInterest eventName, BaseService service) {
		if(eventsOfInterest.get(eventName) == null) {
			eventsOfInterest.put(eventName, new ArrayList<>());
		}
		eventsOfInterest.get(eventName).add(service);
	}
	
	public ResponseMessage sendAndReceive(RequestMessage request) {
		
		BaseService service = servicesAvailable.get(request.getResource());
		switch(request.getMethod()) {
		case DELETE:
			return service.delete(request);
		case GET:
			try {
				return service.get(request); 
			} catch(ErrorMessageException e) {
				log.error("Exception: ", e);
				return new ErrorMessage(e);
			}
		case PATCH:
			return service.patch(request);
		case POST:
			return service.post(request);
		case PUT:
			return service.put(request);
		default:
			return new ResponseMessage();
		}
	}
	
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		if(eventsOfInterest.containsKey(eventName)) {
			for(BaseService service : eventsOfInterest.get(eventName)) {
				service.notify(eventName, entity);
			}
		}
	}
}