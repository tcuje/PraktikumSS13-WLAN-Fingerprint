package de.rwth.ti.wps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.common.Constants;
import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

/**
 * This is the main activity class
 * 
 */
public class DebugActivity extends SuperActivity {

	private TextView textStatus;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		// Setup UI
		textStatus = (TextView) findViewById(R.id.textStatus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debug, menu);
		return true;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		showDebug();
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_export:
			try {
				storage.exportDatabase(Constants.LOCAL_DB_NAME);
				Toast.makeText(getBaseContext(),
						R.string.database_export_success, Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_import:
			storage.importDatabase(Environment.getDataDirectory()
					+ File.separator + Constants.LOCAL_DB_NAME);
			Toast.makeText(getBaseContext(), R.string.database_import_success,
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_show_debug:
			showDebug();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
