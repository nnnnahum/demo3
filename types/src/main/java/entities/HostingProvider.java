package entities;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "providers")
public class HostingProvider extends Organization{
	
	public static final String RESOURCE = "/providers";

	public HostingProvider() {}
	
	public HostingProvider(UUID id, String name, List<PermissionOnEntity> perms) {
		super(id, name, perms);
	}
}