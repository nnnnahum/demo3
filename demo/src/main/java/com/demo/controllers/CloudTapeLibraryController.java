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

import entities.Location;
import entities.CloudTapeLibrary;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(CloudTapeLibrary.RESOURCE)
public class CloudTapeLibraryController {
        
    @Autowired
    private MessageRouter router;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postCustomer(
    		@RequestHeader(name = "auth") String authId,
    		@RequestBody CloudTapeLibrary cloudTapeLibrary) {
    	Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, CloudTapeLibrary.RESOURCE, null, 
    			null, headers, cloudTapeLibrary, Location.MGMTAPI, Location.LOCAL));
    }

	
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putCustomer(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id,
    		@RequestBody CloudTapeLibrary clientTapeLibrary) {
    	Params headers = new Params();
    	headers.put("authId", authId);
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, CloudTapeLibrary.RESOURCE, 
    			UUID.fromString(id),  null, headers, clientTapeLibrary, Location.MGMTAPI, Location.LOCAL));
    }
	
	@DeleteMapping("/{id}")
    public ResponseMessage deleteCustomer(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, CloudTapeLibrary.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getCustomers(
    		@RequestHeader(name = "auth") String authId,
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, CloudTapeLibrary.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getCustomer(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, CloudTapeLibrary.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
}