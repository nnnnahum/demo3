package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatLong {

	private Double lat;
	@JsonProperty("long")
	private Double longVal;
	
	public LatLong() {}
	
	public LatLong(Double lat, Double longVal) {
		this.lat = lat;
		this.longVal = longVal;
	}
	
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLong() {
		return longVal;
	}
	public void setLong(Double longVal) {
		this.longVal = longVal;
	}
}
