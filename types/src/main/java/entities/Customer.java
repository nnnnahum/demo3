package entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
public class Customer extends Organization{
	
	public static final String RESOURCE = "/customers";

	List<CloudLibrary> instances;
	Organization parentReseller;

	public Customer() {}
	
	public Customer(UUID id, String name, Organization parentReseller, List<PermissionOnEntity> perms) {
		super(id, name, perms);
		this.parentReseller = parentReseller;
	}
	
	public List<CloudLibrary> getInstances(){
		return instances;
	}
	
	public void setInstances(List<CloudLibrary> instances) {
		this.instances = instances;
	}
	
	public Organization getParentReseller() {
		return parentReseller;
	}

	public void setParentReseller(Organization parentReseller) {
		this.parentReseller = parentReseller;
	}

	public static final Set<Permission> defaultAdminPermissions = Stream.of(
			Permission.MANAGE_ROLES,
			Permission.MANAGE_USERS,
			Permission.MANAGE_CLOUD_LIBRARIES,
			Permission.VIEW_USERS,
			Permission.VIEW_ROLES,
			Permission.VIEW_CLOUD_LIBRARIES,
			Permission.VIEW_SITES)
			  .collect(Collectors.toCollection(HashSet::new));
	
	public static final Set<Permission> defaultViewPermission = Stream.of(
			Permission.VIEW_USERS,
			Permission.VIEW_ROLES,
			Permission.VIEW_CLOUD_LIBRARIES,
			Permission.VIEW_SITES)
			.collect(Collectors.toCollection(HashSet::new));
}