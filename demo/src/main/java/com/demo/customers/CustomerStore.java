package com.demo.customers;

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

import entities.Customer;
import entities.requests.Params;

@Service
public class CustomerStore implements Store<Customer> {

	private static final String COLLECTION_NAME = "customers";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Customer post(Customer customer) {
		return ops.getMongoOperations().insert(customer);
	}

	@Override
	public Customer put(UUID id, Customer customer) {
	    
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				customer, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Customer getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Customer.class);
	}

	@Override
	public List<Customer> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Customer.class);
		return ops.getMongoOperations().find(mongoQuery, Customer.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Customer.class);
		return ops.getMongoOperations().count(mongoQuery, Customer.class);
	}
	
	@Override
	public Customer patch(Customer customer) {
		Update update = new Update();
		if(customer.getName() != null && !customer.getName().isEmpty()) {
			update.set("name", customer.getName());
		}
		if(customer.getPerms() != null) {
			update.set("perms", customer.getPerms());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(customer.getId())), update, Customer.class, COLLECTION_NAME);
	}
}