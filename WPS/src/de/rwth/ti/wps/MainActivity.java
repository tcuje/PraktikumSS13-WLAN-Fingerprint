package de.rwth.ti.wps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.db.StorageHandler;

/**
 * This is the main activity class
 * 
 */
public class MainActivity extends Activity implements
		ActionBar.OnNavigationListener, OnClickListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	public static final String PACKAGE_NAME = "de.rwth.ti.wps";

	private ScanManager scm;
	private StorageHandler storage;
	private CompassManager cmgr;

	TextView textStatus;
	Button buttonScan;

	/** Called when the activity is first created. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localisation);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3), }), this);

		// Setup UI
		textStatus = (TextView) findViewById(R.id.textStatus);
		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonScan.setOnClickListener(this);

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

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
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
		// TODO GUI don't show debug info on startup
		showDebug();
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		storage.onStop();
		scm.onStop();
		cmgr.onStop();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.buttonScan) {
			// FIXME GUI get real data from gui
			Building b = storage.createBuilding("Haus "
					+ (storage.countBuildings() + 1));
			Floor f = storage.createFloor(b, "Ebene "
					+ (storage.countFloors() + 1), null,
					(storage.countFloors() + 1), 15);
			MeasurePoint mp = storage.createMeasurePoint(f, 0, 0);
			boolean check = scm.startSingleScan(mp);
			if (check == false) {
				Toast.makeText(this, "Fehler beim Scanstart", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Start other Activities, when the related MenuItem is selected
		// TextView textView = (TextView) findViewById(R.id.textStatus);
		String text;
		text = item.getTitle() + "\n" + Integer.toString(item.getItemId())
				+ "\n";

		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_localisation:
			text += "Lokalisation";
			break;
		case R.id.action_measure:
			text += "Messung";
			intent = new Intent(this, MeasureActivity.class);
			break;
		case R.id.action_new_map:
			intent = new Intent(this, NewMapActivity.class);
			break;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			break;
		case R.id.menu_show_debug:
			showDebug();
			return true;
		case R.id.menu_export:
			try {
				storage.exportDatabase("local.sqlite");
				// TODO GUI extract message
				Toast.makeText(getBaseContext(),
						"Datenbank erfolgreich exportiert", Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_import:
			// FIXME GUI get user input for filename
			storage.importDatabase(Environment.getExternalStorageDirectory()
					+ File.separator + "local.sqlite");
			// TODO GUI extract message
			Toast.makeText(getBaseContext(),
					"Datenbank erfolgreich importiert", Toast.LENGTH_SHORT)
					.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

		if (intent != null)
			startActivity(intent);

		textStatus.setText(text);
		return true;
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

	public void showDebug() {
		textStatus.setText("Database:\n");
		textStatus.append("\nBuildings: " + storage.countBuildings() + "\n");
		for (Building b : storage.getAllBuildings()) {
			textStatus.append("Building\t" + b.getId() + "\t" + b.getName()
					+ "\n");
		}
		textStatus.append("\nMaps: " + storage.countFloors() + "\n");
		for (Floor m : storage.getAllFloors()) {
			textStatus.append("Map\t" + m.getId() + "\t" + m.getName() + "\t"
					+ m.getFile() + "\n");
		}
		textStatus.append("\nCheckpoints: " + storage.countMeasurePoints()
				+ "\n");
		for (MeasurePoint cp : storage.getAllMeasurePoints()) {
			textStatus.append("Checkpoint\t" + cp.getId() + "\t"
					+ cp.getFloorId() + "\t" + cp.getPosx() + "\t"
					+ cp.getPosy() + "\n");
		}
		textStatus.append("\nScans: " + storage.countScans() + "\n");
		for (Scan scan : storage.getAllScans()) {
			textStatus.append("Scan\t" + scan.getId() + "\t" + scan.getMpid()
					+ "\t" + scan.getTime() + "\t" + scan.getCompass() + "\n");
		}
		textStatus.append("\nAccessPoints: " + storage.countAccessPoints()
				+ "\n");
		List<AccessPoint> all = storage.getAllAccessPoints();
		for (AccessPoint ap : all) {
			textStatus.append("AP\t" + ap.getId() + "\t" + ap.getScanId()
					+ "\t" + ap.getBssid() + "\t" + ap.getLevel() + "\t"
					+ ap.getFreq() + "\t'" + ap.getSsid() + "'\t"
					+ ap.getProps() + "\n");
		}
		if (all.size() > 0) {
			String bssid = all.get(0).getBssid();
			List<AccessPoint> first = storage.getAccessPoint(bssid);
			textStatus.append("\n" + bssid + "\n");
			for (AccessPoint ap : first) {
				textStatus.append("AP\t" + ap.getId() + "\t" + ap.getScanId()
						+ "\t" + ap.getBssid() + "\t" + ap.getLevel() + "\n");
			}
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		// TextView textView = (TextView) findViewById(R.id.textStatus);
		showDebug();
		// Fragment fragment = new DummySectionFragment();
		// Bundle args = new Bundle();
		// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		// fragment.setArguments(args);
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.container, fragment).commit();
		return true;
	}

}
