package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role extends BaseEntity{

	public static final String RESOURCE = "/roles";

	public String name;
	public Organization org;

	public Role() {}
	public Role(UUID id, String name, Organization org) {
		super(id);
		this.name = name;
		this.org = org;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}
}