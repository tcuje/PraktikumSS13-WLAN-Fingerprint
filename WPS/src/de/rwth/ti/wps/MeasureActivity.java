package de.rwth.ti.wps;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.Constants;
import de.rwth.ti.common.IPMapView;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

public class MeasureActivity extends SuperActivity implements
		OnItemSelectedListener {

	private List<Building> buildingList;
	private ArrayAdapter<CharSequence> buildingAdapter;
	private Spinner buildingSpinner;
	private Building buildingSelected;
	private List<Floor> floorList;
	private ArrayAdapter<CharSequence> floorAdapter;
	private Spinner floorSpinner;
	private Floor floorSelected;
	private Button btMeasure;
	private IPMapView mapView;
	private TextView directionText;
	private TextView compassText;
	private CompassManager.Direction direction;
	private BroadcastReceiver wifiReceiver;
	private MeasurePoint lastMP;
	private AlertDialog waitDialog;
	private double lastAzimuth;
	private Timer timer;
	private TimerTask updateComp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measure);

		buildingAdapter = new ArrayAdapter<CharSequence>(this,
				R.layout.spinner_item);
		buildingSpinner = (Spinner) findViewById(R.id.buildingSelectSpinner);
		buildingSpinner.setAdapter(buildingAdapter);
		buildingSpinner.setOnItemSelectedListener(this);

		floorAdapter = new ArrayAdapter<CharSequence>(this,
				R.layout.spinner_item);
		floorSpinner = (Spinner) findViewById(R.id.floorSelectSpinner);
		floorSpinner.setAdapter(floorAdapter);
		floorSpinner.setOnItemSelectedListener(this);

		btMeasure = (Button) findViewById(R.id.measure_button);

		mapView = (IPMapView) findViewById(R.id.map_view);
		mapView.setMeasureMode(true);

		directionText = (TextView) findViewById(R.id.direction_text_view);

		compassText = (TextView) findViewById(R.id.compass_text);

		timer = new Timer();

		direction = CompassManager.Direction.NORTH;
		wifiReceiver = new MyReceiver();
		updateCompass();
	}

	private void updateCompass() {
		lastAzimuth = this.getCompassManager().getAzimut();
		compassText.setText("N " + (int) lastAzimuth + "°");
		// compare azimuth to direction
		btMeasure.setEnabled(false);
		switch (direction) {
		case NORTH:
			if (lastAzimuth > -Constants.ANGLE_DIFF
					&& lastAzimuth < Constants.ANGLE_DIFF) {
				btMeasure.setEnabled(true);
			}
			break;
		case EAST:
			if (lastAzimuth > 90 - Constants.ANGLE_DIFF
					&& lastAzimuth < 90 + Constants.ANGLE_DIFF) {
				btMeasure.setEnabled(true);
			}
			break;
		case SOUTH:
			if (lastAzimuth > 180 - Constants.ANGLE_DIFF
					|| lastAzimuth < -180 + Constants.ANGLE_DIFF) {
				btMeasure.setEnabled(true);
			}
			break;
		case WEST:
			if (lastAzimuth > -90 - Constants.ANGLE_DIFF
					&& lastAzimuth < -90 + Constants.ANGLE_DIFF) {
				btMeasure.setEnabled(true);
			}
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		buildingAdapter.clear();
		buildingList = storage.getAllBuildings();
		for (Building b : buildingList) {
			buildingAdapter.add(b.getName());
		}
		if (buildingList.size() == 0) {
			buildingSelected = null;
		} else {
			buildingSelected = buildingList.get(0);
		}
		this.registerReceiver(this.wifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		updateComp = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						updateCompass();
					}
				});
			}
		};
		timer.schedule(updateComp, 0, 500);
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			this.unregisterReceiver(wifiReceiver);
		} catch (IllegalArgumentException ex) {
			// just ignore it
		}
		if (updateComp != null) {
			updateComp.cancel();
			updateComp = null;
		}
	}

	public void measure(View view) {
		if (view.getId() == R.id.measure_button) {
			// check if building/floor is selected
			if (buildingSelected == null) {
				Toast.makeText(this, R.string.error_empty_input,
						Toast.LENGTH_LONG).show();
				return;
			}
			if (floorSelected == null) {
				Toast.makeText(this, R.string.error_empty_input,
						Toast.LENGTH_LONG).show();
				return;
			}
			float[] p = mapView.getMeasurePoint();
			if (p == null) {
				Toast.makeText(this, R.string.error_no_measure_point,
						Toast.LENGTH_LONG).show();
				return;
			}
			boolean check = scm.startSingleScan();
			if (check == false) {
				Toast.makeText(this, R.string.error_scanning, Toast.LENGTH_LONG)
						.show();
			} else {
				if (lastMP == null) {
					lastMP = storage.createMeasurePoint(floorSelected, p[0],
							p[1]);
				}
				if (waitDialog != null) {
					waitDialog.dismiss();
				}
				waitDialog = new AlertDialog.Builder(this).setTitle(
						R.string.scan_wait).show();
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (parent == buildingSpinner) {
			buildingSelected = buildingList.get(pos);
			// update floor spinner
			floorList = storage.getFloors(buildingSelected);
			floorAdapter.clear();
			for (Floor f : floorList) {
				floorAdapter.add(f.getName());
			}
			if (floorList.size() == 0) {
				floorSelected = null;
			} else {
				onItemSelected(floorSpinner, view, 0, id);
			}
		} else if (parent == floorSpinner) {
			floorSelected = floorList.get(pos);
			// update map view
			byte[] file = floorSelected.getFile();
			if (file != null) {
				ByteArrayInputStream bin = new ByteArrayInputStream(file);
				mapView.newMap(bin, storage.getMeasurePoints(floorSelected));
			} else {
				Toast.makeText(this, R.string.error_no_floor_file,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent == buildingSpinner) {
			// clear floor spinner
			floorList.clear();
			floorAdapter.clear();
			buildingSelected = null;
			floorSelected = null;
		} else if (parent == floorSpinner) {
			buildingSelected = null;
			floorSelected = null;
			mapView.clear();
		}
	}

	private class MyReceiver extends BroadcastReceiver {

		private WifiManager wifi = MeasureActivity.this.getScanManager()
				.getWifi();

		@Override
		public void onReceive(Context c, Intent intent) {
			List<ScanResult> results = wifi.getScanResults();
			// measure mode, save the access points to database
			if (results != null && !results.isEmpty()) {
				if (lastMP != null && waitDialog != null
						&& waitDialog.isShowing()) {
					Date d = new Date();
					Scan scan = MeasureActivity.this.getStorage().createScan(
							lastMP,
							d.getTime() / 1000,
							MeasureActivity.this.getCompassManager()
									.getAzimut());
					for (ScanResult result : results) {
						MeasureActivity.this.getStorage().createAccessPoint(
								scan, result.BSSID, result.level,
								result.frequency, result.SSID,
								result.capabilities);
					}
					waitDialog.dismiss();
					waitDialog = null;
					Toast.makeText(MeasureActivity.this,
							R.string.success_scanning, Toast.LENGTH_SHORT)
							.show();
					if (direction.ordinal() + 1 > CompassManager.Direction
							.values().length) {
						lastMP = null;
					}
					direction = CompassManager.Direction.values()[(direction
							.ordinal() + 1) % 4];
					switch (direction) {
					case NORTH:
						directionText.setText(R.string.measure_face_north);
						break;
					case EAST:
						directionText.setText(R.string.measure_face_east);
						break;
					case SOUTH:
						directionText.setText(R.string.measure_face_south);
						break;
					case WEST:
						directionText.setText(R.string.measure_face_west);
						break;
					default:
						break;
					}
				}
			} else {
				Toast.makeText(MeasureActivity.this, R.string.scan_no_ap,
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
