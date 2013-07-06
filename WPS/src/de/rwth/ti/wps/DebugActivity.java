package de.rwth.ti.wps;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
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
	private AsyncTask<String, Integer, Boolean> task;
	private ProgressDialog waitDialog;

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
		textStatus.setText(R.string.please_wait);
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder("Database:\n");
				sb.append("\nBuildings: " + getStorage().countAllBuildings()
						+ "\n");
				for (Building b : getStorage().getAllBuildings()) {
					sb.append("Building\t" + b.getId() + "\t" + b.getName()
							+ "\n");
				}
				sb.append("\nFloors: " + getStorage().countAllFloors() + "\n");
				for (Floor m : getStorage().getAllFloors()) {
					byte[] f = m.getFile();
					sb.append("Floor\t" + m.getId() + "\t" + m.getName() + "\t"
							+ (f != null && f.length != 0) + "\n");
				}
				sb.append("\nMeasurePoints: "
						+ getStorage().countAllMeasurePoints() + "\n");
				for (MeasurePoint cp : getStorage().getAllMeasurePoints()) {
					sb.append("MeasurePoint\t" + cp.getId() + "\t"
							+ cp.getFloorId() + "\t" + cp.getPosx() + "\t"
							+ cp.getPosy() + "\n");
				}
				sb.append("\nScans: " + getStorage().countAllScans() + "\n");
				for (Scan scan : getStorage().getAllScans()) {
					sb.append("Scan\t" + scan.getId() + "\t" + scan.getMpid()
							+ "\t" + scan.getTime() + "\t" + scan.getCompass()
							+ "\n");
				}
				sb.append("\nAccessPoints: "
						+ getStorage().countAllAccessPoints() + "\n");
				List<AccessPoint> all = getStorage().getAllAccessPoints();
				for (AccessPoint ap : all) {
					sb.append("AP\t" + ap.getId() + "\t" + ap.getScanId()
							+ "\t" + ap.getBssid() + "\t" + ap.getLevel()
							+ "\t" + ap.getFreq() + "\t'" + ap.getSsid()
							+ "'\t" + ap.getProps() + "\n");
				}
				final String str = sb.toString();
				runOnUiThread(new Runnable() {
					public void run() {
						textStatus.setText(str);
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
			final EditText input = new EditText(DebugActivity.this);
			input.setText(Constants.LOCAL_DB_NAME);
			input.requestFocus();
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
									waitDialog = ProgressDialog.show(
											DebugActivity.this, "",
											getString(R.string.please_wait));
									waitDialog.setCancelable(false);
									task = new AsyncTask<String, Integer, Boolean>() {
										private String msg;

										@Override
										protected void onPreExecute() {
											lockScreenOrientation();
										}

										@Override
										protected Boolean doInBackground(
												String... params) {
											msg = params[0];
											try {
												getStorage().exportDatabase(
														params[0]);
											} catch (IOException e) {
												msg = e.toString();
												return false;
											}
											return true;
										}

										@Override
										protected void onPostExecute(
												Boolean result) {
											if (waitDialog != null) {
												waitDialog.dismiss();
												waitDialog = null;
											}
											if (result == true) {
												Toast.makeText(
														getBaseContext(),
														getText(R.string.database_export_success)
																+ "\n" + msg,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(
														getBaseContext(), msg,
														Toast.LENGTH_LONG)
														.show();
											}
											unlockScreenOrientation();
										}
									};
									task.execute(dbExportName);
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
							waitDialog = ProgressDialog.show(
									DebugActivity.this, "",
									getString(R.string.please_wait));
							waitDialog.setCancelable(false);
							task = new AsyncTask<String, Integer, Boolean>() {
								@Override
								protected void onPreExecute() {
									lockScreenOrientation();
								}

								@Override
								protected Boolean doInBackground(
										String... params) {
									boolean result = getStorage()
											.importDatabase(params[0]);
									return result;
								}

								@Override
								protected void onProgressUpdate(
										Integer... progress) {
									waitDialog.setProgress(progress[0]);
								}

								@Override
								protected void onPostExecute(Boolean result) {
									if (result == true) {
										Toast.makeText(
												getBaseContext(),
												R.string.database_import_success,
												Toast.LENGTH_SHORT).show();
									}
									if (waitDialog != null) {
										waitDialog.dismiss();
										waitDialog = null;
									}
									showDebug();
									unlockScreenOrientation();
								}
							};
							task.execute(chosenFile);
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

	private void lockScreenOrientation() {
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	private void unlockScreenOrientation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

}
