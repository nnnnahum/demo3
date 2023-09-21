package com.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Location;
import entities.LoginRequest;
import entities.Session;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(LoginRequest.RESOURCE)
public class LoginController {

        
    @Autowired
    private MessageRouter router;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postUser(
    		@RequestBody LoginRequest loginRequest) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, Session.RESOURCE, null, 
    			null, null, loginRequest, Location.MGMTAPI, Location.LOCAL));
    }
}