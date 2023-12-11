package com.demo.resellers;

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

import entities.Reseller;
import entities.requests.Params;

@Service
public class ResellerStore implements Store<Reseller> {

	private static final String COLLECTION_NAME = "resellers";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Reseller post(Reseller reseller) {
		return ops.getMongoOperations().insert(reseller);
	}

	@Override
	public Reseller put(UUID id, Reseller reseller) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				reseller, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Reseller getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Reseller.class);
	}

	@Override
	public List<Reseller> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Reseller.class);
		return ops.getMongoOperations().find(mongoQuery, Reseller.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Reseller.class);
		return ops.getMongoOperations().count(mongoQuery, Reseller.class);
	}
	
	@Override
	public Reseller patch(Reseller reseller) {
		Update update = new Update();
		if(reseller.getName() != null && !reseller.getName().isEmpty()) {
			update.set("name", reseller.getName());
		}
		if(reseller.getPerms() != null) {
			update.set("perms", reseller.getPerms());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(reseller.getId())), update, Reseller.class, COLLECTION_NAME);
	}
}