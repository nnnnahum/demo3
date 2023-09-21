package entities;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tenants")
public class Tenant extends Organization{
	
	public static final String RESOURCE = "/tenants";

	List<Instance> instances;

	public Tenant() {}
	
	public Tenant(UUID id, String name, List<PermissionOnEntity> perms) {
		super(id, name, perms);
	}
	
	public List<Instance> getInstances(){
		return instances;
	}
	
	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
}