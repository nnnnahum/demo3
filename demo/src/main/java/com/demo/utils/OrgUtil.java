package com.demo.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.MessageRouter;

import entities.Location;
import entities.Organization;
import entities.Reseller;
import entities.HostingProvider;
import entities.Customer;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class OrgUtil {

	@Autowired
	MessageRouter router; 
	
	public Organization getOrgfromOrgId(UUID orgId) {
		ResponseMessage responseMessage = router.sendAndReceive(new RequestMessage(HttpMethod.GET, 
				Customer.RESOURCE, orgId, null,  null, null, Location.LOCAL, Location.LOCAL));
		if(responseMessage.getStatus() == HttpStatus.OK) {
			return (Customer) responseMessage.getBody();
		}
		responseMessage = router.sendAndReceive(new RequestMessage(HttpMethod.GET, 
				HostingProvider.RESOURCE, orgId, null,  null, null, Location.LOCAL, Location.LOCAL));
		if(responseMessage.getStatus() == HttpStatus.OK) {
			return (HostingProvider) responseMessage.getBody();
		}
		responseMessage = router.sendAndReceive(new RequestMessage(HttpMethod.GET, 
				Reseller.RESOURCE, orgId, null,  null, null, Location.LOCAL, Location.LOCAL));
		if(responseMessage.getStatus() == HttpStatus.OK) {
			return (Reseller) responseMessage.getBody();
		}
		return null;
	}
}
