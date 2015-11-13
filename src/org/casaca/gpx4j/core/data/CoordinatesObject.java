package org.casaca.gpx4j.core.data;

import java.math.BigDecimal;
import java.util.Calendar;

public abstract class CoordinatesObject extends BaseObject implements Comparable<CoordinatesObject> {
	private BigDecimal elevation;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private Calendar time;
	
	private Extensions extensions;
	
	public CoordinatesObject(BigDecimal latitude, BigDecimal longitude){
		if(latitude == null || longitude == null) throw new IllegalArgumentException("Error creating CoordinatesObject. Latitude and longitude must not be null");
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = null;
		this.time = null;
		
		this.extensions = new Extensions();
	}
	
	public CoordinatesObject(BigDecimal latitude, BigDecimal longitude, BigDecimal elevation){
		if(latitude == null || longitude == null || elevation == null) throw new IllegalArgumentException("Error creating CoordinatesObject. Latitude, longitude and elevation must not be null");
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.time = null;
		
		this.extensions = new Extensions();
	}
	
	public CoordinatesObject(BigDecimal latitude, BigDecimal longitude, BigDecimal elevation, Calendar time){
		if(latitude == null || longitude == null || elevation == null) throw new IllegalArgumentException("Error creating CoordinatesObject. Latitude, longitude and elevation must not be null");
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.time = time;
		
		this.extensions = new Extensions();
	}
	
	public BigDecimal getElevation() {
		return this.elevation;
	}
	public void setElevation(BigDecimal elevation) {
		this.elevation = elevation;
	}
	public BigDecimal getLatitude() {
		return this.latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public BigDecimal getLongitude() {
		return this.longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public Calendar getTime() {
		return this.time;
	}
	public void setTime(Calendar time) {
		this.time = time;
	}

	public Extensions getExtensions() {
		return this.extensions;
	}

	//Comparable method
	@Override
	public int compareTo(CoordinatesObject o) {
		if(this.time==null)
			return 1;
		else
			if(o==null || o.getTime()==null)
				return -1;
			else
				return this.getTime().compareTo(o.getTime());
	}

	@Override
	public String toString() {
		return "lat: "+this.latitude+" lon: "+this.longitude+" elevation: "+this.elevation+" date: "+this.time.getTimeInMillis();
	}
}

