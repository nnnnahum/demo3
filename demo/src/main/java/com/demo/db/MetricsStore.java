package com.demo.db;

import entities.requests.Params;

public interface MetricsStore<T>{
	T getMetrics(Params query);
}
