package com.demo.tasks;

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

import entities.Task;
import entities.requests.Params;

@Service
public class TaskStore implements Store<Task> {

	private static final String COLLECTION_NAME = "tasks";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Task post(Task task) {
		return ops.getMongoOperations().insert(task);
	}

	@Override
	public Task put(UUID id, Task task) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				task, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Task getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Task.class);
	}

	@Override
	public List<Task> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Task.class);
		return ops.getMongoOperations().find(mongoQuery, Task.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Task.class);
		return ops.getMongoOperations().count(mongoQuery, Task.class);
	}
	
	@Override
	public Task patch(Task task) {
		Update update = new Update();
		if(task.getProgress() != null) {
			update.set("progress", task.getProgress());
		}
		if(task.getCancelRequested() != null) {
			update.set("cancelRequested", task.getCancelRequested());
		}
		if(task.getLastUpdated() != null) {
			update.set("lastUpdated", task.getLastUpdated());
		}
		if(task.getResults() != null) {
			update.set("results", task.getResults());
		}
		if(task.getStatus() != null) {
			update.set("status", task.getStatus());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(task.getId())), update, Task.class, COLLECTION_NAME);
	}
}