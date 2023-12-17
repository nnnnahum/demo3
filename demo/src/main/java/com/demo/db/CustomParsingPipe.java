package com.demo.db;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

import com.github.rutledgepaulv.rqe.operators.QueryOperator;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;

public class CustomParsingPipe implements Function<String, Node> {


    @Override
    public Node apply(String rsql) {    	
    	Set<ComparisonOperator> operators = Arrays.stream(QueryOperator.values())
        .map(QueryOperator::parserOperator).collect(toSet());
    	operators.add(new ComparisonOperator("=em=", true));
    	RSQLParser result = new RSQLParser(operators);
    	
        return result
                .parse(rsql);
    }


}
