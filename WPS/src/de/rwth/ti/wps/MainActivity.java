package de.rwth.ti.wps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;
import de.rwth.ti.common.Cardinal;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.Constants;
import de.rwth.ti.common.IPMapView;
import de.rwth.ti.common.QualityCheck;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.StorageHandler;
import de.rwth.ti.loc.Location;
import de.rwth.ti.loc.LocationResult;

/**
 * This is the main activity class
 * 
 */
public class MainActivity extends SuperActivity implements
		OnCheckedChangeListener {

	private CheckBox checkLoc;
	private IPMapView viewMap;
	private ImageButton btCenter;
	private Button btZoom;
	private BroadcastReceiver wifiReceiver;
	private ProgressDialog waitDialog;
	private int control;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// create app sd directory
		File sdDir = new File(Constants.SD_APP_DIR);
		if (sdDir.exists() == false) {
			if (sdDir.mkdirs() == false) {
				Toast.makeText(this, R.string.error_sd_dir, Toast.LENGTH_LONG)
						.show();
			}
		}
		checkLoc = (CheckBox) findViewById(R.id.toggleLocalization);
		checkLoc.setOnCheckedChangeListener(this);
		viewMap = (IPMapView) findViewById(R.id.viewMap);
		viewMap.setMeasureMode(false);
		viewMap.setOnScaleChangeListener(new ScaleChangeListener());
		btCenter = (ImageButton) findViewById(R.id.centerButton);
		btZoom = (Button) findViewById(R.id.zoomButton);
		btZoom.setText("x1.0");
		wifiReceiver = new MyReceiver();
		control = 0;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		if (checkLoc.isChecked() == true) {
			getScanManager().startAutoScan(Constants.AUTO_SCAN_SEC);
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		// start async task to validate the database
		waitDialog = ProgressDialog.show(this, "",
				getString(R.string.please_wait));
		waitDialog.setCancelable(false);
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage(R.string.error_db_failure);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		final AlertDialog alert = builder.create();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final boolean dbCheck = getStorage().validate();
				runOnUiThread(new Runnable() {
					public void run() {
						if (waitDialog != null) {
							waitDialog.dismiss();
							waitDialog = null;
						}
						if (dbCheck == false) {
							alert.show();
						} else {
							MainActivity.this
									.registerReceiver(
											wifiReceiver,
											new IntentFilter(
													WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
						}
					}
				});
			}
		});
		t.start();
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		getScanManager().stopAutoScan();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		try {
			this.unregisterReceiver(wifiReceiver);
		} catch (IllegalArgumentException ex) {
			// just ignore it
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean state) {
		if (view == checkLoc) {
			if (state == true) {
				getScanManager().startAutoScan(Constants.AUTO_SCAN_SEC);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			} else {
				getScanManager().stopAutoScan();
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
			// research for building and floor
			control = 1;
		}
	}

	private class MyReceiver extends BroadcastReceiver {

		private WifiManager wifi = MainActivity.this.getScanManager().getWifi();
		private CompassManager comp = MainActivity.this.getCompassManager();
		private StorageHandler sth = MainActivity.this.getStorage();
		private Floor lastMap;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (checkLoc.isChecked() == true) {
				List<ScanResult> results = wifi.getScanResults();
				Location myLoc = new Location(sth);
				Cardinal direction = Cardinal.getFromAzimuth(comp
						.getMeanAzimut());
				long start = System.currentTimeMillis();
				LocationResult myLocRes = myLoc.getLocation(results, direction,
						control);
				control = 0;
				if (myLocRes == null) {
					Toast.makeText(MainActivity.this,
							"Position nicht gefunden", Toast.LENGTH_LONG)
							.show();
				} else {
					long stop = System.currentTimeMillis();
					Floor map = myLocRes.getFloor();
					long mStart = System.currentTimeMillis();
					if (lastMap == null || map.getId() != lastMap.getId()) {
						// map has changed reload it
						byte[] file = myLocRes.getFloor().getFile();
						if (file != null) {
							ByteArrayInputStream bin = new ByteArrayInputStream(
									file);
							viewMap.newMap(bin);
							List<MeasurePoint> mpl = getStorage()
									.getMeasurePoints(map);
							for (MeasurePoint mp : mpl) {
								mp.setQuality(QualityCheck.getQuality(
										getStorage(), mp));
								viewMap.addOldPoint(mp);
							}
						} else {
							Toast.makeText(MainActivity.this,
									R.string.error_no_floor_file,
									Toast.LENGTH_LONG).show();
						}
					}
					long mStop = System.currentTimeMillis();
					viewMap.setPoint(myLocRes);
					if (lastMap == null || map.getId() != lastMap.getId()) {
						// map has changed focus position once
						lastMap = map;
						viewMap.zoomPoint();
					}
					Toast.makeText(
							MainActivity.this,
							"Loc: " + (stop - start) + "ms\nMap: "
									+ (mStop - mStart) + "ms",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public void centerPosition(View view) {
		if (view == btCenter) {
			viewMap.focusPoint();
		}
	}

	public void zoomPosition(View view) {
		if (view == btZoom) {
			viewMap.zoomPoint();
		}
	}

	private class ScaleChangeListener implements
			IPMapView.OnScaleChangeListener {

		@Override
		public void onScaleChange(float scale) {
			if (btZoom == null) {
				// do nothing
				return;
			}
			String zStr = Float.toString(scale);
			int ind = zStr.indexOf(".");
			if (ind != -1) {
				int len = Math.min(ind + 3, zStr.length());
				zStr = zStr.substring(0, len);
			}
			zStr = "x" + zStr;
			btZoom.setText(zStr);
		}

	}

}
