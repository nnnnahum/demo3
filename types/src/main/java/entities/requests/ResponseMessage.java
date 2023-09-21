package entities.requests;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage {
	
	public ResponseMessage() {}
	public ResponseMessage(HttpStatus status,
			               Params headers,
			               Object body,
			               Count count) {
		this.status = status;
		this.headers = headers;
		this.body = body;
		this.count = count;
	}
	
	public ResponseMessage(HttpStatus status,
            Params headers,
            Object body) {
		this.status = status;
		this.headers = headers;
		this.body = body;
	}
	
	private HttpStatus status;
	private Params headers;
	private Object body;
	private Count count;
	
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
	public HttpStatus getStatus() {
		return status;
	}
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	public Count getCount() {
		return count;
	}
	public void setCount(Count count) {
		this.count = count;
	}
}