package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tape_library")
public class TapeLibrary extends BaseEntity{
	
	public static final String RESOURCE = "/tapeLibrary";
	public static final String DATACENTER_RESOURCE = "/datacenters/{datacenterId}/tapeLibrary";
	
	public TapeLibrary() {}
	
	public TapeLibrary(UUID id, String ip, String hostname, String location, Integer drivesTotal, Datacenter datacenter) {
		super(id);
		this.ip = ip;
		this.hostname = hostname;
		this.location = location;
		this.datacenter = datacenter;
		this.drivesTotal = drivesTotal;
	}
	
	private String ip;
	private String hostname;
	private String location;
	private Datacenter datacenter;
	private Integer drivesTotal;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}

	public Integer getDrivesTotal() {
		return drivesTotal;
	}

	public void setDrivesTotal(Integer drivesTotal) {
		this.drivesTotal = drivesTotal;
	}
}
