package com.demo.tasks;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Task;
import entities.requests.Params;

@Component
public class TaskModel {

	private Store<Task> taskStore;
	
	@Autowired 
	public void setTaskStore(TaskStore store) {
		this.taskStore = store;
	}

	public Task post(Task task) {
		return taskStore.post(task);
	}

	public Task getById(UUID id) {
		return taskStore.getById(id);
	}

	public Task put(Task task) {
		taskStore.put(task.getId(), task);
		return taskStore.getById(task.getId());
	}

	public void delete(UUID id) {
		taskStore.deleteById(id);
	}

	public List<Task> get(Params query) {
		return taskStore.get(query);
	}
	
	public long count(Params query) {
		return taskStore.count(query);
	}

	public Task patch(Task task) {
		return taskStore.patch(task);
	}
}
