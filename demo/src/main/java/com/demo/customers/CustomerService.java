package com.demo.customers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;
import com.demo.utils.AuthUtil;
import com.demo.utils.OrgUtil;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.Location;
import entities.Organization;
import entities.Permission;
import entities.PermissionOnEntity;
import entities.User;
import entities.Customer;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class CustomerService implements BaseService{

	public static final String PATH = "/customers";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	CustomerModel model;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Customer.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		Customer customer = (Customer) request.getBody();
		customer.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateCustomer(customer, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
			
		// When creating a customer, make sure to add the current user's role as someone who can add users
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(customer.getParentReseller().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_CUSTOMERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}

		User userCreatingCustomer = authUtil.getUserFromSession(request);
		customer.getPerms().add(new PermissionOnEntity(Permission.MANAGE_USERS, userCreatingCustomer.getRole().getId().toString()));
		customer = model.post(customer);
		router.notify(EventsOfInterest.customer_created, customer);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), customer);
	}
	
	private FieldValidationErrorMessage validateCustomer(Customer customer, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(customer.getName() == null || customer.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Customer customer = model.getById(request.getId());
		if(customer == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Customer not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), customer);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Customer customer = (Customer) request.getBody();
		customer.setId(request.getId());
		FieldValidationErrorMessage fvem = validateCustomer(customer, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		customer = model.put(customer);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), customer);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		model.delete(request.getId());
		return new ResponseMessage(HttpStatus.NO_CONTENT, request.getHeaders(), null);
	}

	public ResponseMessage get(RequestMessage request) {
		if (request.getId() != null) {
			return getById(request);
		}
		Params query = request.getQuery();
		List<Customer> customers = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), customers, 
				new Count((long)customers.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Customer customer = (Customer) request.getBody();
		customer.setId(request.getId());
		customer = model.patch(customer);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), customer);
	}
}
