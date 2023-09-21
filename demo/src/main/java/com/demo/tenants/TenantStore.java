package com.demo.tenants;

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

import entities.Tenant;
import entities.requests.Params;

@Service
public class TenantStore implements Store<Tenant> {

	private static final String COLLECTION_NAME = "tenants";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Tenant post(Tenant tenant) {
		return ops.getMongoOperations().insert(tenant);
	}

	@Override
	public Tenant put(UUID id, Tenant tenant) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				tenant, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Tenant getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Tenant.class);
	}

	@Override
	public List<Tenant> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Tenant.class);
		return ops.getMongoOperations().find(mongoQuery, Tenant.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Tenant.class);
		return ops.getMongoOperations().count(mongoQuery, Tenant.class);
	}
	
	@Override
	public Tenant patch(Tenant tenant) {
		Update update = new Update();
		if(tenant.getName() != null && !tenant.getName().isEmpty()) {
			update.set("name", tenant.getName());
		}
		if(tenant.getPerms() != null) {
			update.set("perms", tenant.getPerms());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(tenant.getId())), update, Tenant.class, COLLECTION_NAME);
	}
}