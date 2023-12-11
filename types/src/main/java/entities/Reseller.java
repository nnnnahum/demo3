package entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resellers")
public class Reseller extends Organization {
	
	public static final String RESOURCE = "/resellers";

	public Reseller() {}
	
	public Reseller(UUID id, String name, List<PermissionOnEntity> perms) {
		super(id, name, perms);
	}
	
	public static final Set<Permission> defaultAdminPermissions = Stream.of(
			Permission.MANAGE_ROLES,
			Permission.MANAGE_USERS,
			Permission.MANAGE_CUSTOMERS,
			Permission.MANAGE_CLOUD_LIBRARIES,
			Permission.VIEW_USERS,
			Permission.VIEW_ROLES,
			Permission.VIEW_CUSTOMERS,
			Permission.VIEW_CLOUD_LIBRARIES,
			Permission.VIEW_SITES)
			  .collect(Collectors.toCollection(HashSet::new));
	
	public static final Set<Permission> defaultViewPermission = Stream.of(
			Permission.VIEW_USERS,
			Permission.VIEW_ROLES,
			Permission.VIEW_CUSTOMERS,
			Permission.VIEW_CLOUD_LIBRARIES,
			Permission.VIEW_SITES)
			.collect(Collectors.toCollection(HashSet::new));
}