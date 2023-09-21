package com.demo.datacenter;

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

import entities.AggregatedHostMetrics;
import entities.BaseEntity;
import entities.Datacenter;
import entities.EventsOfInterest;
import entities.Host;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class DatacenterService implements BaseService{

	public static final String PATH = "/datacenters";
	
	private static final Logger log = LoggerFactory.getLogger(DatacenterService.class);

	
	@Autowired
	MessageRouter router;
	
	@Autowired
	DatacenterModel model;
	
	@Autowired 
	AuthUtil authUtil;
	
	@Autowired 
	OrgUtil orgUtil;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Datacenter.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		
		Datacenter datacenter = (Datacenter) request.getBody();
		datacenter.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateDatacenter(datacenter, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(datacenter.getProvider().getId());
			if(org == null) {
				log.warn("Org id is not a registered provider: ", datacenter.getProvider().getId());
				return new ErrorMessage(HttpStatus.CONFLICT, 
						request.getHeaders(), 
						"provider id is not a registered provider: " 
						+ datacenter.getProvider().getId());
			}
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// make sure there isn't another Datacenter with the same name
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + datacenter.getName()
						+ ";id!=" + datacenter.getId());
		List<Datacenter> Datacenters = model.get(emailCheck);
		if(Datacenters != null && !Datacenters.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another Datacenter already exists for name: " 
					+ datacenter.getName());
		}
			
		datacenter = model.post(datacenter);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), datacenter);
	}

	private FieldValidationErrorMessage validateDatacenter(Datacenter Datacenter, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(Datacenter.getName() == null || Datacenter.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		if(Datacenter.getGeo() == null || Datacenter.getGeo().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "geo");
		}
		
		if(Datacenter.getProvider() == null || Datacenter.getProvider().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "provider.id");
		}
				
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Datacenter datacenter = model.getById(request.getId());
		
		if(datacenter == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Datacenter not found with Id: " + request.getId());
		}
		
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(datacenter.getProvider().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE, Permission.VIEW))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		Params query = new Params();
		query.setQuery("datacenter.id==" + datacenter.getId());
		ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.HOST_METRICS_RESOURCE,
				 null, query, null, null, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			log.error("Could not get aggregated host metrics for datacenter: ", datacenter.getId());
		} else {
			AggregatedHostMetrics metrics = (AggregatedHostMetrics) response.getBody();
			datacenter.setDatacenterMetrics(metrics);			
		}
		
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), datacenter);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Datacenter datacenter = (Datacenter) request.getBody();
		datacenter.setId(request.getId());
		FieldValidationErrorMessage fvem = validateDatacenter(datacenter, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(datacenter.getProvider().getId());
			if(org == null) {
				log.warn("Org id is not a registered provider: ", datacenter.getProvider().getId());
				return new ErrorMessage(HttpStatus.CONFLICT, 
						request.getHeaders(), 
						"provider id is not a registered provider: " 
						+ datacenter.getProvider().getId());
			}
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// make sure there isn't another Datacenter with the same name
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + datacenter.getName()
						+ ";id!=" + datacenter.getId());
		List<Datacenter> datacenters = model.get(emailCheck);
		if(datacenters != null && !datacenters.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another Datacenter already exists for name: " 
					+ datacenter.getName());
		}

		Datacenter existingDatacenter = model.getById(datacenter.getId());
		datacenter.setProvider(existingDatacenter.getProvider());
		datacenter = model.put(datacenter);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), datacenter);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		
		Datacenter existingDatacenter = model.getById(request.getId());
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingDatacenter.getProvider().getId());
			if(org == null) {
				log.warn("Org id is not a registered provider: ", existingDatacenter.getProvider().getId());
				return new ErrorMessage(HttpStatus.CONFLICT, 
						request.getHeaders(), 
						"provider id is not a registered provider: " 
						+ existingDatacenter.getProvider().getId());
			}
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE))) {
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

		// intentionally no permission checks so everyone can see all datacenters and avaialble capacity
		Params query = request.getQuery();
		List<Datacenter> datacenters = model.get(query);
		for(Datacenter datacenter: datacenters) {
			query = new Params();
			query.setQuery("datacenter.id==" + datacenter.getId());
			ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.HOST_METRICS_RESOURCE,
					 null, query, null, null, Location.LOCAL, Location.LOCAL));
			if(response.getStatus() != HttpStatus.OK) {
				log.error("Could not get aggregated host metrics for datacenter: ", datacenter.getId());
			} else {
				AggregatedHostMetrics metrics = (AggregatedHostMetrics) response.getBody();
				datacenter.setDatacenterMetrics(metrics);			
			
			}
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), datacenters);
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Datacenter datacenter = (Datacenter) request.getBody();
		datacenter.setId(request.getId());
		
		Datacenter existingDatacenter = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingDatacenter.getProvider().getId());
			if(org == null) {
				log.warn("Org id is not a registered provider: ", existingDatacenter.getProvider().getId());
				return new ErrorMessage(HttpStatus.CONFLICT, 
						request.getHeaders(), 
						"provider id is not a registered provider: " 
						+ existingDatacenter.getProvider().getId());
			}
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		datacenter = model.patch(datacenter);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), datacenter);
	}
}
