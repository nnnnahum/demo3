package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hosts")
public class Host extends BaseEntity {
	
	public static final String RESOURCE = "/hosts";
	public static final String HOST_METRICS_RESOURCE = "/hostmetrics";
	public static final String DATACENTER_RESOURCE = "/datacenters/{datacenterId}/hosts";
	
	public Host() {}
	
	public Host(UUID id, String ip, String hostname, String location, String currentUsage, 
			String currentCapacity, Integer cpuTotal, Integer cpuAvailable, Double ramTotal,
			Double ramAvialable, Datacenter datacenter) {
		super(id);
		this.ip = ip;
		this.currentUsage = currentUsage;
		this.currentCapacity = currentCapacity;
		this.datacenter = datacenter;
		this.cpuTotal = cpuTotal;
		this.cpuAvailable = cpuAvailable;
		this.ramTotal = ramTotal;
		this.ramAvailable = ramAvialable;
	}
	
	private String ip;
	private String hostname;
	private String currentUsage;
	private String currentCapacity;
	private Integer cpuTotal;
	private Integer cpuAvailable;
	private Double ramTotal;
	private Double ramAvailable;
	private Datacenter datacenter;
	
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
	
	
	public String getCurrentUsage() {
		return currentUsage;
	}

	public void setCurrentUsage(String currentUsage) {
		this.currentUsage = currentUsage;
	}
	public String getCurrentCapacity() {
		return currentCapacity;
	}
	public void setCurrentCapacity(String currentCapacity) {
		this.currentCapacity = currentCapacity;
	}
	public Integer getCpuTotal() {
		return cpuTotal;
	}

	public void setCpuTotal(Integer cpuTotal) {
		this.cpuTotal = cpuTotal;
	}

	public Integer getCpuAvailable() {
		return cpuAvailable;
	}

	public void setCpuAvailable(Integer cpuAvailable) {
		this.cpuAvailable = cpuAvailable;
	}

	public Double getRamTotal() {
		return ramTotal;
	}

	public void setRamTotal(Double ramTotal) {
		this.ramTotal = ramTotal;
	}

	public Double getRamAvailable() {
		return ramAvailable;
	}

	public void setRamAvailable(Double ramAvailable) {
		this.ramAvailable = ramAvailable;
	}

	public Datacenter getDatacenter() {
		return datacenter;
	}
	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}
}
