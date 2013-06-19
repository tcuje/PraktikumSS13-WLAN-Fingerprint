package de.rwth.ti.wps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.Constants;
import de.rwth.ti.common.IPMapView;
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
	private BroadcastReceiver wifiReceiver;

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
		btCenter = (ImageButton) findViewById(R.id.centerButton);
		wifiReceiver = new MyReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		if (checkLoc.isChecked() == true) {
			getScanManager().startAutoScan(Constants.AUTO_SCAN_SEC);
		}
		this.registerReceiver(wifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		getScanManager().stopAutoScan();
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
			} else {
				getScanManager().stopAutoScan();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_localisation:
			item.setChecked(!item.isChecked());
			if (item.isChecked() == true) {
				getScanManager().startAutoScan(Constants.AUTO_SCAN_SEC);
			} else {
				getScanManager().stopAutoScan();
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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
				Location myLoc = new Location(storage);
				LocationResult myLocRes = myLoc.getLocation(results,
						(int) comp.getAzimut(), 0);
				if (myLocRes == null) {
					Toast.makeText(MainActivity.this,
							"Position nicht gefunden", Toast.LENGTH_LONG)
							.show();
				} else {
					Floor map = myLocRes.getMap();
					if (lastMap == null || map.getId() != lastMap.getId()) {
						// map has changed reload it
						byte[] file = myLocRes.getMap().getFile();
						if (file != null) {
							ByteArrayInputStream bin = new ByteArrayInputStream(
									file);
							viewMap.newMap(bin);
							List<MeasurePoint> mpl = storage.getMeasurePoints(map);
							for(MeasurePoint mp : mpl){
								viewMap.addOldPoint(new PointF((float)mp.getPosx(),(float)mp.getPosy()));
							}
						} else {
							Toast.makeText(MainActivity.this,
									R.string.error_no_floor_file,
									Toast.LENGTH_LONG).show();
						}
					}
					viewMap.setPoint((float) myLocRes.getX(),
							(float) myLocRes.getY());
					if (lastMap == null || map.getId() != lastMap.getId()) {
						// map has changed focus position once
						lastMap = map;
						viewMap.focusPoint();
					}
				}
			}
		}
	}

	public void centerPosition(View view) {
		if (view == btCenter) {
			viewMap.focusPoint();
		}
	}

}
