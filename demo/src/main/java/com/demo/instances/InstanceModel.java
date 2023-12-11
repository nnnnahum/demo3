package com.demo.instances;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.CloudLibrary;
import entities.requests.Params;

@Component
public class InstanceModel {

	private Store<CloudLibrary> instanceStore;
	
	@Autowired 
	public void setInstanceStore(InstanceStore store) {
		this.instanceStore = store;
	}

	public CloudLibrary post(CloudLibrary instance) {
		return instanceStore.post(instance);
	}

	public CloudLibrary getById(UUID id) {
		return instanceStore.getById(id);
	}

	public CloudLibrary put(CloudLibrary instance) {
		instanceStore.put(instance.getId(), instance);
		return instanceStore.getById(instance.getId());
	}

	public void delete(UUID id) {
		instanceStore.deleteById(id);
	}

	public List<CloudLibrary> get(Params query) {
		return instanceStore.get(query);
	}
	
	public long count(Params query) {
		return instanceStore.count(query);
	}

	public CloudLibrary patch(CloudLibrary instance) {
		return instanceStore.patch(instance);
	}
}
