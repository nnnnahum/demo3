package com.demo.domainengines;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.demo.db.MongoDBClient;
import com.demo.db.MongoStoreUtil;
import com.demo.db.Store;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;

import entities.Datacenter;
import entities.DomainEngine;
import entities.requests.Params;

@Service
public class DomainEngineStore implements Store<DomainEngine>{

	private static final String COLLECTION_NAME = "domainengine";

	@Autowired
	MongoDBClient ops;
	
	@Override
	public DomainEngine post(DomainEngine domainEngine) {
		return ops.getMongoOperations().insert(domainEngine);
	}

	@Override
	public DomainEngine put(UUID id, DomainEngine domainEngine) {
		// TODO Auto-generated method stub
		return ops.getMongoOperations().findAndReplace(
				query(where("_id").is(id)), 
				domainEngine, COLLECTION_NAME);
	}

	@Override
	public void deleteById(UUID id) {
		ops.getMongoOperations().remove(
				query(where("_id").is(id)), 
				COLLECTION_NAME);
	}

	@Override
	public DomainEngine getById(UUID id) {
		return ops.getMongoOperations().findById(
				id, DomainEngine.class);
	}

	@Override
	public List<DomainEngine> get(Params query) {
		Query mongoQuery = MongoStoreUtil.getQuery(query, DomainEngine.class);
		Bson lookup = Aggregates.lookup("tape_library", "_id", "domainEngine._id", "tape_library");
		
		Bson unwind = Aggregates.unwind("$tape_library");
		
		Bson group = Aggregates.group("$_id",
				Accumulators.first("datacenter", "$datacenter"),
				Accumulators.first("ip", "$ip"),
				Accumulators.first("hostname", "$hostname"),
				Accumulators.sum("drivesTotal", "$tape_library.drivesTotal"),
				Accumulators.sum("cartridgesTotal", "$tape_library.cartridgesTotal"),
				Accumulators.sum("sizeAvailable", "$tape_library.sizeAvailable"),
				Accumulators.addToSet("tape_library", "$tape_library"));

		
		List<Bson> pipeline = new ArrayList<>();
		pipeline.add(lookup);
		pipeline.add(unwind);
		pipeline.add(group);
		Document matchDoc = null;
		if(mongoQuery != null) {
			mongoQuery = MongoStoreUtil.getQuery(query, DomainEngine.class);
			matchDoc = mongoQuery.getQueryObject();
			pipeline.add(Aggregates.match(matchDoc));
		}
		
		AggregateIterable<Document> result = ops.getMongoOperations().getCollection(COLLECTION_NAME).aggregate(pipeline);
		List<DomainEngine> domainEngines = new ArrayList<DomainEngine>();
		MongoCursor<Document> it = result.cursor();
		while(it.hasNext()) {
			Document domainDoc = it.next();
			DomainEngine domainEngine = new DomainEngine();
			domainEngine.setId(domainDoc.get("_id",UUID.class));
			domainEngine.setDrivesTotal(domainDoc.getInteger("drivesTotal"));
			domainEngine.setHostname(domainDoc.getString("hostname"));
			domainEngine.setIp(domainDoc.getString("ip"));
			domainEngine.setSizeAvailable(domainDoc.getLong("sizeAvailable"));
			Document datacenterDoc = (Document) domainDoc.get("datacenter");
			if(datacenterDoc != null) {
				Datacenter datacenter = new Datacenter();
				datacenter.setId(datacenterDoc.get("_id", UUID.class));
				datacenter.setName(datacenterDoc.getString("name"));
				datacenter.setGeo(datacenterDoc.getString("geo"));
				domainEngine.setDatacenter(datacenter);
			}
			domainEngines.add(domainEngine);
		}
		
		return domainEngines;
		
//		db.domainengine.aggregate(
//				{$lookup: {from: "tape_library", localField: "_id", foreignField: "domainEngine._id", as: "tape_library" }}, 
//				{$unwind: "$tape_library"},
//		        {$project: {"_id": 1, totalSize: "$tape_library.sizeAvailable"}}, 
//				{$group: {"_id": "_id", totalSum : {$sum: "$totalSize"}}})
		
//		return ops.getMongoOperations().find(mongoQuery, DomainEngine.class);
	}

	@Override
	public long count(Params query) {
		Query mongoQuery = MongoStoreUtil.getQueryForCount(query, DomainEngine.class);
		return ops.getMongoOperations().count(mongoQuery, DomainEngine.class);
	}

	@Override
	public DomainEngine patch(DomainEngine domainEngine) {
		Update update = new Update();
		if(domainEngine.getHostname() != null && !domainEngine.getHostname().isEmpty()) {
			update.set("hostname", domainEngine.getHostname());
		}
		if(domainEngine.getIp() != null && !domainEngine.getIp().isEmpty()) {
			update.set("ip", domainEngine.getHostname());
		}
		return ops.getMongoOperations().findAndModify(query(where("_id").is(domainEngine.getId())), update, DomainEngine.class, COLLECTION_NAME);
	}

}
