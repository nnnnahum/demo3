package com.demo.providers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Provider;
import entities.requests.Params;

@Component
public class ProviderModel {

	private Store<Provider> providerStore;
	
	@Autowired 
	public void setUserStore(ProviderStore store) {
		this.providerStore = store;
	}

	public Provider post(Provider provider) {
		return providerStore.post(provider);
	}

	public Provider getById(UUID id) {
		return providerStore.getById(id);
	}

	public Provider put(Provider provider) {
		providerStore.put(provider.getId(), provider);
		return providerStore.getById(provider.getId());
	}

	public void delete(UUID id) {
		providerStore.deleteById(id);
	}

	public List<Provider> get(Params query) {
		return providerStore.get(query);
	}
	
	public long count(Params query) {
		return providerStore.count(query);
	}

	public Provider patch(Provider provider) {
		return providerStore.patch(provider);
	}
}
