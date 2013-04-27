package de.rwth.ti;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassManager implements SensorEventListener {

	private SensorManager sensor;
	private Sensor accel;
	private Sensor magnet;
	private float[] gravity;
	private float[] geomagnetic;
	private float azimut;

	public CompassManager(Wavi app) {
		sensor = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
		azimut = 0;
	}

	public void onStart() {
		sensor.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
		sensor.registerListener(this, magnet, SensorManager.SENSOR_DELAY_UI);
	}

	public void onStop() {
		sensor.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
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
				azimut = orientation[0];
			}
		}
	}

	public float getAzimut() {
		return azimut;
	}

}
