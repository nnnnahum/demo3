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

import entities.Role;
import entities.requests.Params;

@Service
public class RoleStore implements Store<Role> {

	private static final String COLLECTION_NAME = "roles";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Role post(Role role) {
		return ops.getMongoOperations().insert(role);
	}

	@Override
	public Role put(UUID id, Role role) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				role, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Role getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Role.class);
	}

	@Override
	public List<Role> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Role.class);
		return ops.getMongoOperations().find(mongoQuery, Role.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Role.class);
		return ops.getMongoOperations().count(mongoQuery, Role.class);
	}

	@Override
	public Role patch(Role role) {
		Update update = new Update();
		if(role.getName() != null && !role.getName().isEmpty()) {
			update.set("name", role.getName());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(role.getId())), update, Role.class, COLLECTION_NAME);
	}
}