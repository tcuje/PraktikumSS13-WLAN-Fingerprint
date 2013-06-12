package de.rwth.ti.wps;

import java.util.List;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
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
		boolean result = super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.menu_import) {
			showDebug();
		}
		return result;
	}

}
