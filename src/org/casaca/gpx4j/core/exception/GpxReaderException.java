package org.casaca.gpx4j.core.exception;

public class GpxReaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3137364129788613594L;

	public GpxReaderException(){
		super();
	}
	
	public GpxReaderException(String message){
		super(message);
	}
	
	public GpxReaderException(String message, Throwable t){
		super(message, t);
	}
	
	public GpxReaderException(Throwable clazz){
		super(clazz);
	}
}
