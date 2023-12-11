package com.demo.customers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.db.Store;

import entities.Customer;
import entities.requests.Params;

@Component
public class CustomerModel {

	private Store<Customer> customerStore;
	
	@Autowired 
	public void setUserStore(CustomerStore store) {
		this.customerStore = store;
	}

	public Customer post(Customer customer) {
		return customerStore.post(customer);
	}

	public Customer getById(UUID id) {
		return customerStore.getById(id);
	}

	public Customer put(Customer customer) {
		customerStore.put(customer.getId(), customer);
		return customerStore.getById(customer.getId());
	}

	public void delete(UUID id) {
		customerStore.deleteById(id);
	}

	public List<Customer> get(Params query) {
		return customerStore.get(query);
	}
	
	public long count(Params query) {
		return customerStore.count(query);
	}

	public Customer patch(Customer customer) {
		return customerStore.patch(customer);
	}
}
