package com.demo.events;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Event;
import entities.requests.Params;

@Component
public class EventModel {

	private Store<Event> eventStore;
	
	@Autowired 
	public void setEventStore(EventStore store) {
		this.eventStore = store;
	}

	public Event post(Event event) {
		return eventStore.post(event);
	}

	public Event getById(UUID id) {
		return eventStore.getById(id);
	}

	public Event put(Event event) {
		eventStore.put(event.getId(), event);
		return eventStore.getById(event.getId());
	}

	public void delete(UUID id) {
		eventStore.deleteById(id);
	}

	public List<Event> get(Params query) {
		return eventStore.get(query);
	}
	
	public long count(Params query) {
		return eventStore.count(query);
	}

	public Event patch(Event event) {
		return eventStore.patch(event);
	}
}
