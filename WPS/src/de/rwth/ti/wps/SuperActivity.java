package de.rwth.ti.wps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.ScanManager;
import de.rwth.ti.db.StorageHandler;

public class SuperActivity extends Activity {

	public static final String PACKAGE_NAME = "de.rwth.ti.wps";

	/*
	 * Own classes
	 */
	protected ScanManager scm;
	protected StorageHandler storage;
	protected CompassManager cmgr;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup Wifi
		if (scm == null) {
			scm = new ScanManager(this);
		}

		// Setup database storage
		if (storage == null) {
			storage = new StorageHandler(this);
		}

		// Setup compass manager
		if (cmgr == null) {
			cmgr = new CompassManager(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		storage.onStart();
		scm.onStart();
		cmgr.onStart();
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		storage.onStop();
		scm.onStop();
		cmgr.onStop();
	}

	public ScanManager getScanManager() {
		return scm;
	}

	public StorageHandler getStorage() {
		return storage;
	}

	public CompassManager getCompassManager() {
		return cmgr;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Start other Activities, when the related MenuItem is selected
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_localisation:
			intent = new Intent(this, MainActivity.class);
			break;
		case R.id.action_measure:
			intent = new Intent(this, MeasureActivity.class);
			break;
		case R.id.action_new_floor:
			intent = new Intent(this, NewFloorActivity.class);
			break;
		case R.id.action_debug:
			intent = new Intent(this, DebugActivity.class);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		if (intent != null) {
			startActivity(intent);
		}
		return true;
	}

}
