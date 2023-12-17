package com.demo.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	    if(params == null) params = new Params();
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
		QueryConversionPipeline pipeline = QueryConversionPipeline.builder()
				.useNonDefaultParsingPipe(new CustomParsingPipe())
				.build();
	    if(params == null) params = new Params();

	    Query query = new Query();
	    String queryStr = params.getQuery();
	    if(queryStr == null || queryStr.isEmpty()) return query;
	    String generateCondition = queryStr;
	    Criteria forEM = null;
	    if(queryStr.contains("=em=")) {
	    	if(queryStr.contains(";")){
	    		generateCondition = queryStr.substring(0, queryStr.lastIndexOf(";"));
	    	} else {
	    		generateCondition = "";
	    	}
	    	String emCondition = queryStr.substring(queryStr.lastIndexOf(";") + 1);
	    	String [] elemMatch = emCondition.split("=em=");
	    	String criteriaStr = elemMatch[1];
	    	if(criteriaStr.startsWith("(")) {
	    		criteriaStr = criteriaStr.substring(1);
	    	} 
	    	if(criteriaStr.endsWith(")")) {
	    		criteriaStr = criteriaStr.substring(0, criteriaStr.length() - 1);
	    	}
	    	String [] criteriaPieces = criteriaStr.split(",");
	    	
	    	forEM = Criteria.where(elemMatch[0]);
	    	Criteria inside = null;
	    	for(String piece : criteriaPieces) {
	    		String [] pieces = piece.split(":");
	    		if(inside == null) {
	    			inside = Criteria.where(pieces[0]).is(pieces[1]);
	    		} else {
	    			inside.and(pieces[0]).is(pieces[1]);
	    		}
	    	}
    		forEM.elemMatch(inside);
		}
	    Criteria criteria = null;
	    if(generateCondition != null && !generateCondition.isEmpty()) {
			Condition<GeneralQueryBuilder> condition = pipeline.apply(generateCondition, c);
		    criteria = condition.query(new MyMongoVisitor());
	    }
	    if(forEM != null) {
	    	if(criteria == null) criteria = forEM;
	    }
	    query.addCriteria(criteria);
		
		return query;
	}
}