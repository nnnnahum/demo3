package com.demo.events;

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

import entities.Event;
import entities.requests.Params;

@Service
public class EventStore implements Store<Event> {

	private static final String COLLECTION_NAME = "events";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Event post(Event event) {
		return ops.getMongoOperations().insert(event);
	}

	@Override
	public Event put(UUID id, Event event) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				event, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Event getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Event.class);
	}

	@Override
	public List<Event> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Event.class);
		return ops.getMongoOperations().find(mongoQuery, Event.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Event.class);
		return ops.getMongoOperations().count(mongoQuery, Event.class);
	}
	
	@Override
	public Event patch(Event event) {
		Update update = new Update();
		if(event.getResult() != null) {
			update.set("result", event.getResult());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(event.getId())), update, Event.class, COLLECTION_NAME);
	}
}