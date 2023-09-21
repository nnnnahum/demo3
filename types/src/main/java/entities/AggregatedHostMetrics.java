package entities;

public class AggregatedHostMetrics {

	private Integer totalHosts;
	private Integer totalCPU;
	private Double totalRAM;
	private Integer maxCPUInHost;
	private Double maxRAMInHost;
	
	public AggregatedHostMetrics () {}
	public AggregatedHostMetrics (Integer totalHosts, Integer totalCPU, 
			Double totalRAM, Integer maxCPUInHost, Double maxRAMInHost) {
		this.totalHosts = totalHosts;
		this.totalCPU = totalCPU;
		this.totalRAM = totalRAM;
		this.maxCPUInHost = maxCPUInHost;
		this.maxRAMInHost = maxRAMInHost;
	}
	
	public Integer getTotalHosts() {
		return totalHosts;
	}
	public void setTotalHosts(Integer totalHosts) {
		this.totalHosts = totalHosts;
	}
	public Integer getTotalCPU() {
		return totalCPU;
	}
	public void setTotalCPU(Integer totalCPU) {
		this.totalCPU = totalCPU;
	}
	public Double getTotalRAM() {
		return totalRAM;
	}
	public void setTotalRAM(Double totalRAM) {
		this.totalRAM = totalRAM;
	}
	public Integer getMaxCPUInHost() {
		return maxCPUInHost;
	}
	public void setMaxCPUInHost(Integer maxCPUInHost) {
		this.maxCPUInHost = maxCPUInHost;
	}
	public Double getMaxRAMInHost() {
		return maxRAMInHost;
	}
	public void setMaxRAMInHost(Double maxRAMInHost) {
		this.maxRAMInHost = maxRAMInHost;
	}
}
