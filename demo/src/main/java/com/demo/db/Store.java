package com.demo.db;

import java.util.List;
import java.util.UUID;

import entities.requests.Params;


public interface Store<T> {

	T post(T t);
	T put(UUID id, T t);
	void deleteById(UUID id);
	T getById(UUID id);
	List<T> get(Params query);
	long count(Params query);
	T patch(T t);
}