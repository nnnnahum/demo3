package com.demo.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.User;
import entities.requests.Params;

@Component
public class UserModel {

	private Store<User> userStore;
	
	@Autowired 
	public void setUserStore(UserStore store) {
		this.userStore = store;
	}

	public User post(User user) {
		return userStore.post(user);
	}

	public User getById(UUID id) {
		return userStore.getById(id);
	}

	public User put(User user) {
		userStore.put(user.getId(), user);
		return userStore.getById(user.getId());
	}

	public void delete(UUID id) {
		userStore.deleteById(id);
	}

	public List<User> get(Params query) {
		return userStore.get(query);
	}
	
	public long count(Params query) {
		return userStore.count(query);
	}

	public User patch(User user) {
		return userStore.patch(user);
	}
}
