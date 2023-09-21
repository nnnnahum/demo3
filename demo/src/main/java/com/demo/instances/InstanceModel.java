package com.demo.instances;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Instance;
import entities.requests.Params;

@Component
public class InstanceModel {

	private Store<Instance> instanceStore;
	
	@Autowired 
	public void setInstanceStore(InstanceStore store) {
		this.instanceStore = store;
	}

	public Instance post(Instance instance) {
		return instanceStore.post(instance);
	}

	public Instance getById(UUID id) {
		return instanceStore.getById(id);
	}

	public Instance put(Instance instance) {
		instanceStore.put(instance.getId(), instance);
		return instanceStore.getById(instance.getId());
	}

	public void delete(UUID id) {
		instanceStore.deleteById(id);
	}

	public List<Instance> get(Params query) {
		return instanceStore.get(query);
	}
	
	public long count(Params query) {
		return instanceStore.count(query);
	}

	public Instance patch(Instance instance) {
		return instanceStore.patch(instance);
	}
}
