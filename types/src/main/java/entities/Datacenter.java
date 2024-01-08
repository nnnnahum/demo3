package entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datacenters")
public class Datacenter extends Organization{

	public static final String RESOURCE = "/datacenters";

	private String geo;
	private HostingProvider provider;
	private LatLong latlong;
	private AggregatedHostMetrics datacenterMetrics;
	
	public static final Set<Permission> defaultAdminPermissions = Stream.of(
			Permission.MANAGE_ROLES,
			Permission.MANAGE_USERS,
			Permission.MANAGE_DOMAIN_ENGINES,
			Permission.MANAGE_TAPE_LIBRARIES,
			Permission.VIEW_USERS,
			Permission.VIEW_ROLES,
			Permission.VIEW_TAPE_LIBRARIES,
			Permission.VIEW_DOMAIN_ENGINES)
			  .collect(Collectors.toCollection(HashSet::new));
	
	public static final Set<Permission> defaultViewPermission = Stream.of(
			Permission.VIEW_USERS,
			Permission.VIEW_ROLES,
			Permission.VIEW_TAPE_LIBRARIES,
			Permission.VIEW_DOMAIN_ENGINES)
			.collect(Collectors.toCollection(HashSet::new));
	
	public Datacenter() {}
	
	public Datacenter(UUID id, String name, String geo, HostingProvider provider, LatLong latlong, List<PermissionOnEntity> perms,
			AggregatedHostMetrics datacenterMetrcs) {
		super(id, name, perms);
		this.geo = geo;
		this.provider = provider;
		this.latlong = latlong;
		this.datacenterMetrics = datacenterMetrcs;
	}
	
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}

	public HostingProvider getProvider() {
		return provider;
	}

	public void setProvider(HostingProvider provider) {
		this.provider = provider;
	}

	public LatLong getLatlong() {
		return latlong;
	}

	public void setLatlong(LatLong latlong) {
		this.latlong = latlong;
	}
	
	public AggregatedHostMetrics getDatacenterMetrics() {
		return datacenterMetrics;
	}
	
	public void setDatacenterMetrics(AggregatedHostMetrics datacenterMetrics) {
		this.datacenterMetrics = datacenterMetrics;
	}
}