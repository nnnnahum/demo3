package com.demo.instances;

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

import entities.CloudLibrary;
import entities.requests.Params;

@Service
public class InstanceStore implements Store<CloudLibrary>{

	private static final String COLLECTION_NAME = "instances";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public CloudLibrary post(CloudLibrary instance) {
		return ops.getMongoOperations().insert(instance);
	}

	@Override
	public CloudLibrary put(UUID id, CloudLibrary instance) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				instance, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public CloudLibrary getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, CloudLibrary.class);
	}

	@Override
	public List<CloudLibrary> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, CloudLibrary.class);
		return ops.getMongoOperations().find(mongoQuery, CloudLibrary.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, CloudLibrary.class);
		return ops.getMongoOperations().count(mongoQuery, CloudLibrary.class);
	}
	
	@Override
	public CloudLibrary patch(CloudLibrary instance) {
		Update update = new Update();
		if(instance.getHost() != null && instance.getHost().getId() != null) {
			update.set("host", instance.getHost());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(instance.getId())), update, CloudLibrary.class, COLLECTION_NAME);
	}
}