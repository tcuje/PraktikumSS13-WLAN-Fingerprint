package de.rwth.ti;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Map;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.db.StorageHandler;

/**
 * This is the main activity class
 * 
 */
public class Wavi extends Activity implements OnClickListener {

	public static final String PACKAGE_NAME = "de.rwth.ti";

	private ScanManager scm;
	private StorageHandler storage;
	private CompassManager cmgr;

	// FIXME make this private and replace with usefull functions for the gui
	TextView textStatus;
	Button buttonScan;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		storage.onStart();
		scm.onStart();
		cmgr.onStart();
		// FIXME don't show debug info on startup
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
			// FIXME get real data from gui
			scm.startSingleScan(storage.createMeasurePoint(null, 0, 0));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_show_debug:
			showDebug();
			return true;
		case R.id.menu_export:
			try {
				storage.exportDatabase("local.sqlite");
				Toast.makeText(getBaseContext(),
						"Datenbank erfolgreich exportiert", Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_import:
			try {
				storage.importDatabase("local.sqlite");
				Toast.makeText(getBaseContext(),
						"Datenbank erfolgreich importiert", Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		textStatus.setText("Maps: " + storage.countMaps() + "\n");
		for (Map m : storage.getAllMaps()) {
			textStatus.append("Map\t" + m.getId() + "\t" + m.getName() + "\t "
					+ m.getFile() + "\n");
		}
		textStatus
				.append("\nCheckpoints: " + storage.countCheckpoints() + "\n");
		for (MeasurePoint cp : storage.getAllMeasurePoints()) {
			textStatus.append("Checkpoint\t" + cp.getId() + "\t"
					+ cp.getMapId() + "\t" + cp.getPosx() + "\t" + cp.getPosy()
					+ "\n");
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
		String bssid = all.get(0).getBssid();
		List<AccessPoint> first = storage.getAccessPoint(bssid);
		textStatus.append("\n" + bssid + "\n");
		for (AccessPoint ap : first) {
			textStatus.append("AP\t" + ap.getId() + "\t" + ap.getScanId()
					+ "\t" + ap.getBssid() + "\t" + ap.getLevel() + "\n");
		}
	}

}