package com.demo.datacenter;
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

import entities.Datacenter;
import entities.requests.Params;

@Service
public class DatacenterStore implements Store<Datacenter> {
	
	private static final String COLLECTION_NAME = "datacenters";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Datacenter post(Datacenter datacenter) {
		return ops.getMongoOperations().insert(datacenter);
	}

	@Override
	public Datacenter put(UUID id, Datacenter datacenter) {
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				datacenter, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Datacenter getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Datacenter.class);
	}

	@Override
	public List<Datacenter> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Datacenter.class);
		return ops.getMongoOperations().find(mongoQuery, Datacenter.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Datacenter.class);
		return ops.getMongoOperations().count(mongoQuery, Datacenter.class);
	}

	@Override
	public Datacenter patch(Datacenter datacenter) {
		Update update = new Update();
		if(datacenter.getName() != null && !datacenter.getName().isEmpty()) {
			update.set("name", datacenter.getName());
		}
		if(datacenter.getPerms() != null) {
			update.set("perms", datacenter.getPerms());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(datacenter.getId())), update, Datacenter.class, COLLECTION_NAME);
	}
}
