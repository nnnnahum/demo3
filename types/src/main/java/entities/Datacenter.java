package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datacenters")
public class Datacenter extends BaseEntity{

	public static final String RESOURCE = "/datacenters";

	private String geo;
	private String name;
	private HostingProvider provider;
	private LatLong latlong;
	private AggregatedHostMetrics datacenterMetrics;
	
	public Datacenter() {}
	
	public Datacenter(UUID id, String geo, String name, HostingProvider provider, LatLong latlong, 
			AggregatedHostMetrics datacenterMetrcs) {
		super(id);
		this.geo = geo;
		this.name = name;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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