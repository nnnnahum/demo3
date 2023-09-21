package com.demo.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.github.rutledgepaulv.qbuilders.builders.GeneralQueryBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.qbuilders.visitors.MongoVisitor;
import com.github.rutledgepaulv.rqe.pipes.QueryConversionPipeline;

import entities.requests.Params;


public class MongoStoreUtil {

	public static Query getQuery(Params params, Class c) {
		
	    Query query = getQueryForCount(params, c);
	    		
	    if(params.getSort() != null && !params.getSort().isEmpty()) {
	    	
	    	Direction direction = Direction.ASC;
	    	String property = params.getSort();
	    	if(params.getSort().startsWith("-")) {
	    		direction = direction.DESC;
	    		property = property.substring(1);
	    	}
			query.with(Sort.by(direction, property));
	    }
	    
	    int page = 1;
	    int pageSize = 10;
	    if(params.getPage() != null && !params.getPage().isEmpty()
	    		&& params.getPageSize() != null 
	    		&& !params.getPageSize().isEmpty()) {
	    	page = Integer.parseInt(params.getPage());
	    	pageSize = Integer.parseInt(params.getPageSize());
	    }
	    query.limit(pageSize);
	    query.skip((page - 1) * pageSize);
	    
		return query;
	}
	
	public static Query getQueryForCount(Params params, Class c) {
		QueryConversionPipeline pipeline = 
				QueryConversionPipeline.defaultPipeline();
		
	    Query query = new Query();
		if(params.getQuery() != null && !params.getQuery().isEmpty()) {
			Condition<GeneralQueryBuilder> condition = pipeline.apply(params.getQuery(), c);
		    Criteria criteria = condition.query(new MyMongoVisitor());
		    query.addCriteria(criteria);
		}
		
		return query;
	}
}