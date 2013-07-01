package de.rwth.ti.wps;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.common.ChooseFileDialog;
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
		textStatus.append("\nBuildings: " + getStorage().countAllBuildings()
				+ "\n");
		for (Building b : getStorage().getAllBuildings()) {
			textStatus.append("Building\t" + b.getId() + "\t" + b.getName()
					+ "\n");
		}
		textStatus.append("\nFloors: " + getStorage().countAllFloors() + "\n");
		for (Floor m : getStorage().getAllFloors()) {
			byte[] f = m.getFile();
			textStatus.append("Floor\t" + m.getId() + "\t" + m.getName() + "\t"
					+ (f != null && f.length != 0) + "\n");
		}
		textStatus.append("\nMeasurePoints: "
				+ getStorage().countAllMeasurePoints() + "\n");
		for (MeasurePoint cp : getStorage().getAllMeasurePoints()) {
			textStatus.append("MeasurePoint\t" + cp.getId() + "\t"
					+ cp.getFloorId() + "\t" + cp.getPosx() + "\t"
					+ cp.getPosy() + "\n");
		}
		textStatus.append("\nScans: " + getStorage().countAllScans() + "\n");
		for (Scan scan : getStorage().getAllScans()) {
			textStatus.append("Scan\t" + scan.getId() + "\t" + scan.getMpid()
					+ "\t" + scan.getTime() + "\t" + scan.getCompass() + "\n");
		}
		textStatus.append("\nAccessPoints: "
				+ getStorage().countAllAccessPoints() + "\n");
		List<AccessPoint> all = getStorage().getAllAccessPoints();
		for (AccessPoint ap : all) {
			textStatus.append("AP\t" + ap.getId() + "\t" + ap.getScanId()
					+ "\t" + ap.getBssid() + "\t" + ap.getLevel() + "\t"
					+ ap.getFreq() + "\t'" + ap.getSsid() + "'\t"
					+ ap.getProps() + "\n");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_export:
			final EditText input = new EditText(DebugActivity.this);
			input.setText(Constants.LOCAL_DB_NAME);
			new AlertDialog.Builder(DebugActivity.this)
					.setTitle(R.string.database_export_question)
					.setMessage(R.string.choose_filename)
					.setView(input)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String dbExportName = input.getText()
											.toString();
									if (dbExportName
											.endsWith(Constants.DB_SUFFIX) == false) {
										dbExportName += Constants.DB_SUFFIX;
									}
									try {
										getStorage().exportDatabase(
												dbExportName);
									} catch (IOException e) {
										Toast.makeText(getBaseContext(),
												e.toString(), Toast.LENGTH_LONG)
												.show();
									}
									Toast.makeText(
											getBaseContext(),
											getText(R.string.database_export_success)
													+ "\n" + dbExportName,
											Toast.LENGTH_SHORT).show();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Do nothing.
								}
							}).show();
			break;
		case R.id.menu_import:
			ChooseFileDialog directoryChooserDialog = new ChooseFileDialog(
					DebugActivity.this,
					new ChooseFileDialog.ChosenFileListener() {
						@Override
						public void onChosenFile(String chosenFile) {
							if (getStorage().importDatabase(chosenFile) == true) {
								Toast.makeText(getBaseContext(),
										R.string.database_import_success,
										Toast.LENGTH_SHORT).show();
							}
							showDebug();
						}
					}, Constants.DB_SUFFIX);
			directoryChooserDialog.chooseDirectory(Constants.SD_APP_DIR);
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
