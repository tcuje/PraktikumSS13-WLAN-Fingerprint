package de.rwth.ti.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;
import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.db.StorageHandler;
import de.rwth.ti.wps.DebugActivity;
import de.rwth.ti.wps.R;

public class DatabaseTransformer {

	public static void exportDatabase(final Activity parentActivity) {
		final EditText input = new EditText(parentActivity);
		input.setText(Constants.EXPORT_DEFAULT_DB_NAME);
		input.setSingleLine();
		input.requestFocus();
		new AlertDialog.Builder(parentActivity)
				.setTitle(R.string.database_export_question)
				.setMessage(R.string.choose_filename)
				.setView(input)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							private ProgressDialog waitDialog;

							public void onClick(DialogInterface dialog,
									int whichButton) {
								String dbExportName = input.getText()
										.toString();
								if (dbExportName.endsWith(Constants.DB_SUFFIX) == false) {
									dbExportName += Constants.DB_SUFFIX;
								}
								waitDialog = ProgressDialog.show(
										parentActivity,
										"",
										parentActivity
												.getString(R.string.please_wait));
								waitDialog.setCancelable(false);
								AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
									private String msg;

									@Override
									protected void onPreExecute() {
										lockScreenOrientation(parentActivity);
									}

									@Override
									protected Boolean doInBackground(
											String... params) {
										msg = params[0];
										try {
											File sd = new File(
													Constants.SD_APP_DIR);
											File data = Environment
													.getDataDirectory();
											String srcDBPath = "//data//"
													+ Constants.PACKAGE_NAME
													+ "//databases//"
													+ Constants.DB_NAME;
											String dstDBPath = "/" + params[0];
											File srcDB = new File(data,
													srcDBPath);
											File dstDB = new File(sd, dstDBPath);
											FileInputStream fis = new FileInputStream(
													srcDB);
											FileChannel src = fis.getChannel();
											FileOutputStream fos = new FileOutputStream(
													dstDB);
											FileChannel dst = fos.getChannel();
											dst.transferFrom(src, 0, src.size());
											try {
												fis.close();
											} catch (Exception ex) {
											}
											try {
												fos.close();
											} catch (Exception ex) {
											}
										} catch (IOException e) {
											msg = e.toString();
											return false;
										}
										return true;
									}

									@Override
									protected void onPostExecute(Boolean result) {
										if (waitDialog != null) {
											waitDialog.dismiss();
											waitDialog = null;
										}
										if (result == true) {
											Toast.makeText(
													parentActivity,
													parentActivity
															.getText(R.string.database_export_success)
															+ "\n" + msg,
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(parentActivity, msg,
													Toast.LENGTH_LONG).show();
										}
										unlockScreenOrientation(parentActivity);
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
	}

	public static void importDatabase(final DebugActivity parentActivity,
			final StorageHandler localHandler) {
		ChooseFileDialog directoryChooserDialog = new ChooseFileDialog(
				parentActivity, new ChooseFileDialog.ChosenFileListener() {
					private ProgressDialog waitDialog;

					@Override
					public void onChosenFile(String chosenFile) {
						waitDialog = ProgressDialog.show(parentActivity, "",
								parentActivity.getString(R.string.please_wait));
						waitDialog.setCancelable(false);
						AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
							private long start;

							@Override
							protected void onPreExecute() {
								lockScreenOrientation(parentActivity);
							}

							@Override
							protected Boolean doInBackground(String... params) {
								File f = new File(params[0]);
								if (f.exists() == false || f.isFile() == false) {
									String msg = parentActivity
											.getString(R.string.file_not_exist)
											+ "\n" + f.getAbsolutePath();
									Toast.makeText(parentActivity, msg,
											Toast.LENGTH_LONG).show();
									return false;
								}
								start = System.currentTimeMillis();
								// copy the database from sd card to internal
								// storage
								// File sd =
								// Environment.getExternalStorageDirectory();
								// File data = Environment.getDataDirectory();
								// String dstDBPath = "//data//" +
								// MainActivity.PACKAGE_NAME
								// + "//databases//" + IMPORT_DB_NAME;
								// String srcDBPath = "/" + filename;
								// File dstDB = new File(data, dstDBPath);
								// File srcDB = new File(sd, srcDBPath);
								// FileChannel src = new
								// FileInputStream(srcDB).getChannel();
								// FileChannel dst = new
								// FileOutputStream(dstDB).getChannel();
								// dst.transferFrom(src, 0, src.size());
								// src.close();
								// dst.close();
								// open import database
								StorageHandler temp = new StorageHandler(
										parentActivity, params[0]);
								if (temp.onStart() == false) {
									return false;
								}
								// import buildings
								List<Building> impBuildings = temp
										.getAllBuildings();
								List<Building> locBuildings = localHandler
										.getAllBuildings();
								for (Building bImp : impBuildings) {
									Building bParent = null;
									for (Building loc : locBuildings) {
										if (loc.compare(bImp)) {
											// building already exist local
											bParent = loc;
											// update local object
											long oldID = bImp.getId();
											bImp.setId(loc.getId());
											localHandler.changeBuilding(bImp);
											bImp.setId(oldID);
											break;
										}
									}
									if (bParent == null) {
										// new building
										bParent = localHandler
												.createBuilding(bImp.getName());
										if (bParent == null) {
											// skip all child objects for
											// invalid building
											continue;
										}
									}
									// import floors
									List<Floor> impFloors = temp
											.getFloors(bImp);
									List<Floor> locFloors = localHandler
											.getFloors(bParent);
									for (Floor fImp : impFloors) {
										Floor fParent = null;
										for (Floor loc : locFloors) {
											if (loc.compare(fImp)) {
												// floor already exist local
												fParent = loc;
												// update local object
												long oldID = fImp.getId();
												fImp.setId(loc.getId());
												localHandler.changeFloor(fImp);
												fImp.setId(oldID);
												break;
											}
										}
										if (fParent == null) {
											fParent = localHandler.createFloor(
													bParent, fImp.getName(),
													fImp.getFile(),
													fImp.getLevel(),
													fImp.getNorth());
											if (fParent == null) {
												// skip all child objects for
												// invalid floor
												continue;
											}
										}
										// import measure points
										List<MeasurePoint> impMeasurePoints = temp
												.getMeasurePoints(fImp);
										List<MeasurePoint> locMeasurePoints = localHandler
												.getMeasurePoints(fParent);
										for (MeasurePoint mpImp : impMeasurePoints) {
											MeasurePoint mpParent = null;
											for (MeasurePoint loc : locMeasurePoints) {
												if (loc.compare(mpImp)) {
													// measure point already
													// exist local
													mpParent = loc;
													// update local object
													long oldID = mpImp.getId();
													mpImp.setId(loc.getId());
													localHandler
															.changeMeasurePoint(mpImp);
													mpImp.setId(oldID);
													break;
												}
											}
											if (mpParent == null) {
												mpParent = localHandler.createMeasurePoint(
														fParent,
														mpImp.getPosx(),
														mpImp.getPosy());
												if (mpParent == null) {
													// skip all child objects
													// for invalid floor
													continue;
												}
											}
											// import scans
											List<Scan> impScans = temp
													.getScans(mpImp);
											List<Scan> locScans = localHandler
													.getScans(mpParent);
											for (Scan scImp : impScans) {
												Scan scParent = null;
												for (Scan loc : locScans) {
													if (loc.compare(scImp)) {
														// scan already exist
														// local
														scParent = loc;
														// update local object
														long oldID = scImp
																.getId();
														scImp.setId(loc.getId());
														localHandler
																.changeScan(scImp);
														scImp.setId(oldID);
														break;
													}
												}
												if (scParent == null) {
													scParent = localHandler
															.createScan(
																	mpParent,
																	scImp.getTime(),
																	scImp.getCompass());
													if (scParent == null) {
														// skip all child
														// objects for invalid
														// scan
														continue;
													}
												}
												// import access points
												List<AccessPoint> impAPs = temp
														.getAccessPoints(scImp);
												List<AccessPoint> locAPs = localHandler
														.getAccessPoints(scParent);
												for (AccessPoint apImp : impAPs) {
													AccessPoint apParent = null;
													for (AccessPoint loc : locAPs) {
														if (loc.compare(apImp)) {
															// access point
															// already exist
															// local
															apParent = loc;
															// update local
															// object
															long oldID = apImp
																	.getId();
															apImp.setId(loc
																	.getId());
															localHandler
																	.changeAccessPoint(apImp);
															apImp.setId(oldID);
															break;
														}
													}
													if (apParent == null) {
														apParent = localHandler
																.createAccessPoint(
																		scParent,
																		apImp.getBssid(),
																		apImp.getLevel(),
																		apImp.getFreq(),
																		apImp.getSsid(),
																		apImp.getProps());
														if (apParent == null) {
															continue;
														}
													}
												}
											}
										}
									}
								}
								temp.onStop();
								return true;
							}

							@Override
							protected void onPostExecute(Boolean result) {
								long stop = System.currentTimeMillis();
								if (result == true) {
									Toast.makeText(
											parentActivity,
											parentActivity
													.getString(R.string.database_import_success)
													+ " "
													+ (stop - start)
													+ "ms", Toast.LENGTH_LONG)
											.show();
								}
								if (waitDialog != null) {
									waitDialog.dismiss();
									waitDialog = null;
								}
								parentActivity.showDebug();
								unlockScreenOrientation(parentActivity);
							}
						};
						task.execute(chosenFile);
					}
				}, Constants.DB_SUFFIX);
		directoryChooserDialog.chooseDirectory(Constants.SD_APP_DIR);
	}

	private static void lockScreenOrientation(Activity parentActivity) {
		int currentOrientation = parentActivity.getResources()
				.getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			parentActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			parentActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	private static void unlockScreenOrientation(Activity parentActivity) {
		parentActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

}
