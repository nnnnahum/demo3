package com.demo.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
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
import entities.Task;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(Task.RESOURCE)
public class TaskController {

    @Autowired
    private MessageRouter router;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postTask(
    		@RequestHeader(name = "auth") String authId,
    		@RequestBody Task task) {
    	
    	Params headers = new Params();
    	headers.put("authId", authId);
		return router.sendAndReceive(new RequestMessage(HttpMethod.POST, Task.RESOURCE, null, 
    			null, headers , task, Location.MGMTAPI, Location.LOCAL));
    }

	
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putTask(
    		@PathVariable String id,
    		@RequestHeader(name = "auth") String authId,
    		@RequestBody Task task) {
    	Params headers = new Params();
    	headers.put("authId", authId);
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, Task.RESOURCE, 
    			UUID.fromString(id),  null, headers, task, Location.MGMTAPI, Location.LOCAL));
    }
	
    @RequestMapping(method = RequestMethod.POST, path = "/{id}/cancel")
    public ResponseMessage cancelTask(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, Task.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getTasks(
    		@RequestHeader(name = "auth") String authId,
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Task.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getTask(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Task.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
}