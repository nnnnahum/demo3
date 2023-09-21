package com.demo.host;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;

import entities.BaseEntity;
import entities.Datacenter;
import entities.EventsOfInterest;
import entities.Host;
import entities.Location;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class HostService implements BaseService{

	public static final String PATH = "/hosts";
	public static final String Path = "/hostmetrics";
	
	private static final Logger log = LoggerFactory.getLogger(HostService.class);
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	HostModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Host.RESOURCE, this);
		router.registerRoute(Host.HOST_METRICS_RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		Host host = (Host) request.getBody();
		host.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateHost(host, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		host = model.post(host);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), host);
	}
	
	private FieldValidationErrorMessage validateHost(Host host, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(host.getIp() == null || host.getIp().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "ip");
		}
		
		if(host.getDatacenter() == null || host.getDatacenter().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "datacenter.id");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Host host = model.getById(request.getId());
		if(host == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Host not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), host);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Host host = (Host) request.getBody();
		host.setId(request.getId());
		FieldValidationErrorMessage fvem = validateHost(host, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		host = model.put(host);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), host);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		model.delete(request.getId());
		return new ResponseMessage(HttpStatus.NO_CONTENT, request.getHeaders(), null);
	}

	public ResponseMessage get(RequestMessage request) {
		switch(request.getResource()) {
		case Host.RESOURCE:
			return getHosts(request);
		case Host.HOST_METRICS_RESOURCE:
			return getHostMetrics(request);
		default:
			log.error("Error finding path: ", request.getResource());	
			return null;
		}
	}

	private ResponseMessage getHostMetrics(RequestMessage request) {
		// if the request is external, add datacenter with providers that the user has access to. 
		Params query = request.getQuery();
		if(request.getSource() != Location.LOCAL) {
			// get datacenters as if it is an external request with the headers 
			ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.GET, Datacenter.RESOURCE, 
					null, null, request.getHeaders(), null, Location.MGMTAPI, Location.LOCAL));
			if(response.getStatus() != HttpStatus.OK) {
				log.error("Error fetching Datacenters: ", response);
				return null;
			}
			List<Datacenter> datacenters = (List<Datacenter>) response.getBody();
			if (datacenters.isEmpty()){
				log.error("User doesn't have access to any datacenters.");
				return null;
			}
			StringBuilder builder = new StringBuilder();
			builder.append(datacenters.stream().map(x -> "datacenter.id==" + x.getId())
					.collect(Collectors.joining(", ")));
			if(query.getQuery() == null) query.setQuery(builder.toString());
			else {
				query.setQuery("(" + query.getQuery() + ");(" + builder.toString() + ")");
			}

		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), model.getMetrics(query));
	}

	private ResponseMessage getHosts(RequestMessage request) {
		if (request.getId() != null) {
			return getById(request);
		}
		List<Host> hosts = model.get(request.getQuery());
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), hosts);

	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Host host = (Host) request.getBody();
		host.setId(request.getId());
		host = model.patch(host);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), host);
	}
}
