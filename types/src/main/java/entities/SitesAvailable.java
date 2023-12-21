package entities;

public class SitesAvailable extends BaseEntity{

	public static final String RESOURCE = "/sites";
	
	String geo;
	
	public SitesAvailable() {}
	
	public SitesAvailable(String geo) {
		this.geo = geo;
	}
}
