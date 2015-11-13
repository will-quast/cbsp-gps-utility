package org.casaca.gpx4j.core.exception;

import java.io.FileNotFoundException;

public class GpxFileNotFoundException extends FileNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6585173525939830804L;
	
	public GpxFileNotFoundException(String message){
		super(message);
	}
	
	public GpxFileNotFoundException(){
		super();
	}
}
