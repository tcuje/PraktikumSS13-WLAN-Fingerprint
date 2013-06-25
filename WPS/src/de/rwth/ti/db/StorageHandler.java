package de.rwth.ti.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import de.rwth.ti.common.Constants;
import de.rwth.ti.share.IGUIDataHandler;
import de.rwth.ti.share.IMeasureDataHandler;

/**
 * This class handles the database or persistent storage access
 * 
 */
public class StorageHandler implements IGUIDataHandler, IMeasureDataHandler {

	private static final String DB_NAME = "local";

	private Context context;
	private SQLiteDatabase db;
	private Storage storage;
	private final String dbName;

	public StorageHandler(Context context, String dbName) {
		this.context = context;
		this.storage = new Storage(context, dbName);
		this.dbName = dbName;
	}

	/**
	 * Uses the default local database
	 * 
	 * @param context
	 */
	public StorageHandler(Context context) {
		this(context, DB_NAME);
	}

	public void onStart() throws SQLException {
		db = storage.getWritableDatabase();
	}

	public void onStop() {
		storage.close();
	}

	@Override
	public AccessPoint createAccessPoint(Scan scan, String bssid, long level,
			long freq, String ssid, String props) {
		ContentValues values = new ContentValues();
		if (scan == null) {
			return null;
		}
		values.put(AccessPoint.COLUMN_SCANID, scan.getId());
		values.put(AccessPoint.COLUMN_BSSID, bssid);
		values.put(AccessPoint.COLUMN_LEVEL, level);
		values.put(AccessPoint.COLUMN_FREQ, freq);
		values.put(AccessPoint.COLUMN_SSID, ssid);
		values.put(AccessPoint.COLUMN_PROPS, props);
		long insertId = db.insert(AccessPoint.TABLE_NAME, null, values);
		Cursor cursor = db.query(AccessPoint.TABLE_NAME,
				AccessPoint.ALL_COLUMNS, AccessPoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(insertId) }, null, null, null);
		AccessPoint result = null;
		if (cursor.moveToFirst()) {
			result = cursorToAccessPoint(cursor);
		}
		cursor.close();
		return result;
	}

	private AccessPoint cursorToAccessPoint(Cursor cursor) {
		AccessPoint result = new AccessPoint();
		result.setId(cursor.getLong(0));
		result.setScanId(cursor.getLong(1));
		result.setBssid(cursor.getString(2));
		result.setLevel(cursor.getInt(3));
		result.setFreq(cursor.getInt(4));
		result.setSsid(cursor.getString(5));
		result.setProps(cursor.getString(6));
		return result;
	}

	private List<AccessPoint> cursorToAccessPoints(Cursor cursor) {
		List<AccessPoint> result = new ArrayList<AccessPoint>(cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				AccessPoint ap = cursorToAccessPoint(cursor);
				result.add(ap);
			} while (cursor.moveToNext() == true);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<AccessPoint> getAllAccessPoints() {
		Cursor cursor = db.query(AccessPoint.TABLE_NAME,
				AccessPoint.ALL_COLUMNS, null, null, null, null, null);
		List<AccessPoint> result = cursorToAccessPoints(cursor);
		return result;
	}

	@Override
	public List<AccessPoint> getAccessPoints(Scan scan) {
		Cursor cursor = db
				.query(AccessPoint.TABLE_NAME, AccessPoint.ALL_COLUMNS,
						AccessPoint.COLUMN_SCANID + "=?",
						new String[] { String.valueOf(scan.getId()) }, null,
						null, null);
		List<AccessPoint> result = cursorToAccessPoints(cursor);
		return result;
	}

	@Override
	public List<AccessPoint> getAccessPoint(String bssid) {
		Cursor cursor = db.query(AccessPoint.TABLE_NAME,
				AccessPoint.ALL_COLUMNS, AccessPoint.COLUMN_BSSID + "=?",
				new String[] { bssid }, null, null, AccessPoint.COLUMN_LEVEL
						+ " DESC");
		List<AccessPoint> result = cursorToAccessPoints(cursor);
		return result;
	}

	@Override
	public Scan createScan(MeasurePoint mp, long time, double north) {
		ContentValues values = new ContentValues();
		if (mp == null) {
			return null;
		}
		values.put(Scan.COLUMN_MPID, mp.getId());
		values.put(Scan.COLUMN_TIME, time);
		values.put(Scan.COLUMN_COMPASS, north);
		long insertId = db.insert(Scan.TABLE_NAME, null, values);
		Cursor cursor = db.query(Scan.TABLE_NAME, Scan.ALL_COLUMNS,
				Scan.COLUMN_ID + "=?",
				new String[] { String.valueOf(insertId) }, null, null, null);
		Scan result = null;
		if (cursor.moveToFirst()) {
			result = cursorToScan(cursor);
		}
		cursor.close();
		return result;
	}

	private Scan cursorToScan(Cursor cursor) {
		Scan result = new Scan();
		result.setId(cursor.getLong(0));
		result.setMpid(cursor.getLong(1));
		result.setTime(cursor.getLong(2));
		result.setCompass(cursor.getLong(3));
		return result;
	}

	private List<Scan> cursorToScans(Cursor cursor) {
		List<Scan> result = new ArrayList<Scan>(cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				Scan scan = cursorToScan(cursor);
				result.add(scan);
			} while (cursor.moveToNext() == true);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<Scan> getAllScans() {
		Cursor cursor = db.query(Scan.TABLE_NAME, Scan.ALL_COLUMNS, null, null,
				null, null, null);
		List<Scan> result = cursorToScans(cursor);
		return result;
	}

	@Override
	public List<Scan> getScans(MeasurePoint mp) {
		Cursor cursor = db.query(Scan.TABLE_NAME, Scan.ALL_COLUMNS,
				Scan.COLUMN_MPID + "=?",
				new String[] { String.valueOf(mp.getId()) }, null, null, null);
		List<Scan> result = cursorToScans(cursor);
		return result;
	}

	@Override
	public Scan getScan(AccessPoint ap) {
		Cursor cursor = db.query(Scan.TABLE_NAME, Scan.ALL_COLUMNS,
				Scan.COLUMN_ID + "=?",
				new String[] { String.valueOf(ap.getScanId()) }, null, null,
				null);
		Scan result = null;
		if (cursor.moveToFirst() == true) {
			result = cursorToScan(cursor);
		}
		return result;
	}

	@Override
	public MeasurePoint createMeasurePoint(Floor f, double x, double y) {
		ContentValues values = new ContentValues();
		if (f == null) {
			return null;
		}
		values.put(MeasurePoint.COLUMN_FLOORID, f.getId());
		values.put(MeasurePoint.COLUMN_POS_X, x);
		values.put(MeasurePoint.COLUMN_POS_Y, y);
		long insertId = db.insert(MeasurePoint.TABLE_NAME, null, values);
		Cursor cursor = db.query(MeasurePoint.TABLE_NAME,
				MeasurePoint.ALL_COLUMNS, MeasurePoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(insertId) }, null, null, null);
		MeasurePoint result = null;
		if (cursor.moveToFirst()) {
			result = cursorToMeasurePoint(cursor);
		}
		cursor.close();
		return result;
	}

	private MeasurePoint cursorToMeasurePoint(Cursor cursor) {
		MeasurePoint result = new MeasurePoint();
		result.setId(cursor.getLong(0));
		result.setFloorId(cursor.getLong(1));
		result.setPosx(cursor.getDouble(2));
		result.setPosy(cursor.getDouble(3));
		return result;
	}

	private List<MeasurePoint> cursorToMeasurePoints(Cursor cursor) {
		List<MeasurePoint> result = new ArrayList<MeasurePoint>(
				cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				MeasurePoint cp = cursorToMeasurePoint(cursor);
				result.add(cp);
			} while (cursor.moveToNext() == true);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<MeasurePoint> getAllMeasurePoints() {
		Cursor cursor = db.query(MeasurePoint.TABLE_NAME,
				MeasurePoint.ALL_COLUMNS, null, null, null, null, null);
		List<MeasurePoint> result = cursorToMeasurePoints(cursor);
		return result;
	}

	@Override
	public MeasurePoint getMeasurePoint(Scan scan) {
		Cursor cursor = db.query(MeasurePoint.TABLE_NAME,
				MeasurePoint.ALL_COLUMNS, MeasurePoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(scan.getMpid()) }, null, null,
				null);
		MeasurePoint result = null;
		if (cursor.moveToFirst() == true) {
			result = cursorToMeasurePoint(cursor);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<MeasurePoint> getMeasurePoints(Floor floor) {
		Cursor cursor = db.query(MeasurePoint.TABLE_NAME,
				MeasurePoint.ALL_COLUMNS, MeasurePoint.COLUMN_FLOORID + "=?",
				new String[] { String.valueOf(floor.getId()) }, null, null,
				null);
		cursor.moveToFirst();
		List<MeasurePoint> result = cursorToMeasurePoints(cursor);
		return result;
	}

	@Override
	public Floor createFloor(Building b, String name, byte[] file, long level,
			double north) {
		ContentValues values = new ContentValues();
		if (b == null) {
			return null;
		}
		values.put(Floor.COLUMN_BID, b.getId());
		values.put(Floor.COLUMN_NAME, name);
		values.put(Floor.COLUMN_FILE, file);
		values.put(Floor.COLUMN_LEVEL, level);
		values.put(Floor.COLUMN_NORTH, north);
		long insertId = db.insert(Floor.TABLE_NAME, null, values);
		Cursor cursor = db.query(Floor.TABLE_NAME, Floor.ALL_COLUMNS,
				Floor.COLUMN_ID + "=?",
				new String[] { String.valueOf(insertId) }, null, null, null);
		Floor result = null;
		if (cursor.moveToFirst()) {
			result = cursorToFloor(cursor);
		}
		cursor.close();
		return result;
	}

	private Floor cursorToFloor(Cursor cursor) {
		Floor result = new Floor();
		result.setId(cursor.getLong(0));
		result.setBId(cursor.getLong(1));
		result.setName(cursor.getString(2));
		result.setFile(cursor.getBlob(3));
		result.setLevel(cursor.getLong(4));
		result.setNorth(cursor.getLong(5));
		return result;
	}

	private List<Floor> cursorToFloors(Cursor cursor) {
		List<Floor> result = new ArrayList<Floor>(cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				Floor floor = cursorToFloor(cursor);
				result.add(floor);
			} while (cursor.moveToNext() == true);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<Floor> getAllFloors() {
		Cursor cursor = db.query(Floor.TABLE_NAME, Floor.ALL_COLUMNS, null,
				null, null, null, null);
		List<Floor> result = cursorToFloors(cursor);
		return result;
	}

	@Override
	public List<Floor> getFloors(Building b) {
		Cursor cursor = db.query(Floor.TABLE_NAME, Floor.ALL_COLUMNS,
				Floor.COLUMN_BID + "=?",
				new String[] { String.valueOf(b.getId()) }, null, null, null,
				null);
		List<Floor> result = cursorToFloors(cursor);
		return result;
	}

	@Override
	public Floor getFloor(MeasurePoint mp) {
		Cursor cursor = db.query(Floor.TABLE_NAME, Floor.ALL_COLUMNS,
				Floor.COLUMN_ID + "=?",
				new String[] { String.valueOf(mp.getFloorId()) }, null, null,
				null, null);
		Floor result = null;
		if (cursor.moveToFirst() == true) {
			result = cursorToFloor(cursor);
		}
		cursor.close();
		return result;
	}

	@Override
	public boolean changeFloor(Floor floor) {
		ContentValues values = new ContentValues();
		values.put(Floor.COLUMN_NAME, floor.getName());
		int result = db.update(Floor.TABLE_NAME, values,
				Floor.COLUMN_ID + "=?",
				new String[] { String.valueOf(floor.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteFloor(Floor floor) {
		int result = db.delete(Floor.TABLE_NAME, Floor.COLUMN_ID + "=?",
				new String[] { String.valueOf(floor.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeBuilding(Building building) {
		ContentValues values = new ContentValues();
		values.put(Floor.COLUMN_NAME, building.getName());
		int result = db.update(Building.TABLE_NAME, values, Building.COLUMN_ID
				+ "=?", new String[] { String.valueOf(building.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}
	
	private static final String TAG = "deletedBuilding";

	@Override
	public boolean deleteBuilding(Building building) {
		int result = db.delete(Building.TABLE_NAME, Building.COLUMN_ID+"=?",
				new String[] { String.valueOf(building.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeAccessPoint(AccessPoint ap) {
		ContentValues values = new ContentValues();
		values.put(AccessPoint.COLUMN_SCANID, ap.getId());
		values.put(AccessPoint.COLUMN_BSSID, ap.getBssid());
		values.put(AccessPoint.COLUMN_LEVEL, ap.getLevel());
		values.put(AccessPoint.COLUMN_FREQ, ap.getFreq());
		values.put(AccessPoint.COLUMN_SSID, ap.getSsid());
		values.put(AccessPoint.COLUMN_PROPS, ap.getProps());
		int result = db.update(AccessPoint.TABLE_NAME, values,
				AccessPoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(ap.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteAccessPoint(AccessPoint ap) {
		int result = db.delete(AccessPoint.TABLE_NAME, AccessPoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(ap.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeMeasurePoint(MeasurePoint mp) {
		ContentValues values = new ContentValues();
		values.put(MeasurePoint.COLUMN_FLOORID, mp.getId());
		int result = db.update(MeasurePoint.TABLE_NAME, values,
				MeasurePoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(mp.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteMea1surePoint(MeasurePoint mp) {
		int result = db.delete(MeasurePoint.TABLE_NAME, MeasurePoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(mp.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeScan(Scan sc) {
		ContentValues values = new ContentValues();
		values.put(Scan.COLUMN_MPID, sc.getId());
		values.put(Scan.COLUMN_TIME, sc.getTime());
		values.put(Scan.COLUMN_COMPASS, sc.getCompass());
		int result = db.update(Scan.TABLE_NAME, values, Scan.COLUMN_ID + "=?",
				new String[] { String.valueOf(sc.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteScan(Scan sc) {
		int result = db.delete(Scan.TABLE_NAME, Scan.COLUMN_ID + "=?",
				new String[] { String.valueOf(sc.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public Building createBuilding(String name) {
		ContentValues values = new ContentValues();
		values.put(Building.COLUMN_NAME, name);
		long insertId = db.insert(Building.TABLE_NAME, null, values);
		Cursor cursor = db.query(Building.TABLE_NAME, Building.ALL_COLUMNS,
				Building.COLUMN_ID + "=?",
				new String[] { String.valueOf(insertId) }, null, null, null);
		Building result = null;
		if (cursor.moveToFirst()) {
			result = cursorToBuilding(cursor);
		}
		cursor.close();
		return result;
	}

	private Building cursorToBuilding(Cursor cursor) {
		Building result = new Building();
		result.setId(cursor.getLong(0));
		result.setName(cursor.getString(1));
		return result;
	}

	private List<Building> cursorToBuildings(Cursor cursor) {
		List<Building> result = new ArrayList<Building>(cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				Building scan = cursorToBuilding(cursor);
				result.add(scan);
			} while (cursor.moveToNext() == true);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<Building> getAllBuildings() {
		Cursor cursor = db.query(Building.TABLE_NAME, Building.ALL_COLUMNS,
				null, null, null, null, Building.COLUMN_NAME + " ASC");
		List<Building> result = cursorToBuildings(cursor);
		return result;
	}

	@Override
	public Building getBuilding(Floor floor) {
		Cursor cursor = db.query(Building.TABLE_NAME, Building.ALL_COLUMNS,
				Building.COLUMN_ID + "=?",
				new String[] { String.valueOf(floor.getBId()) }, null, null,
				null);
		Building result = null;
		if (cursor.moveToFirst() == true) {
			result = cursorToBuilding(cursor);
		}
		cursor.close();
		return result;
	}

	private long countTable(String tableName) {
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		cursor.close();
		return result;
	}

	@Override
	public long countScans() {
		long result = countTable(Scan.TABLE_NAME);
		return result;
	}

	@Override
	public long countAccessPoints() {
		long result = countTable(AccessPoint.TABLE_NAME);
		return result;
	}

	@Override
	public long countMeasurePoints() {
		long result = countTable(MeasurePoint.TABLE_NAME);
		return result;
	}

	@Override
	public long countFloors() {
		long result = countTable(Floor.TABLE_NAME);
		return result;
	}

	@Override
	public long countBuildings() {
		long result = countTable(Building.TABLE_NAME);
		return result;
	}

	@Override
	public List<Scan> getScans(Floor floor, int compass) {
		List<Scan> result = new LinkedList<Scan>();
		List<MeasurePoint> mps = getMeasurePoints(floor);
		for (MeasurePoint mp : mps) {
			List<Scan> scans = getScans(mp);
			for (Scan scan : scans) {
				if (scan.getCompass() > compass - 45
						|| scan.getCompass() < compass + 45) {
					result.add(scan);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param filename
	 *            start directory is the root directory on the sd drive
	 * @throws IOException
	 */
	public void exportDatabase(String filename) throws IOException {
		db.close();
		File sd = new File(Constants.SD_APP_DIR);
		File data = Environment.getDataDirectory();
		String srcDBPath = "//data//" + Constants.PACKAGE_NAME
				+ "//databases//" + dbName;
		// String dstDBPath = "/backup/" + filename;
		String dstDBPath = "/" + filename;
		File srcDB = new File(data, srcDBPath);
		File dstDB = new File(sd, dstDBPath);
		FileInputStream fis = new FileInputStream(srcDB);
		FileChannel src = fis.getChannel();
		FileOutputStream fos = new FileOutputStream(dstDB);
		FileChannel dst = fos.getChannel();
		dst.transferFrom(src, 0, src.size());
		fis.close();
		fos.close();
		db = storage.getWritableDatabase();
	}

	/**
	 * Updates your local map information with the given database, including
	 * import
	 * 
	 * @param filename
	 *            full filepath for the import database
	 */
	public void importDatabase(String filename) {
		// copy the database from sd card to internal storage
//		File sd = Environment.getExternalStorageDirectory();
//		File data = Environment.getDataDirectory();
//		String dstDBPath = "//data//" + MainActivity.PACKAGE_NAME
//				+ "//databases//" + IMPORT_DB_NAME;
//		String srcDBPath = "/" + filename;
//		File dstDB = new File(data, dstDBPath);
//		File srcDB = new File(sd, srcDBPath);
//		FileChannel src = new FileInputStream(srcDB).getChannel();
//		FileChannel dst = new FileOutputStream(dstDB).getChannel();
//		dst.transferFrom(src, 0, src.size());
//		src.close();
//		dst.close();
		// open import database
		StorageHandler temp = new StorageHandler(context, filename);
		try {
			temp.onStart();
		} catch (SQLException ex) {
			Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
		// import buildings
		List<Building> impBuildings = temp.getAllBuildings();
		List<Building> locBuildings = this.getAllBuildings();
		for (Building bImp : impBuildings) {
			Building bParent = null;
			for (Building loc : locBuildings) {
				if (loc.compare(bImp)) {
					// building already exist local
					bParent = loc;
					// update local object
					bImp.setId(loc.getId());
					this.changeBuilding(bImp);
					break;
				}
			}
			if (bParent == null) {
				// new building
				bParent = this.createBuilding(bImp.getName());
				if (bParent == null) {
					// skip all child objects for invalid building
					continue;
				}
			}
			// import floors
			List<Floor> impFloors = temp.getFloors(bImp);
			List<Floor> locFloors = this.getFloors(bParent);
			for (Floor fImp : impFloors) {
				Floor fParent = null;
				for (Floor loc : locFloors) {
					if (loc.compare(fImp)) {
						// floor already exist local
						fParent = loc;
						// update local object
						fImp.setId(loc.getId());
						this.changeFloor(fImp);
						break;
					}
				}
				if (fParent == null) {
					fParent = this.createFloor(bParent, fImp.getName(),
							fImp.getFile(), fImp.getLevel(), fImp.getNorth());
					if (fParent == null) {
						// skip all child objects for invalid floor
						continue;
					}
				}
				// import measure points
				List<MeasurePoint> impMeasurePoints = temp
						.getMeasurePoints(fImp);
				List<MeasurePoint> locMeasurePoints = this
						.getMeasurePoints(fParent);
				for (MeasurePoint mpImp : impMeasurePoints) {
					MeasurePoint mpParent = null;
					for (MeasurePoint loc : locMeasurePoints) {
						if (loc.compare(mpImp)) {
							// measure point already exist local
							mpParent = loc;
							// update local object
							mpImp.setId(loc.getId());
							this.changeMeasurePoint(mpImp);
							break;
						}
					}
					if (mpParent == null) {
						mpParent = this.createMeasurePoint(fParent,
								mpImp.getPosx(), mpImp.getPosy());
						if (mpParent == null) {
							// skip all child objects for invalid floor
							continue;
						}
					}
					// import scans
					List<Scan> impScans = temp.getScans(mpImp);
					List<Scan> locScans = this.getScans(mpParent);
					for (Scan scImp : impScans) {
						Scan scParent = null;
						for (Scan loc : locScans) {
							if (loc.compare(scImp)) {
								// scan already exist local
								scParent = loc;
								// update local object
								scImp.setId(loc.getId());
								this.changeScan(scImp);
								break;
							}
						}
						if (scParent == null) {
							scParent = this.createScan(mpParent,
									scImp.getTime(), scImp.getCompass());
							if (scParent == null) {
								// skip all child objects for invalid scan
								continue;
							}
						}
						// import access points
						List<AccessPoint> impAPs = temp.getAccessPoints(scImp);
						List<AccessPoint> locAPs = this
								.getAccessPoints(scParent);
						for (AccessPoint apImp : impAPs) {
							AccessPoint apParent = null;
							for (AccessPoint loc : locAPs) {
								if (loc.compare(apImp)) {
									// access point already exist local
									apParent = loc;
									// update local object
									apImp.setId(loc.getId());
									this.changeAccessPoint(apImp);
									break;
								}
							}
							if (apParent == null) {
								apParent = this.createAccessPoint(scParent,
										apImp.getBssid(), apImp.getLevel(),
										apImp.getFreq(), apImp.getSsid(),
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
	}
}
