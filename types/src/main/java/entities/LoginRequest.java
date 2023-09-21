package entities;

public class LoginRequest {

	public static final String RESOURCE = "/login";
	
	String emailAddress;
	String password;
	
	public LoginRequest() {}
	public LoginRequest(String emailAddress, String password) {
		this.emailAddress = emailAddress;
		this.password = password;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
