package com.demo.instances;


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
import entities.EventsOfInterest;
import entities.Host;
import entities.CloudLibrary;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class InstanceService implements BaseService {

	public static final UUID SUPERUSERID = UUID.fromString("2890a526-5dfa-4f71-9b66-5681b2119427");

	private static final Logger log = LoggerFactory.getLogger(InstanceService.class);

	public static final String PATH = "/instances";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Autowired
	InstanceModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(CloudLibrary.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		CloudLibrary instance = (CloudLibrary)request.getBody();
		instance.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateInstance(instance, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(instance.getTenant().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// find host and make sure it has the capacity needed.
		Params query = new Params();
		query.setQuery("datacenter.id==" + instance.getDatacenter().getId() + "cpuAvailable=gt=" + instance.getCpuTotal() + ";ramAvailable=gt=" + instance.getRamTotal());
		query.setSort("ramAvailable");
		query.setPage("1");
		query.setPageSize("1");
		ResponseMessage response = router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.RESOURCE, null, query, null, null, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			log.error("Couldn't find available host");
			return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, 
					request.getHeaders(), "Couldn't find available host");
		}
		List<Host> hosts = (List<Host>)response.getBody();
		if(hosts.isEmpty()) {
			log.error("Couldn't find available host");
			return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, 
					request.getHeaders(), "Insufficient host capacity");
		}
		
		// patch the host with the reduced capacity
		Host host = hosts.get(0);
		Integer cpuAvailable = host.getCpuAvailable() - instance.getCpuTotal();
		Double ramAvailable = host.getRamAvailable() - instance.getRamTotal();
		Host patchHost = new Host(host.getId(),  null,  null, null, null, null, null, cpuAvailable, null, ramAvailable, null);
		
		response = router.sendAndReceive(new RequestMessage(HttpMethod.PATCH, Host.RESOURCE, host.getId(), null, null, patchHost, Location.LOCAL, Location.LOCAL));
		if(response.getStatus() != HttpStatus.OK) {
			log.error("Failed to patch host with new available metrics");
			return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, 
					request.getHeaders(), "Failed to patch host with new available metrics");
		}
		instance = model.post(instance);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), instance);
	}
	
	private FieldValidationErrorMessage validateInstance(CloudLibrary instance, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(instance.getDatacenter() == null || instance.getDatacenter().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "datacenter.id");
		}
		
		if(instance.getTenant() == null || instance.getTenant().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "tenant.id");
		}
		
		if(instance.getRamTotal() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "ramTotal");
		}
		
		if(instance.getCpuTotal() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "cpuTotal");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		CloudLibrary instance = model.getById(request.getId());
		if(instance == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Instance not found with Id: " + request.getId());
		}
		
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(instance.getTenant().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES, Permission.VIEW_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), instance);
	}
	
	public ResponseMessage put(RequestMessage request) {
				
		CloudLibrary instance = (CloudLibrary) request.getBody();
		instance.setId(request.getId());
		FieldValidationErrorMessage fvem = validateInstance(instance, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
		Organization org = orgUtil.getOrgfromOrgId(instance.getTenant().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		instance = model.put(instance);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), instance);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		CloudLibrary existingInstance = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingInstance.getTenant().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
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
			query = authUtil.appendQueryForTenantPermissions(request, "tenant");
		}
		List<CloudLibrary> instances = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), instances, 
				new Count((long)instances.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		CloudLibrary instance = (CloudLibrary) request.getBody();
		instance.setId(request.getId());
		
		CloudLibrary existingInstnace = model.getById(instance.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingInstnace.getTenant().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CLOUD_LIBRARIES))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
				
		instance = model.patch(instance);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), instance);
	}
}
