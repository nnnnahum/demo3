package entities;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
public class Task extends BaseEntity {
	public static final String RESOURCE = "/tasks";
	
	public Task() {}
	
	public Task(UUID id, String name, Boolean isCancelable, Boolean cancelRequested,
			TaskStatus status, Integer progress, Object results,
			User initiatedBy, Organization org,
			Date createdAt, Date lastUpdated) {
		super(id);
		this.name = name;
		this.isCancelable = isCancelable;
		this.cancelRequested = cancelRequested;
		this.status = status;
		this.progress = progress;
		this.results = results;
		this.initiatedBy = initiatedBy;
		this.org = org;
		this.createdAt = createdAt;
		this.lastUpdated = lastUpdated;
	}
	
	private String name;
	private Boolean isCancelable;
	private Boolean cancelRequested;
	private TaskStatus status;
	private Integer progress;
	private Object results;
	private User initiatedBy;
	private Organization org;
	private Date createdAt;
	private Date lastUpdated;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsCancelable() {
		return isCancelable;
	}
	public void setIsCancelable(Boolean isCancelable) {
		this.isCancelable = isCancelable;
	}
	public Boolean getCancelRequested() {
		return cancelRequested;
	}
	public void setCancelRequested(Boolean cancelRequested) {
		this.cancelRequested = cancelRequested;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public Integer getProgress() {
		return progress;
	}
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	public Object getResults() {
		return results;
	}
	public void setResults(Object results) {
		this.results = results;
	}

	public User getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(User initiatedBy) {
		this.initiatedBy = initiatedBy;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization parentOrganization) {
		this.org = parentOrganization;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
