package org.casaca.gpx4j.core.data;

public abstract class BaseObject {
	private Long id;
	
	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}
}
