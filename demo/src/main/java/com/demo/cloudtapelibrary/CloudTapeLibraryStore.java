package com.demo.cloudtapelibrary;

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

import entities.CloudTapeLibrary;
import entities.requests.Params;

@Service
public class CloudTapeLibraryStore implements Store<CloudTapeLibrary>{

	private static final String COLLECTION_NAME = "cloud_tape_libraries";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public CloudTapeLibrary post(CloudTapeLibrary cloudTapeLibrary) {
		return ops.getMongoOperations().insert(cloudTapeLibrary);
	}

	@Override
	public CloudTapeLibrary put(UUID id, CloudTapeLibrary cloudTapeLibrary) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				cloudTapeLibrary, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public CloudTapeLibrary getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, CloudTapeLibrary.class);
	}

	@Override
	public List<CloudTapeLibrary> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, CloudTapeLibrary.class);
		return ops.getMongoOperations().find(mongoQuery, CloudTapeLibrary.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, CloudTapeLibrary.class);
		return ops.getMongoOperations().count(mongoQuery, CloudTapeLibrary.class);
	}
	
	@Override
	public CloudTapeLibrary patch(CloudTapeLibrary cloudTapeLibrary) {
		Update update = new Update();
		if(cloudTapeLibrary.getName() != null && !cloudTapeLibrary.getName().isEmpty()) {
			update.set("host", cloudTapeLibrary.getName());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(cloudTapeLibrary.getId())), update, CloudTapeLibrary.class, COLLECTION_NAME);
	}
}