package entities.requests;

import org.springframework.http.HttpStatus;

public class FieldValidationErrorMessage extends ErrorMessage {

	public FieldValidationErrorMessage() {
		super();
	}
	
	private FieldValidationErrorMessage(Params headers, String field) {
		super(HttpStatus.BAD_REQUEST, 
				headers, 
				"Invalid value for field: " + field);
		
	}
	
	public static FieldValidationErrorMessage addError(
			FieldValidationErrorMessage fvem, Params headers, String field) {
		if(fvem == null) {
			fvem = new FieldValidationErrorMessage(headers, field);
		} else {
			fvem.setBody(fvem.getBody() + ", " + field);
		}
		return fvem;
	}
}