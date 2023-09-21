package com.demo.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Role;
import entities.requests.Params;

@Component
public class RoleModel {

	private Store<Role> roleStore;
	
	@Autowired 
	public void setRoleStore(RoleStore store) {
		this.roleStore = store;
	}

	public Role post(Role role) {
		return roleStore.post(role);
	}

	public Role getById(UUID id) {
		return roleStore.getById(id);
	}

	public Role put(Role role) {
		roleStore.put(role.getId(), role);
		return roleStore.getById(role.getId());
	}

	public void delete(UUID id) {
		roleStore.deleteById(id);
	}

	public List<Role> get(Params query) {
		return roleStore.get(query);
	}
	
	public long count(Params query) {
		return roleStore.count(query);
	}

	public Role patch(Role role) {
		return roleStore.patch(role);
	}
}
