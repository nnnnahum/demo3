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
import entities.Reseller;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(Reseller.RESOURCE)
public class ResellerController {
        
    @Autowired
    private MessageRouter router;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postReseller(
    		@RequestBody Reseller tenant) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, Reseller.RESOURCE, null, 
    			null, null, tenant, Location.MGMTAPI, Location.LOCAL));
    }

	
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putReseller(
    		@PathVariable String id,
    		@RequestBody Reseller tenant) {
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, Reseller.RESOURCE, 
    			UUID.fromString(id),  null, null, tenant, Location.MGMTAPI, Location.LOCAL));
    }
	
	@DeleteMapping("/{id}")
    public ResponseMessage deleteReseller(
    		@PathVariable String id) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, Reseller.RESOURCE, 
    			UUID.fromString(id),  null, null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getResellers(
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Reseller.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getReseller(
    		@PathVariable String id) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Reseller.RESOURCE, 
    			UUID.fromString(id),  null, null, null, Location.MGMTAPI, Location.LOCAL));
    }
}