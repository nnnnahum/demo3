package entities.requests;

import org.springframework.http.HttpStatus;

public class ErrorMessage extends ResponseMessage {

	public ErrorMessage() {}
	
	public ErrorMessage(HttpStatus status, Params headers, Object body) {
		super(status, headers, body);
	}
	
	public ErrorMessage(ErrorMessageException e) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, null, e);
	}
}
