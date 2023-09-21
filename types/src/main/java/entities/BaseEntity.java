package entities;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class BaseEntity {

	@Id
	private UUID id;
	
	public BaseEntity(){}
	public BaseEntity(UUID id) {
		this.id = id;
	}
		
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
