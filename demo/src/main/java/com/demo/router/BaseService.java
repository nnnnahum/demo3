package com.demo.router;

import entities.BaseEntity;
import entities.EventsOfInterest;
import entities.requests.ErrorMessageException;
import entities.requests.RequestMessage;
import entities.requests.ResponseMessage;

public interface BaseService {

	public void start();
	public ResponseMessage post(RequestMessage request);
	public ResponseMessage put(RequestMessage request);
	public ResponseMessage delete(RequestMessage request);
	public ResponseMessage get(RequestMessage request) throws ErrorMessageException;
	public void notify(EventsOfInterest eventName, BaseEntity entity);
	public ResponseMessage patch(RequestMessage request);
}