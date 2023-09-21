package com.demo.providers;

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

import entities.Host;
import entities.Provider;
import entities.requests.Params;

@Service
public class ProviderStore implements Store<Provider> {

	private static final String COLLECTION_NAME = "providers";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Provider post(Provider provider) {
		return ops.getMongoOperations().insert(provider);
	}

	@Override
	public Provider put(UUID id, Provider provider) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				provider, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Provider getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Provider.class);
	}

	@Override
	public List<Provider> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Provider.class);
		return ops.getMongoOperations().find(mongoQuery, Provider.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Provider.class);
		return ops.getMongoOperations().count(mongoQuery, Provider.class);
	}
	
	@Override
	public Provider patch(Provider provider) {
		Update update = new Update();
		if(provider.getName() != null && !provider.getName().isEmpty()) {
			update.set("name", provider.getName());
		}
		if(provider.getPerms() != null) {
			update.set("perms", provider.getPerms());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(provider.getId())), update, Provider.class, COLLECTION_NAME);
	}
}