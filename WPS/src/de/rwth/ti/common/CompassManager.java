package de.rwth.ti.common;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * This class reads the magnetic sensor and provides azimut angle information
 * 
 */
public class CompassManager implements SensorEventListener {

	public enum Direction {
		NORTH, EAST, SOUTH, WEST
	}

	private final long timeout;
	private SensorManager sensor;
	private Sensor accel;
	private Sensor magnet;
	private float[] gravity;
	private float[] geomagnetic;
	private double azimut;
	private List<SensorValue> values;

	public interface OnCustomEventListener {
		public void onEvent();
	}

	public CompassManager(Activity app, long medianTimeSpan) {
		timeout = medianTimeSpan;
		sensor = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
		accel = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnet = sensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		azimut = 0;
		values = new LinkedList<CompassManager.SensorValue>();
	}

	public void onStart() {
		values.clear();
		sensor.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
		sensor.registerListener(this, magnet, SensorManager.SENSOR_DELAY_UI);
	}

	public void onStop() {
		sensor.unregisterListener(this);
		values.clear();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		cleanValues();
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			gravity = event.values;
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			geomagnetic = event.values;
		}
		if (gravity != null && geomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, gravity,
					geomagnetic);
			if (success == true) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut = orientation[0] * 360.0 / (2.0 * Math.PI);
				values.add(new SensorValue(azimut));
			}
		}
	}

	/**
	 * 
	 * @return Returns the last known azimut in degrees, initialized with zero
	 */
	public double getAzimut() {
		return azimut;
	}

	/**
	 * 
	 * @return Returns the mean azimut in fixed time span
	 */
	public double getMeanAzimut() {
		cleanValues();
		if (values.isEmpty()) {
			return 0;
		}
		double x = 0;
		double y = 0;
		for (SensorValue v : values) {
			double val = v.getValue();
			x += Math.cos(val / 180 * Math.PI);
			y += Math.sin(val / 180 * Math.PI);
		}
		x /= values.size();
		y /= values.size();
		double result = Math.atan2(y, x) * 180 / Math.PI;
		return result;
	}

	private class SensorValue {

		private double value;
		private long time;

		public SensorValue(double value) {
			this.value = value;
			this.time = new Date().getTime();
		}

		public double getValue() {
			return value;
		}

		public long getTime() {
			return time;
		}

	}

	private void cleanValues() {
		long time = new Date().getTime();
		Iterator<SensorValue> itVal = values.iterator();
		while (itVal.hasNext()) {
			SensorValue v = itVal.next();
			if ((time - v.getTime()) > timeout) {
				itVal.remove();
			}
		}
	}

}
