package entities.requests;

import java.util.UUID;

import org.springframework.http.HttpMethod;

import entities.Location;

public class RequestMessage {

	public RequestMessage() {}
	public RequestMessage(HttpMethod method, 
						  String resource, 
						  UUID id,
						  Params query, 
						  Params headers, 
						  Object body, 
						  Location source,
						  Location destination) {
		this.method = method;
		this.resource = resource;
		this.id = id;
		this.query = query;
		this.headers = headers;
		this.body = body;
		this.source = source;
		this.destination = destination;
	}
	
	private HttpMethod method;
	private UUID id;
	private String resource;
	private Params query;
	private Params headers;
	private Object body;
	private Location source;
	private Location destination;
	public HttpMethod getMethod() {
		return method;
	}
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public UUID getId() {
		return this.id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public Params getQuery() {
		return query;
	}
	public void setQuery(Params query) {
		this.query = query;
	}
	public Params getHeaders() {
		return headers;
	}
	public void setHeaders(Params headers) {
		this.headers = headers;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	public Location getSource() {
		return source;
	}
	public void setSource(Location source) {
		this.source = source;
	}
	public Location getDestination() {
		return destination;
	}
	public void setDestination(Location destination) {
		this.destination = destination;
	}
}