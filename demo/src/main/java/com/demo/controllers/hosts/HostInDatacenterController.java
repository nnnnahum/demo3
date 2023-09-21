package com.demo.controllers.hosts;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.router.MessageRouter;

import entities.Datacenter;
import entities.Host;
import entities.Location;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
@RestController
@RequestMapping(Host.DATACENTER_RESOURCE)
public class HostInDatacenterController {

	@Autowired
    private MessageRouter router;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage postHost(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@RequestBody Host host) {
    	verifyHostId(host, datacenterId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.POST, Host.RESOURCE, null, 
    			null, null, host, Location.MGMTAPI, Location.LOCAL));
    }

	
    private void verifyHostId(Host host, String datacenterId) {
    	UUID datacenterUuid = UUID.fromString(datacenterId);
    	if(host.getDatacenter() == null || host.getDatacenter().getId() == null) {
    		host.setDatacenter(new Datacenter(datacenterUuid, null, null, null, null, null));
    	}
	}


	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseMessage putHost(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@PathVariable String id,
    		@RequestBody Host host) {
    	verifyHostId(host, datacenterId);
		return router.sendAndReceive(new RequestMessage(HttpMethod.PUT, Host.RESOURCE, 
    			UUID.fromString(id),  null, null, host, Location.MGMTAPI, Location.LOCAL));
    }
	
	@DeleteMapping("/{id}")
    public ResponseMessage deleteHost(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@PathVariable String id) {
    	return router.sendAndReceive(new RequestMessage(HttpMethod.DELETE, Host.RESOURCE, 
    			UUID.fromString(id),  null, null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping
    public ResponseMessage getHostForDatacenter(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@RequestParam(required = false) String query,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) String page,
    		@RequestParam(required = false) String pageSize
    		) {
		Params params = new Params();
		if(query == null) query = "datacenter.id==" + datacenterId;
		else {
			params.setQuery(query+";datacenter.id==" + datacenterId);
		}
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.RESOURCE, 
    			null, new Params(query, sort, page, pageSize), null, null, Location.MGMTAPI, Location.LOCAL));
    }
	
	@GetMapping("/{id}")
    public ResponseMessage getHost(
    		@PathVariable(value = "datacenterId") String datacenterId,
    		@PathVariable String id) {
		Params params = new Params();
		params.setQuery("datacenter.id==" + datacenterId);
    	return router.sendAndReceive(new RequestMessage(HttpMethod.GET, Host.RESOURCE, 
    			UUID.fromString(id), params, null, null, Location.MGMTAPI, Location.LOCAL));
    }
}
