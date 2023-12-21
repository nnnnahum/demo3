package com.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Location;
import entities.SitesAvailable;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(SitesAvailable.RESOURCE)
public class SitesController {
    
	@Autowired
    private MessageRouter router;
    	
	@GetMapping
    public ResponseMessage getSites(
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
		return router.sendAndReceive(new RequestMessage(HttpMethod.GET, SitesAvailable.RESOURCE, 
    			null,  new Params(query, sort, page, pageSize), null, null, Location.MGMTAPI, Location.LOCAL));
    }
}
