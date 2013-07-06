package de.rwth.ti.wps;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;
import de.rwth.ti.common.DatabaseTransformer;
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

	private WebView dataView;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		// Setup UI
		dataView = (WebView) findViewById(R.id.debug_data_view);
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
		dataView.loadData(getString(R.string.please_wait), "text/html", null);
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder("<nobr>Database:<br/>");
				sb.append("Buildings: " + getStorage().countAllBuildings()
						+ "<br/>");
				sb.append("Floors: " + getStorage().countAllFloors() + "<br/>");
				sb.append("MeasurePoints: "
						+ getStorage().countAllMeasurePoints() + "<br/>");
				sb.append("Scans: " + getStorage().countAllScans() + "<br/>");
				sb.append("AccessPoints: "
						+ getStorage().countAllAccessPoints() + "<br/>");
				sb.append("<br/>Buildings: " + getStorage().countAllBuildings()
						+ "<br/>");
				for (Building b : getStorage().getAllBuildings()) {
					sb.append("Building " + b.getId() + " " + b.getName()
							+ "<br/>");
				}
				sb.append("<br/>Floors: " + getStorage().countAllFloors()
						+ "<br/>");
				for (Floor m : getStorage().getAllFloors()) {
					byte[] f = m.getFile();
					sb.append("Floor " + m.getId() + " " + m.getName() + " "
							+ (f != null && f.length != 0) + "<br/>");
				}
				sb.append("<br/>MeasurePoints: "
						+ getStorage().countAllMeasurePoints() + "<br/>");
				for (MeasurePoint cp : getStorage().getAllMeasurePoints()) {
					sb.append("MeasurePoint " + cp.getId() + " "
							+ cp.getFloorId() + " " + cp.getPosx() + " "
							+ cp.getPosy() + "<br/>");
				}
				sb.append("<br/>Scans: " + getStorage().countAllScans()
						+ "<br/>");
				for (Scan scan : getStorage().getAllScans()) {
					sb.append("Scan " + scan.getId() + " " + scan.getMpid()
							+ " " + scan.getTime() + " " + scan.getCompass()
							+ "<br/>");
				}
				sb.append("<br/>AccessPoints: "
						+ getStorage().countAllAccessPoints() + "<br/>");
				List<AccessPoint> all = getStorage().getAllAccessPoints();
				for (AccessPoint ap : all) {
					sb.append("AP " + ap.getId() + " " + ap.getScanId() + " "
							+ ap.getBssid() + " " + ap.getLevel() + " "
							+ ap.getFreq() + " '" + ap.getSsid() + "' "
							+ ap.getProps() + "<br/>");
				}
				sb.append("</nobr>");
				final String str = sb.toString();
				runOnUiThread(new Runnable() {
					public void run() {
						dataView.loadData(str, "text/html", null);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_export:
			DatabaseTransformer.exportDatabase(this);
			break;
		case R.id.menu_import:
			DatabaseTransformer.importDatabase(this, getStorage());
			break;
		case R.id.menu_clear:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.database_clear_question)
					.setPositiveButton(R.string.yes, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getStorage().clearDatabase();
							Toast.makeText(getBaseContext(),
									R.string.database_clear_success,
									Toast.LENGTH_SHORT).show();
							showDebug();
						}
					}).setNegativeButton(R.string.no, null).show();
			break;
		}
		return result;
	}

}
