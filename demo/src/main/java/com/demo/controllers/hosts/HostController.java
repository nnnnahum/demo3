package com.demo.controllers.hosts;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Host;
import entities.Location;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(Host.RESOURCE)
public class HostController {

	@Autowired
    private MessageRouter router;
	
	@GetMapping
    public ResponseMessage getHostForDatacenter(
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getHost(
    		@PathVariable String id) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.RESOURCE, 
    			UUID.fromString(id),  null, null, null, Location.MGMTAPI, Location.LOCAL));
    }
}
