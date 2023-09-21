package com.demo.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Session;
import entities.requests.Params;

@Component
public class SessionModel {

	private Store<Session> sessionStore;
	
	@Autowired 
	public void setSessionStore(SessionStore store) {
		this.sessionStore = store;
	}

	public Session post(Session session) {
		return sessionStore.post(session);
	}

	public Session getById(UUID id) {
		return sessionStore.getById(id);
	}

	public Session put(Session session) {
		sessionStore.put(session.getId(), session);
		return sessionStore.getById(session.getId());
	}

	public void delete(UUID id) {
		sessionStore.deleteById(id);
	}

	public List<Session> get(Params query) {
		return sessionStore.get(query);
	}
	
	public long count(Params query) {
		return sessionStore.count(query);
	}

	public Session patch(Session session) {
		return sessionStore.patch(session);
	}
}
