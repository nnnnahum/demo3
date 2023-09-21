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

import entities.Session;
import entities.requests.Params;

@Service
public class SessionStore implements Store<Session> {

	private static final String COLLECTION_NAME = "sessions";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Session post(Session session) {
		return ops.getMongoOperations().insert(session);
	}

	@Override
	public Session put(UUID id, Session session) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				session, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Session getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Session.class);
	}

	@Override
	public List<Session> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Session.class);
		return ops.getMongoOperations().find(mongoQuery, Session.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Session.class);
		return ops.getMongoOperations().count(mongoQuery, Session.class);
	}

	@Override
	public Session patch(Session session) {
		Update update = new Update();
		if(session.getExpiresAt() != null) {
			update.set("expiresAt", session.getExpiresAt());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(session.getId())), update, Session.class, COLLECTION_NAME);
	}
}