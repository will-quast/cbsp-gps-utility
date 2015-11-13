package org.casaca.gpx4j.core.data;

public class Fix extends BaseObject {
	public static final int FIX_NONE = 1;
	public static final int FIX_2D = 2;
	public static final int FIX_3D = 3;
	public static final int FIX_DGPS = 4;
	public static final int FIX_PPS = 5;
	
	public static final String VALUE_FIX_NONE = "NONE";
	public static final String VALUE_FIX_2D = "2D";
	public static final String VALUE_FIX_3D = "3D";
	public static final String VALUE_FIX_DGPS = "DGPS";
	public static final String VALUE_FIX_PPS = "PPS";
	
	private Integer fix;
	
	public static Fix createFix(String text) throws IllegalArgumentException {
		if(text == null)
			throw new IllegalArgumentException("Error creating fix. Argument is not valid. Fix must be NONE, 2D, 3D, DGPS or PPS");
		
		Integer choice = null;
		if(text.toUpperCase().equals(VALUE_FIX_NONE))
			choice = Integer.valueOf(FIX_NONE);
		else if(text.toUpperCase().equals(VALUE_FIX_2D))
			choice = Integer.valueOf(FIX_2D);
		else if(text.toUpperCase().equals(VALUE_FIX_3D))
			choice = Integer.valueOf(FIX_3D);
		else if(text.toUpperCase().equals(VALUE_FIX_DGPS))
			choice = Integer.valueOf(FIX_DGPS);
		else if(text.toUpperCase().equals(VALUE_FIX_PPS))
			choice = Integer.valueOf(FIX_PPS);
		else
			choice = Integer.valueOf(FIX_NONE);
		
		return new Fix(choice);
	}
	
	public Fix(Integer fix) throws IllegalArgumentException{
		switch(fix){
		case FIX_NONE:
			this.fix=FIX_NONE;
			break;
		case FIX_2D:
			this.fix=FIX_2D;
			break;
		case FIX_3D:
			this.fix=FIX_3D;
			break;
		case FIX_DGPS:
			this.fix=FIX_DGPS;
			break;
		case FIX_PPS:
			this.fix=FIX_PPS;
			break;
			default:
				throw new IllegalArgumentException("Fix must be NONE (1), 2D (2), 3D (3), DGPS (4) or PPS (5)");
		}
	}
	
	public Integer getFix(){
		return this.fix;
	}
	
	public String toString(){
		switch(fix){
			case FIX_NONE:
				return VALUE_FIX_NONE;
			case FIX_2D:
				return VALUE_FIX_2D;
			case FIX_3D:
				return VALUE_FIX_3D;
			case FIX_DGPS:
				return VALUE_FIX_DGPS;
			case FIX_PPS:
				return VALUE_FIX_PPS;
			default:
				return VALUE_FIX_NONE;
		}
	}
}
