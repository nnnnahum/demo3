package entities;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resellers")
public class Reseller extends Organization {
	
	public static final String RESOURCE = "/resellers";

	public Reseller() {}
	
	public Reseller(UUID id, String name, List<PermissionOnEntity> perms) {
		super(id, name, perms);
	}
}