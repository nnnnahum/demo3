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

import entities.Instance;
import entities.requests.Params;

@Service
public class InstanceStore implements Store<Instance>{

	private static final String COLLECTION_NAME = "instances";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Instance post(Instance instance) {
		return ops.getMongoOperations().insert(instance);
	}

	@Override
	public Instance put(UUID id, Instance instance) {
	    
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
	public Instance getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Instance.class);
	}

	@Override
	public List<Instance> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Instance.class);
		return ops.getMongoOperations().find(mongoQuery, Instance.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Instance.class);
		return ops.getMongoOperations().count(mongoQuery, Instance.class);
	}
	
	@Override
	public Instance patch(Instance instance) {
		Update update = new Update();
		if(instance.getHost() != null && instance.getHost().getId() != null) {
			update.set("host", instance.getHost());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(instance.getId())), update, Instance.class, COLLECTION_NAME);
	}
}