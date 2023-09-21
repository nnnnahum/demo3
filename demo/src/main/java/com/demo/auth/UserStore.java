package com.demo.auth;


import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.demo.db.MongoDBClient;
import com.demo.db.MongoStoreUtil;
import com.demo.db.Store;

import entities.User;
import entities.requests.Params;

@Service
public class UserStore implements Store<User> {

	private static final String COLLECTION_NAME = "users";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public User post(User user) {
		return ops.getMongoOperations().insert(user);
	}

	@Override
	public User put(UUID id, User user) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				user, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public User getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, User.class);
	}

	@Override
	public List<User> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, User.class);
		return ops.getMongoOperations().find(mongoQuery, User.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, User.class);
		return ops.getMongoOperations().count(mongoQuery, User.class);
	}
	
	@Override
	public User patch(User user) {
		Update update = new Update();
		if(user.getFirstName() != null && !user.getFirstName().isEmpty()) {
			update.set("firstName", user.getFirstName());
		}
		if(user.getLastName() != null && !user.getLastName().isEmpty()) {
			update.set("lastName", user.getLastName());
		}
		if(user.getEmailAddress() != null && !user.getEmailAddress().isEmpty()) {
			update.set("emailAddress", user.getEmailAddress());
		}
		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			update.set("password", user.getPassword());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(user.getId())), update, User.class, COLLECTION_NAME);
	}
}