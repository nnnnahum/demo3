package entities;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sessions")
public class Session extends BaseEntity {

	public static final String RESOURCE = "/sessions";
	
	public Session() {}
	
	public Session(UUID id, UUID userId, Date createdAt, Date expiresAt) {
		super(id);
		this.userId = userId;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}
	
	UUID userId;
	Date createdAt;
	Date expiresAt;

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	
}