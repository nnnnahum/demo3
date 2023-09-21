package com.demo.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Location;
import entities.Tenant;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(Tenant.RESOURCE)
public class TenantController {
        
    @Autowired
    private MessageRouter router;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postTenant(
    		@RequestBody Tenant tenant) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, Tenant.RESOURCE, null, 
    			null, null, tenant, Location.MGMTAPI, Location.LOCAL));
    }

	
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putTenant(
    		@PathVariable String id,
    		@RequestBody Tenant tenant) {
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, Tenant.RESOURCE, 
    			UUID.fromString(id),  null, null, tenant, Location.MGMTAPI, Location.LOCAL));
    }
	
	@DeleteMapping("/{id}")
    public ResponseMessage deleteTenant(
    		@PathVariable String id) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, Tenant.RESOURCE, 
    			UUID.fromString(id),  null, null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getTenants(
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Tenant.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getTenant(
    		@PathVariable String id) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Tenant.RESOURCE, 
    			UUID.fromString(id),  null, null, null, Location.MGMTAPI, Location.LOCAL));
    }
}