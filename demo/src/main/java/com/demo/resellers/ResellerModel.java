package com.demo.resellers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Reseller;
import entities.requests.Params;

@Component
public class ResellerModel {

	private Store<Reseller> resellerStore;
	
	@Autowired 
	public void setUserStore(ResellerStore store) {
		this.resellerStore = store;
	}

	public Reseller post(Reseller reseller) {
		return resellerStore.post(reseller);
	}

	public Reseller getById(UUID id) {
		return resellerStore.getById(id);
	}

	public Reseller put(Reseller reseller) {
		resellerStore.put(reseller.getId(), reseller);
		return resellerStore.getById(reseller.getId());
	}

	public void delete(UUID id) {
		resellerStore.deleteById(id);
	}

	public List<Reseller> get(Params query) {
		return resellerStore.get(query);
	}
	
	public long count(Params query) {
		return resellerStore.count(query);
	}

	public Reseller patch(Reseller reseller) {
		return resellerStore.patch(reseller);
	}
}
