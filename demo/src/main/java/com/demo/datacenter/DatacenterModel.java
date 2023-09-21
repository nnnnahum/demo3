package com.demo.datacenter;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Datacenter;
import entities.requests.Params;

@Component
public class DatacenterModel {

	private Store<Datacenter> datacenterStore;
	
	@Autowired
	public void setDatacenterStore(DatacenterStore store) {
		this.datacenterStore = store;
	}
	
	public Datacenter post(Datacenter user) {
		return datacenterStore.post(user);
	}

	public Datacenter getById(UUID id) {
		return datacenterStore.getById(id);
	}

	public Datacenter put(Datacenter user) {
		datacenterStore.put(user.getId(), user);
		return datacenterStore.getById(user.getId());
	}

	public void delete(UUID id) {
		datacenterStore.deleteById(id);
	}

	public List<Datacenter> get(Params query) {
		return datacenterStore.get(query);
	}

	public Datacenter patch(Datacenter datacenter) {
		return datacenterStore.patch(datacenter);
	}
}