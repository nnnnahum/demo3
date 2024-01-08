package com.demo.cloudtapelibrary;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.CloudTapeLibrary;
import entities.requests.Params;

@Component
public class CloudTapeLibraryModel {

	private Store<CloudTapeLibrary> cloudTapeLibraryStore;
	
	@Autowired 
	public void setInstanceStore(CloudTapeLibraryStore store) {
		this.cloudTapeLibraryStore = store;
	}

	public CloudTapeLibrary post(CloudTapeLibrary cloudTapeLibrary) {
		return cloudTapeLibraryStore.post(cloudTapeLibrary);
	}

	public CloudTapeLibrary getById(UUID id) {
		return cloudTapeLibraryStore.getById(id);
	}

	public CloudTapeLibrary put(CloudTapeLibrary cloudTapeLibrary) {
		cloudTapeLibraryStore.put(cloudTapeLibrary.getId(), cloudTapeLibrary);
		return cloudTapeLibraryStore.getById(cloudTapeLibrary.getId());
	}

	public void delete(UUID id) {
		cloudTapeLibraryStore.deleteById(id);
	}

	public List<CloudTapeLibrary> get(Params query) {
		return cloudTapeLibraryStore.get(query);
	}
	
	public long count(Params query) {
		return cloudTapeLibraryStore.count(query);
	}

	public CloudTapeLibrary patch(CloudTapeLibrary cloudTapeLibrary) {
		return cloudTapeLibraryStore.patch(cloudTapeLibrary);
	}
}
