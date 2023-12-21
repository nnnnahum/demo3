package com.demo.tapelibrary;

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

import entities.TapeLibrary;
import entities.requests.Params;

@Service
public class TapeLibraryStore implements Store<TapeLibrary>{

	private static final String COLLECTION_NAME = "tape_libraries";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public TapeLibrary post(TapeLibrary tapeLibrary) {
		return ops.getMongoOperations().insert(tapeLibrary);
	}

	@Override
	public TapeLibrary put(UUID id, TapeLibrary tapeLibrary) {
		// TODO Auto-generated method stub
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				tapeLibrary, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public TapeLibrary getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, TapeLibrary.class);
	}

	@Override
	public List<TapeLibrary> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, TapeLibrary.class);
		return ops.getMongoOperations().find(mongoQuery, TapeLibrary.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, TapeLibrary.class);
		return ops.getMongoOperations().count(mongoQuery, TapeLibrary.class);
	}

	@Override
	public TapeLibrary patch(TapeLibrary tapeLibrary) {
		Update update = new Update();
		if(tapeLibrary.getHostname() != null && !tapeLibrary.getHostname().isEmpty()) {
			update.set("hostname", tapeLibrary.getHostname());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(tapeLibrary.getId())), update, TapeLibrary.class, COLLECTION_NAME);
	}

}
