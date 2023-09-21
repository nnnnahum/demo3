package com.demo.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Datacenter;
import entities.Location;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(Datacenter.RESOURCE)
public class DatacenterController {
    
	@Autowired
    private MessageRouter router;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postDatacenter(
    		@RequestHeader(name = "auth") String authId,
    		@RequestBody Datacenter datacenter) {
    	Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, Datacenter.RESOURCE, null, 
    			null, headers, datacenter, Location.MGMTAPI, Location.LOCAL));
    }

	
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putDatacenter(
    		@PathVariable String id,
    		@RequestHeader(name = "auth") String authId,
    		@RequestBody Datacenter datacenter) {
    	Params headers = new Params();
    	headers.put("authId", authId);
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, Datacenter.RESOURCE, 
    			UUID.fromString(id),  null, headers, datacenter, Location.MGMTAPI, Location.LOCAL));
    }
	
	@DeleteMapping("/{id}")
    public ResponseMessage deleteDatacenter(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, Datacenter.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getDatacenters(
    		@RequestHeader(name = "auth") String authId,
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Datacenter.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getDatacenter(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Datacenter.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
}
