package org.casaca.gpx4j.tools.speedo;

import java.math.BigDecimal;
import java.util.Properties;

import org.casaca.gpx4j.core.data.CoordinatesObject;
import org.casaca.gpx4j.core.exception.GpxPropertiesException;
import org.casaca.gpx4j.tools.GpxTools;
import org.casaca.gpx4j.tools.data.MeasurementUnit;
import org.casaca.gpx4j.tools.data.Speed;
import org.casaca.gpx4j.tools.exception.GpxSpeedoException;
import org.casaca.gpx4j.tools.util.Constants;

public class MphSpeedo extends AbstractSpeedo {
	
	private BigDecimal km;
	private BigDecimal mile;

	public MphSpeedo(Properties props) throws GpxSpeedoException, GpxPropertiesException {
		super(props);
		this.km = BigDecimal.valueOf(Double.parseDouble(GpxTools.getTools().getToolsProperties().getProperty(Constants.TOOLS_CONVERSION_MILE_TO_KM, Constants.APPLICATION_DEFAULT_CONVERSION_MILE_TO_KM)));
		this.mile = BigDecimal.valueOf(Double.parseDouble(GpxTools.getTools().getToolsProperties().getProperty(Constants.TOOLS_CONVERSION_KM_TO_MILE, Constants.APPLICATION_DEFAULT_CONVERSION_KM_TO_MILE)));
	}
	
	@Override
	public MeasurementUnit getUnit() {
		return MeasurementUnit.MPH;
	}

	//CONVERTING METHODS
	@Override
	public Speed toMtSeg(Speed speed) {
		if(speed==null || speed.compareTo(Speed.SPEED_ZERO_MPH)==0) return Speed.SPEED_ZERO_METERS_PER_SECOND;
		
		Speed newSpeed = new Speed(speed.getSpeed().multiply(km).divide(BigDecimal.valueOf(3.6), Constants.APPLICATION_BIGDECIMAL_MATH_CONTEXT), MeasurementUnit.MT_SEG);
		newSpeed.setCoordinates(speed.getCoordinates()[0], speed.getCoordinates()[1]);
		
		return newSpeed;
	}

	@Override
	public Speed toMph(Speed speed) {
		return speed;
	}

	@Override
	public Speed toMinKm(Speed speed) {
		if(speed==null || speed.compareTo(Speed.SPEED_ZERO_MPH)==0) return Speed.SPEED_ZERO_MINUTES_PER_KILOMETER;
		
		Speed newSpeed = new Speed(BigDecimal.valueOf(60).divide((speed.getSpeed().multiply(km)), Constants.APPLICATION_BIGDECIMAL_MATH_CONTEXT), MeasurementUnit.MIN_KM);
		newSpeed.setCoordinates(speed.getCoordinates()[0], speed.getCoordinates()[1]);
		
		return newSpeed;
	}

	@Override
	public Speed toMinMile(Speed speed) {
		if(speed==null || speed.compareTo(Speed.SPEED_ZERO_MPH)==0) return Speed.SPEED_ZERO_MINUTES_PER_MILE;
		
		Speed newSpeed = new Speed(BigDecimal.valueOf(60).divide(speed.getSpeed(), Constants.APPLICATION_BIGDECIMAL_MATH_CONTEXT), MeasurementUnit.MIN_MI);
		newSpeed.setCoordinates(speed.getCoordinates()[0], speed.getCoordinates()[1]);
		
		return newSpeed;
	}

	@Override
	public Speed toKmh(Speed speed) {
		if(speed==null || speed.compareTo(Speed.SPEED_ZERO_MPH)==0) return Speed.SPEED_ZERO_KMH;
		
		Speed newSpeed = new Speed(speed.getSpeed().multiply(km), MeasurementUnit.KMH);
		newSpeed.setCoordinates(speed.getCoordinates()[0], speed.getCoordinates()[1]);
		
		return newSpeed;
	}
	//END CONVERTING METHODS

	@Override
	public Speed getSpeed(CoordinatesObject c1, CoordinatesObject c2, boolean storeSpeed) {
		if(c1==null || c2==null) return Speed.SPEED_ZERO_MPH;
		
		Speed speed = super.getSpeed(c1, c2, storeSpeed);
		speed.setSpeed((speed==null || speed.compareTo(Speed.SPEED_ZERO_MPH)==0)?Speed.SPEED_ZERO_MPH.getSpeed():speed.getSpeed().multiply(BigDecimal.valueOf(3.6)).multiply(this.mile));
		speed.setUnit(MeasurementUnit.MPH);
		
		return speed;
	}
}
