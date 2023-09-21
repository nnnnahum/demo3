package com.demo.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClientException;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.internal.MongoClientImpl;

@Service
public class MongoDBClient {
	private static final Logger log = LoggerFactory.getLogger(MongoDBClient.class);
	
	private MongoClientImpl client; 
	
	public MongoTemplate getMongoOperations() {
		if(client == null) {
			client = new MongoClientImpl(MongoClientSettings.builder().build(), MongoDriverInformation.builder().build());
		}
		try {
			client.getDatabase("myDB");
		} catch (MongoClientException e) {
			log.error("Mongo client exception: ", e);
			client = new MongoClientImpl(MongoClientSettings.builder().build(), MongoDriverInformation.builder().build());
		}
		MongoTemplate ops = new MongoTemplate(client, "myDB");
		return ops;
	}
}
