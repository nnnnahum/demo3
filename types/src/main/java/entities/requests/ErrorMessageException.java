package entities.requests;

public class ErrorMessageException extends Exception {

	private String code;
	private String message;
	
	public ErrorMessageException(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
