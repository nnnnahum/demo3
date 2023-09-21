package com.demo.tenants;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Tenant;
import entities.requests.Params;

@Component
public class TenantModel {

	private Store<Tenant> tenantStore;
	
	@Autowired 
	public void setUserStore(TenantStore store) {
		this.tenantStore = store;
	}

	public Tenant post(Tenant tenant) {
		return tenantStore.post(tenant);
	}

	public Tenant getById(UUID id) {
		return tenantStore.getById(id);
	}

	public Tenant put(Tenant tenant) {
		tenantStore.put(tenant.getId(), tenant);
		return tenantStore.getById(tenant.getId());
	}

	public void delete(UUID id) {
		tenantStore.deleteById(id);
	}

	public List<Tenant> get(Params query) {
		return tenantStore.get(query);
	}
	
	public long count(Params query) {
		return tenantStore.count(query);
	}

	public Tenant patch(Tenant tenant) {
		return tenantStore.patch(tenant);
	}
}
