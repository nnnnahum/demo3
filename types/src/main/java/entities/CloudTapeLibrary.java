package entities;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cloud_tape_library")
public class CloudTapeLibrary extends BaseEntity {

	public static final String RESOURCE = "/cloudTapeLibrary";
	
	public CloudTapeLibrary() {}
	
	public CloudTapeLibrary(UUID id, String name, Boolean compression, 
			Boolean encryption, Datacenter datacenter, 
			Long size, Customer customer) {
		super(id);
		this.name = name;
		this.compression = compression;
		this.encryption = encryption;
		this.datacenter = datacenter;
		this.size = size;
		this.customer = customer;
	}
	
	private String name;
	private Boolean compression;
	private Boolean encryption;
	private Datacenter datacenter;
	private Long size;
	private Customer customer;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getCompression() {
		return compression;
	}

	public void setCompression(Boolean compression) {
		this.compression = compression;
	}

	public Boolean getEncryption() {
		return encryption;
	}

	public void setEncryption(Boolean encryption) {
		this.encryption = encryption;
	}

	public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
	
	public Customer getCustomer() {
		return this.customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
