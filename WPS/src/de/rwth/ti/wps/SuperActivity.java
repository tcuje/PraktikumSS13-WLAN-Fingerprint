package de.rwth.ti.wps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.Constants;
import de.rwth.ti.common.ScanManager;
import de.rwth.ti.db.StorageHandler;

/**
 * This is the super activity for all other acitivities
 * 
 */
public abstract class SuperActivity extends Activity {

	/*
	 * Own classes
	 */
	private ScanManager scm;
	private StorageHandler storage;
	private CompassManager cmgr;

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
			storage = new StorageHandler(this, Constants.DB_NAME);
		}

		// Setup compass manager
		if (cmgr == null && hasCompass) {
			cmgr = new CompassManager(this, Constants.COMPASS_TIMESPAN);
		}
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		if (cmgr != null) {
			cmgr.onStart();
		}
		if (scm != null) {
			scm.onStart();
		}
		if (storage != null) {
			// start async task to validate the database
			final ProgressDialog waitDialog = ProgressDialog.show(this, "",
					getString(R.string.please_wait));
			waitDialog.setCancelable(false);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
					final boolean dbCheck = getStorage().onStart();
					runOnUiThread(new Runnable() {
						public void run() {
							if (waitDialog != null) {
								waitDialog.dismiss();
							}
							if (dbCheck == false) {
								alert.show();
							}
						}
					});
				}
			});
			t.start();
		}
		// // launch default activity for debugging only
		// Intent intent = new Intent(this, MeasureActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// startActivity(intent);
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		if (cmgr != null) {
			cmgr.onStop();
		}
		if (scm != null) {
			scm.onStop();
		}
		if (storage != null) {
			storage.onStop();
		}
	}

	public CompassManager getCompassManager() {
		return cmgr;
	}

	public ScanManager getScanManager() {
		return scm;
	}

	public StorageHandler getStorage() {
		return storage;
	}

	protected String createFloorNameFromLevel(int level) {
		String tString = "";
		if (level < 0) {
			tString = String.valueOf((-1) * level) + ". "
					+ getString(R.string.floor_basement);
		} else if (level > 0) {
			tString = String.valueOf(level) + ". "
					+ getString(R.string.floor_upper);
		} else {
			tString = getString(R.string.floor_ground);
		}
		return tString;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Start other Activities, when the related MenuItem is selected
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_localisation:
			if (this.getClass() != MainActivity.class) {
				intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			break;
		case R.id.action_measure:
			intent = new Intent(this, MeasureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			break;
		case R.id.action_new_floor:
			intent = new Intent(this, NewFloorActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			break;
		case R.id.action_data:
			intent = new Intent(this, DataActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			break;
		case R.id.action_debug:
			intent = new Intent(this, DebugActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
