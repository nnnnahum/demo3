package entities;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
public class Event extends BaseEntity{

	public static final String RESOURCE = "/events";
	
	public Event() {}
	public Event(String name, Date created, Object result, Organization org) {
		this.name = name;
		this.created = created;
		this.result = result;
		this.org = org;
	}
	
	private String name;
	private Date created;
//	Maybe introduce severity to trigger different notifications 
	// private Integer severity;
	private Object result;
	private Organization org;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Organization getOrg() {
		return org;
	}
	public void setOrg(Organization org) {
		this.org = org;
	}
	
	
}
