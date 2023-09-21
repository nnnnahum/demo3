package com.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.db.MongoDBClient;

import entities.Health;

@RestController
public class HealthCheck {
	
	@Autowired
	MongoDBClient ops;
	
	@RequestMapping(value = "/health", method = RequestMethod.GET, produces = "application/json")
	public Health firstPage() {
		ops.getMongoOperations();
		return new Health("OK");
	}

}
