package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "domainengine")
public class DomainEngine extends BaseEntity{

	public static final String RESOURCE = "/domainengines";

	private Datacenter datacenter;
	private String ip;
	private String hostname;
	private AggregatedHostMetrics domainMetrics;
	private Integer drivesTotal;
	private Long sizeAvailable;
		
	public DomainEngine() {}
	
	public DomainEngine(UUID id, String ip, String hostname, Datacenter datacenter,
			AggregatedHostMetrics domainMetrics, Integer drivesTotal, Long sizeAvailable) {
		super(id);
		this.ip = ip;
		this.hostname = hostname;
		this.datacenter = datacenter;
		this.domainMetrics = domainMetrics;
		this.drivesTotal = drivesTotal;
		this.sizeAvailable = sizeAvailable;
	}

	public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}

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

	public AggregatedHostMetrics getDomainMetrics() {
		return domainMetrics;
	}

	public void setDomainMetrics(AggregatedHostMetrics domainMetrics) {
		this.domainMetrics = domainMetrics;
	}

	public Integer getDrivesTotal() {
		return drivesTotal;
	}

	public void setDrivesTotal(Integer drivesTotal) {
		this.drivesTotal = drivesTotal;
	}

	public Long getSizeAvailable() {
		return sizeAvailable;
	}

	public void setSizeAvailable(Long sizeAvailable) {
		this.sizeAvailable = sizeAvailable;
	}
}