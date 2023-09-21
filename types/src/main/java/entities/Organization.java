package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Organization extends BaseEntity{

	private String name;
	private List<PermissionOnEntity> perms = new ArrayList<>();


	public Organization() {}
	
	public Organization(UUID id, String name, List<PermissionOnEntity> perms) {
		super(id);
		this.name = name;
		this.perms = perms;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	public List<PermissionOnEntity> getPerms() {
		return perms;
	}
	public void setPerms(List<PermissionOnEntity> perms) {
		this.perms = perms;
	}
}