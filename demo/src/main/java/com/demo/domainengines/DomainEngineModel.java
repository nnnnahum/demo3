package com.demo.domainengines;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.DomainEngine;
import entities.requests.Params;

@Component
public class DomainEngineModel {

	private Store<DomainEngine> domainEngineStore;
	
	@Autowired
	public void setDomainEngineStore(DomainEngineStore store){
		this.domainEngineStore = store;
	}
	
	
	public DomainEngine post(DomainEngine domainEngine) {
		return domainEngineStore.post(domainEngine);
	}

	public DomainEngine getById(UUID id) {
		return domainEngineStore.getById(id);
	}

	public DomainEngine put(DomainEngine domainEngine) {
		domainEngineStore.put(domainEngine.getId(), domainEngine);
		return domainEngineStore.getById(domainEngine.getId());
	}

	public void delete(UUID id) {
		domainEngineStore.deleteById(id);
	}

	public List<DomainEngine> get(Params query) {
		return domainEngineStore.get(query);
	}
	
	public long count(Params query) {
		return domainEngineStore.count(query);
	}

	public DomainEngine patch(DomainEngine domainEngine) {
		return domainEngineStore.patch(domainEngine);
	}
}
