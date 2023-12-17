package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "users")
public class User extends BaseEntity{

	public static final String RESOURCE = "/users";
	
	public User() {}
	
	public User(UUID id, String firstName, String lastName, String phoneNumber, 
			String emailAddress, String password, Role role, Organization org) {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.password = password;
		this.role = role;
		this.org = org;
	}
	
	String firstName;
	String lastName;
	String emailAddress;

	@JsonInclude(Include.NON_NULL)
	String password;
	Role role;
	Organization org;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}
	
}