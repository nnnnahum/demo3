package com.demo.tasks;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import entities.Task;
import entities.requests.Count;
import entities.requests.ErrorMessage;
import entities.requests.ErrorMessageException;
import entities.requests.FieldValidationErrorMessage;
import entities.requests.Params;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

@Component
public class TaskService implements BaseService{

	private static final Logger log = LoggerFactory.getLogger(TaskService.class);

	public static final String PATH = "/tasks";
	
	@Autowired
	MessageRouter router;
	
	@Autowired
	TaskModel model;
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	OrgUtil orgUtil;
	
	@Override
	public void start() {
		router.registerRoute(Task.RESOURCE, this);
	}
	
	public ResponseMessage post(RequestMessage request) {
		Task task = (Task)request.getBody();
		task.setId(UUID.randomUUID());
		FieldValidationErrorMessage fvem = validateTask(task, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}
		
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(task.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TASKS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
					
		task.setLastUpdated(new Date());
		task = model.post(task);
		return new ResponseMessage(HttpStatus.CREATED, request.getHeaders(), task);
	}
	
	private FieldValidationErrorMessage validateTask(Task task, Params headers) {
		FieldValidationErrorMessage fvem = null;
		
		if(task.getName() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "name");
		}
		
		if(task.getIsCancelable() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "cancelable");
		}
		
		if(task.getOrg() == null || task.getOrg().getId() == null) {
			fvem = FieldValidationErrorMessage.addError(fvem, headers, "parentOrganization.id");
		}

		return fvem;
	}

	public ResponseMessage getById(RequestMessage request) {
		Task task = model.getById(request.getId());
		if(task == null) {
			return new ErrorMessage(HttpStatus.NOT_FOUND, request.getHeaders(), 
					"Task not found with Id: " + request.getId());
		}
		
		if(request.getSource() != Location.LOCAL){
			Organization org = orgUtil.getOrgfromOrgId(task.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TASKS, Permission.VIEW_TASKS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), task);
	}
	
	public ResponseMessage put(RequestMessage request) {
				
		Task task = (Task) request.getBody();
		task.setId(request.getId());
		FieldValidationErrorMessage fvem = validateTask(task, request.getHeaders());
		if(fvem != null) {
			return fvem;
		}

		if(request.getSource() != Location.LOCAL) {
		Organization org = orgUtil.getOrgfromOrgId(task.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TASKS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}

		task.setLastUpdated(new Date());
		task = model.put(task);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), task);	
	}
	
	public ResponseMessage delete(RequestMessage request) {
		Task existingTask = model.getById(request.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingTask.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TASKS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
		
		Task patchTask = new Task();
		patchTask.setId(existingTask.getId());
		patchTask.setCancelRequested(true);
		existingTask = model.patch(patchTask);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), existingTask);
	}

	public ResponseMessage get(RequestMessage request) throws ErrorMessageException{
		if (request.getId() != null) {
			return getById(request);
		}
		
		Params query = request.getQuery();
		if(request.getSource() != Location.LOCAL) {	
			query = authUtil.appendQueryForOrgPermissions(request);
		}
		List<Task> tasks = model.get(query);
		long count = model.count(query);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), tasks, 
				new Count((long)tasks.size(), count));
	}

	@Override
	public void notify(EventsOfInterest eventName, BaseEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResponseMessage patch(RequestMessage request) {
		Task task = (Task) request.getBody();
		task.setId(request.getId());
		
		Task existingTask = model.getById(task.getId());
		if(request.getSource() != Location.LOCAL) {
			Organization org = orgUtil.getOrgfromOrgId(existingTask.getOrg().getId());
			if(!authUtil.hasPermissionInOrg(request, org, Arrays.asList(Permission.MANAGE_TASKS))) {
				return new ErrorMessage(HttpStatus.UNAUTHORIZED, 
						request.getHeaders(), 
						"Unaurothized operation.");
			}
		}
			
		task.setLastUpdated(new Date());
		task = model.patch(task);
		return new ResponseMessage(HttpStatus.OK, request.getHeaders(), task);
	}
}
