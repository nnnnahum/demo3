package com.demo.controllers.tapelibraries;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Location;
import entities.TapeLibrary;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(TapeLibrary.DATACENTER_RESOURCE)
public class TapeLibraryController {

	@Autowired
    private MessageRouter router;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postTapeLibrary(
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@RequestBody TapeLibrary tapeLibrary) {
    	Params headers = new Params();
    	headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, TapeLibrary.RESOURCE, null, 
    			null, headers, tapeLibrary, Location.MGMTAPI, Location.LOCAL));
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putTapeLibrary(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@PathVariable String id,
    		@RequestHeader(name = "auth") String authId,
    		@RequestBody TapeLibrary tapeLibrary) {
		Params headers = new Params();
		headers.put("authId", authId);
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, TapeLibrary.RESOURCE, 
    			UUID.fromString(id),  null, headers, tapeLibrary, Location.MGMTAPI, Location.LOCAL));
    }
	
	@DeleteMapping("/{id}")
    public ResponseMessage deleteTapeLibrary(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
		headers.put("authId", authId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, TapeLibrary.RESOURCE, 
    			UUID.fromString(id),  null, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getTapeLibrariesForDatacenter(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@RequestHeader(name = "auth") String authId,
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
		Params headers = new Params();
		headers.put("auth", authId);
		Params params = new Params();
		if(query == null) query = "datacenter.id==" + datacenterId;
		else {
			params.setQuery(query+";datacenter.id==" + datacenterId);
		}
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, TapeLibrary.RESOURCE, 
    			null, new Params(query, sort, page, pageSize), headers, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getTapeLibrary(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@RequestHeader(name = "auth") String authId,
    		@PathVariable String id) {
		Params headers = new Params();
		headers.put("authId", authId);
		Params params = new Params();
		params.setQuery("datacenter.id==" + datacenterId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, TapeLibrary.RESOURCE, 
    			UUID.fromString(id), params, headers, null, Location.MGMTAPI, Location.LOCAL));
    }
}
