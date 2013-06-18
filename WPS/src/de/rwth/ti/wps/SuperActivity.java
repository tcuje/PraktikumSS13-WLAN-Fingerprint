package de.rwth.ti.wps;

import android.app.Activity;
import android.os.Bundle;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.ScanManager;
import de.rwth.ti.db.StorageHandler;

public abstract class SuperActivity extends Activity {

	/*
	 * Own classes
	 */
	protected ScanManager scm;
	protected StorageHandler storage;
	protected CompassManager cmgr;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState, true, true, true);
	}

	protected void onCreate(Bundle savedInstanceState, boolean hasCompass) {
		onCreate(savedInstanceState, hasCompass, true, true);
	}

	protected void onCreate(Bundle savedInstanceState, boolean hasCompass,
			boolean hasScan) {
		onCreate(savedInstanceState, hasCompass, hasScan, true);
	}

	protected void onCreate(Bundle savedInstanceState, boolean hasCompass,
			boolean hasScan, boolean hasStorage) {
		super.onCreate(savedInstanceState);

		// Setup Wifi
		if (scm == null && hasScan) {
			scm = new ScanManager(this);
		}

		// Setup database storage
		if (storage == null && hasStorage) {
			storage = new StorageHandler(this);
		}

		// Setup compass manager
		if (cmgr == null && hasCompass) {
			cmgr = new CompassManager(this);
		}
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

}
