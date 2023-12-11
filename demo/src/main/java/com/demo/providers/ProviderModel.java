package com.demo.providers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.HostingProvider;
import entities.requests.Params;

@Component
public class ProviderModel {

	private Store<HostingProvider> providerStore;
	
	@Autowired 
	public void setUserStore(ProviderStore store) {
		this.providerStore = store;
	}

	public HostingProvider post(HostingProvider provider) {
		return providerStore.post(provider);
	}

	public HostingProvider getById(UUID id) {
		return providerStore.getById(id);
	}

	public HostingProvider put(HostingProvider provider) {
		providerStore.put(provider.getId(), provider);
		return providerStore.getById(provider.getId());
	}

	public void delete(UUID id) {
		providerStore.deleteById(id);
	}

	public List<HostingProvider> get(Params query) {
		return providerStore.get(query);
	}
	
	public long count(Params query) {
		return providerStore.count(query);
	}

	public HostingProvider patch(HostingProvider provider) {
		return providerStore.patch(provider);
	}
}
