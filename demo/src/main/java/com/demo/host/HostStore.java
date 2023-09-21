package com.demo.host;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.demo.db.MetricsStore;
import com.demo.db.MongoDBClient;
import com.demo.db.MongoStoreUtil;
import com.demo.db.Store;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;

import entities.AggregatedHostMetrics;
import entities.Host;
import entities.requests.Params;

@Service
public class HostStore implements Store<Host>, MetricsStore<AggregatedHostMetrics> {

	private static final String COLLECTION_NAME = "hosts";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public Host post(Host host) {
		return ops.getMongoOperations().insert(host);
	}

	@Override
	public Host put(UUID id, Host host) {
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				host, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public Host getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, Host.class);
	}

	@Override
	public List<Host> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, Host.class);
		return ops.getMongoOperations().find(mongoQuery, Host.class);
	}
	
	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, Host.class);
		return ops.getMongoOperations().count(mongoQuery, Host.class);
	}
	
	@Override
	public Host patch(Host host) {
		Update update = new Update();
		if(host.getIp() != null && !host.getIp().isEmpty()) {
			update.set("ip", host.getIp());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(host.getId())), update, Host.class, COLLECTION_NAME);
	}

	@Override
	public AggregatedHostMetrics getMetrics(Params query) {
		
		List<Document> pipeline = new ArrayList<>();
		Document matchOnQueryParams = new Document("$match", MongoStoreUtil.getQuery(query, Host.class).getQueryObject());
		
		Document agg = new Document().append("$group", new Document()
				.append("_id", 1)
				.append("totalHosts", new Document("$sum", 1))
				.append("totalCPU", new Document("$sum", "$cpuTotal"))
				.append("totalRAM", new Document("$sum", "$ramTotal"))
				.append("maxCPUInHost", new Document("$max", "$cpuAvailable"))
				.append("maxRAMInHost", new Document("$max", "$ramAvailable")));
		pipeline.add(matchOnQueryParams);
		pipeline.add(agg);
		AggregateIterable<Document> results = ops.getMongoOperations().getCollection(COLLECTION_NAME)
				.aggregate(pipeline);
		
		//[{$match: {"datacenter._id" : Binary(Buffer.from("1b448dcfdaf3762c840f5e2326be34bc", "hex"), 3)}}, 
		// {$group: {
		// "_id" : 1, 
		// totalHosts :{$sum:1}, 
		// totalCPU: {$sum:"$cpuTotal"}, 
		// totalRAM: {$sum: "$ramTotal"}, 
		// maxCPUInHost : {$max: "$cpuAvailable"}, 
		// maxRAMInHost: {$max:"$ramAvailable"}}}]
		MongoCursor<Document> iterator = results.iterator();
		if(iterator.hasNext()) {
		Document result = iterator.next();
			return new AggregatedHostMetrics(
					result.getInteger("totalHosts"), 
					result.getInteger("totalCPU"),
					result.getDouble("totalRAM"),
					result.getInteger("maxCPUInHost"), 
					result.getDouble("maxRAMInHost"));
		} else {
			return new AggregatedHostMetrics();
		}
	}
}