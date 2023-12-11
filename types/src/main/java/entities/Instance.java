package entities;

import java.util.UUID;

public class Instance extends BaseEntity {

	public static final String RESOURCE = "/instance";
	
	public Instance() {}
	
	public Instance(UUID id, String currentUsage, String currentCapacity,
			Integer cpuTotal, Integer cpuAvailable, 
			Double ramTotal, Double ramAvailable,
			Customer tenant, Host host, Datacenter datacenter) {
		super(id);
		this.currentUsage = currentUsage;
		this.currentCapacity = currentCapacity;
		this.tenant = tenant;
		this.host = host;
		this.setDatacenter(datacenter);
	}
	
	private String currentUsage;
	private String currentCapacity;
	private Integer cpuTotal;
	private Integer cpuAvailable;
	private Double ramTotal;
	private Double ramAvailable;
	private Customer tenant;
	private Host host;
	private Datacenter datacenter;
		
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

	public Customer getTenant() {
		return tenant;
	}

	public void setTenant(Customer tenant) {
		this.tenant = tenant;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}
}
