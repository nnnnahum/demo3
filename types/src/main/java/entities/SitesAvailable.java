package entities;

import java.util.UUID;

public class SitesAvailable extends BaseEntity{

	public static final String RESOURCE = "/sites";
	
	String geo;
	
	public SitesAvailable() {}
	
	public SitesAvailable(UUID id, String geo) {
		super(id);
		this.geo = geo;
	}
	
	public void setGeo(String geo) {
		this.geo = geo;
	}
	
	public String getGeo() {
		return geo;
	}
}
