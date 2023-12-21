package com.demo.tapelibrary;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.TapeLibrary;
import entities.requests.Params;

@Component
public class TapeLibraryModel {

	private Store<TapeLibrary> tapeLibraryStore;
	
	@Autowired
	public void setTapeLibraryStore(TapeLibraryStore store){
		this.tapeLibraryStore = store;
	}
	
	
	public TapeLibrary post(TapeLibrary tapeLibrary) {
		return tapeLibraryStore.post(tapeLibrary);
	}

	public TapeLibrary getById(UUID id) {
		return tapeLibraryStore.getById(id);
	}

	public TapeLibrary put(TapeLibrary tapeLibrary) {
		tapeLibraryStore.put(tapeLibrary.getId(), tapeLibrary);
		return tapeLibraryStore.getById(tapeLibrary.getId());
	}

	public void delete(UUID id) {
		tapeLibraryStore.deleteById(id);
	}

	public List<TapeLibrary> get(Params query) {
		return tapeLibraryStore.get(query);
	}
	
	public long count(Params query) {
		return tapeLibraryStore.count(query);
	}

	public TapeLibrary patch(TapeLibrary tapeLibrary) {
		return tapeLibraryStore.patch(tapeLibrary);
	}
}
