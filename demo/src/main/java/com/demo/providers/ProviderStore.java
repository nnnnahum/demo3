package com.demo.providers;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.UUID;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.demo.db.MongoDBClient;
import com.demo.db.MongoStoreUtil;
import com.demo.db.Store;

import entities.Host;
import entities.HostingProvider;
import entities.requests.Params;

@Service
public class ProviderStore implements Store<HostingProvider> {

	private static final String COLLECTION_NAME = "providers";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public HostingProvider post(HostingProvider provider) {
		return ops.getMongoOperations().insert(provider);
	}

	@Override
	public HostingProvider put(UUID id, HostingProvider provider) {
	    
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
	public HostingProvider getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, HostingProvider.class);
	}

	@Override
	public List<HostingProvider> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, HostingProvider.class);
//		mongoQuery = new Query();
//		mongoQuery.addCriteria(Criteria.where("perms").elemMatch(Criteria.where("permission").is("VIEW_PROVIDERS").and("roleId").exists(true)));
		return ops.getMongoOperations().find(mongoQuery, HostingProvider.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, HostingProvider.class);
		return ops.getMongoOperations().count(mongoQuery, HostingProvider.class);
	}
	
	@Override
	public HostingProvider patch(HostingProvider provider) {
		Update update = new Update();
		if(provider.getName() != null && !provider.getName().isEmpty()) {
			update.set("name", provider.getName());
		}
		if(provider.getPerms() != null) {
			update.set("perms", provider.getPerms());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(provider.getId())), update, HostingProvider.class, COLLECTION_NAME);
	}
}