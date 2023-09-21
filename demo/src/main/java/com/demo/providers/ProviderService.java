package com.demo.providers;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.demo.router.BaseService;
import com.demo.router.MessageRouter;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.Provider;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class ProviderService implements BaseService{

	public static final String PATH = "/providers";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	ProviderModel model;
	
	@PostConstruct
	public void start() {
		router.registerRoute(Provider.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		Provider provider = (Provider) request.getBody();
		provider.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateProvider(provider, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		// make sure there isn't another provider with the same name
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + provider.getName()
						+ ";id!=" + provider.getId());
		List<Provider> providers = model.get(emailCheck);
		if(providers != null && !providers.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another provider already exists for name: " 
					+ provider.getName());
		}
			
		provider = model.post(provider);
		router.notify(EventsOfInterest.provider_created, provider);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), provider);
	}
	
	private FieldValidationErrorMessage validateProvider(Provider provider, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(provider.getName() == null || provider.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Provider provider = model.getById(request.getId());
		if(provider == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Provider not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), provider);
	}
	
	public ResponseMessage put(RequestMessage request) {
		Provider provider = (Provider) request.getBody();
		provider.setId(request.getId());
		FieldValidationErrorMessage fvem = validateProvider(provider, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		// make sure there isn't another provider with the same name
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + provider.getName()
						+ ";id!=" + provider.getId());
		List<Provider> providers = model.get(emailCheck);
		if(providers != null && !providers.isEmpty()) {
			return new ErrorMessage(HttpStatus.CONFLICT, 
					request.getHeaders(), 
					"Another provider already exists for name: " 
					+ provider.getName());
		}

		provider = model.put(provider);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), provider);	
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
		List<Provider> providers = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), providers, 
				new Count((long)providers.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Provider provider = (Provider) request.getBody();
		provider.setId(request.getId());
		provider = model.patch(provider);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), provider);
	}
}
