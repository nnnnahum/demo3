package com.demo.providers;

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
import entities.HostingProvider;
import entities.Location;
import entities.Organization;
import entities.Permission;
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
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@PostConstruct
	public void start() {
		router.registerRoute(HostingProvider.RESOURCE, this);
	}
		
	public ResponseMessage post(RequestMessage request) {
		HostingProvider provider = (HostingProvider) request.getBody();
		provider.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateProvider(provider, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(provider.getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_PROVIDERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// make sure there isn't another provider with the same name
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + provider.getName()
						+ ";id!=" + provider.getId());
		List<HostingProvider> providers = model.get(emailCheck);
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
	
	private FieldValidationErrorMessage validateProvider(HostingProvider provider, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(provider.getName() == null || provider.getName().isEmpty()) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		HostingProvider provider = model.getById(request.getId());
		if(provider == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Provider not found with Id: " + request.getId());
		}
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), provider);
	}
	
	public ResponseMessage put(RequestMessage request) {
		HostingProvider provider = (HostingProvider) request.getBody();
		provider.setId(request.getId());
		FieldValidationErrorMessage fvem = validateProvider(provider, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(provider.getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_PROVIDERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		// make sure there isn't another provider with the same name
		Params emailCheck = new Params();
		emailCheck.setQuery("name==" + provider.getName()
						+ ";id!=" + provider.getId());
		List<HostingProvider> providers = model.get(emailCheck);
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
		HostingProvider provider = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(provider.getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_PROVIDERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		model.delete(request.getId());
		return new ResponseMessage(HttpStatus.NO_CONTENT, request.getHeaders(), null);
	}

	public ResponseMessage get(RequestMessage request) {
		if (request.getId() != null) {
			return getById(request);
		}
		Params query = authUtil.appendQueryForOrgWithPermission(request, Permission.VIEW_PROVIDERS);
		List<HostingProvider> providers = model.get(query);
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
		HostingProvider provider = (HostingProvider) request.getBody();
		HostingProvider existingProvider = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingProvider.getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_PROVIDERS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		provider.setId(request.getId());
		provider = model.patch(provider);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), provider);
	}
}
