package com.demo.host;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.MetricsStore;
import com.demo.db.Store;

import entities.AggregatedHostMetrics;
import entities.Host;
import entities.requests.Params;

@Component
public class HostModel {

	private Store<Host> hostStore;
	private MetricsStore<AggregatedHostMetrics> metricsStore;
	
	@Autowired 
	public void setHostStore(HostStore store) {
		this.hostStore = store;
	}
	
	@Autowired
	public void setMetricsStore(MetricsStore metricsStore) {
		this.metricsStore = metricsStore;
	}

	public Host post(Host host) {
		return hostStore.post(host);
	}

	public Host getById(UUID id) {
		return hostStore.getById(id);
	}

	public Host put(Host host) {
		hostStore.put(host.getId(), host);
		return hostStore.getById(host.getId());
	}

	public void delete(UUID id) {
		hostStore.deleteById(id);
	}

	public List<Host> get(Params query) {
		return hostStore.get(query);
	}

	public Host patch(Host host) {
		return hostStore.patch(host);
	}

	public AggregatedHostMetrics getMetrics(Params query) {
		return metricsStore.getMetrics(query);
	}
}
