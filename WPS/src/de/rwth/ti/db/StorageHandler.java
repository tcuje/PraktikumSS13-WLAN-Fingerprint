package de.rwth.ti.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import de.rwth.ti.common.Constants;
import de.rwth.ti.common.DataHelper;
import de.rwth.ti.share.IGUIDataHandler;
import de.rwth.ti.share.IMeasureDataHandler;

/**
 * This class handles the database or persistent storage access
 * 
 */
public class StorageHandler implements IGUIDataHandler, IMeasureDataHandler {

	private SQLiteDatabase db;
	private Storage storage;

	public StorageHandler(Context context, String dbName) {
		this.storage = new Storage(context, dbName);
	}

	/**
	 * This should be called first to validate and upgrade the database. It may
	 * take so time, better use async tasks.
	 * 
	 * @return Returns true on success, or false if there is something wrong
	 *         with the database
	 */
	public boolean onStart() {
		try {
			// just open it once so onCreate, onUpgrade and onOpen is called
			db = storage.getWritableDatabase();
		} catch (SQLiteException ex) {
			return false;
		}
		return true;
	}

	public void onStop() {
		if (db != null && db.isOpen() == true) {
			db.close();
			db = null;
		}
	}

	public boolean isReady() {
		boolean result = (db != null);
		return result;
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
	public List<AccessPoint> getAccessPoints(Scan scan, int limit) {
		Cursor cursor = db.query(AccessPoint.TABLE_NAME,
				AccessPoint.ALL_COLUMNS, AccessPoint.COLUMN_SCANID + "=?",
				new String[] { String.valueOf(scan.getId()) }, null, null,
				AccessPoint.COLUMN_LEVEL + " DESC");
		List<AccessPoint> result = new ArrayList<AccessPoint>(limit);
		List<String> macs = new LinkedList<String>();
		if (cursor.moveToFirst() == true) {
			do {
				AccessPoint ap = cursorToAccessPoint(cursor);
				String bssid = ap.getBssid();
				bssid = bssid.substring(0, bssid.length() - 1);
				boolean found = false;
				for (String mac : macs) {
					if (mac.equals(bssid)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					result.add(ap);
					macs.add(bssid);
				}
			} while (cursor.moveToNext() == true && result.size() < limit);
		}
		cursor.close();
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
		values.put(Floor.COLUMN_BID, floor.getBId());
		values.put(Floor.COLUMN_NAME, floor.getName());
		values.put(Floor.COLUMN_LEVEL, floor.getLevel());
		values.put(Floor.COLUMN_NORTH, floor.getNorth());
		values.put(Floor.COLUMN_FILE, floor.getFile());
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
		for (MeasurePoint mp : getMeasurePoints(floor)) {
			deleteMeasurePoint(mp);
		}
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

	@Override
	public boolean deleteBuilding(Building building) {
		for (Floor f : getFloors(building)) {
			deleteFloor(f);
		}
		int result = db.delete(Building.TABLE_NAME, Building.COLUMN_ID + "=?",
				new String[] { String.valueOf(building.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeAccessPoint(AccessPoint ap) {
		ContentValues values = new ContentValues();
		values.put(AccessPoint.COLUMN_SCANID, ap.getScanId());
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
		int result = db.delete(AccessPoint.TABLE_NAME, AccessPoint.COLUMN_ID
				+ "=?", new String[] { String.valueOf(ap.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeMeasurePoint(MeasurePoint mp) {
		ContentValues values = new ContentValues();
		values.put(MeasurePoint.COLUMN_FLOORID, mp.getFloorId());
		values.put(MeasurePoint.COLUMN_POS_X, mp.getPosx());
		values.put(MeasurePoint.COLUMN_POS_Y, mp.getPosy());
		int result = db.update(MeasurePoint.TABLE_NAME, values,
				MeasurePoint.COLUMN_ID + "=?",
				new String[] { String.valueOf(mp.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteMeasurePoint(MeasurePoint mp) {
		for (Scan sc : getScans(mp)) {
			deleteScan(sc);
		}
		int result = db.delete(MeasurePoint.TABLE_NAME, MeasurePoint.COLUMN_ID
				+ "=?", new String[] { String.valueOf(mp.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeScan(Scan sc) {
		ContentValues values = new ContentValues();
		values.put(Scan.COLUMN_MPID, sc.getMpid());
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
	public boolean deleteScan(Scan scan) {
		for (AccessPoint ap : getAccessPoints(scan)) {
			deleteAccessPoint(ap);
		}
		int result = db.delete(Scan.TABLE_NAME, Scan.COLUMN_ID + "=?",
				new String[] { String.valueOf(scan.getId()) });
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
		if (cursor.moveToFirst() == true) {
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
		if (cursor.moveToFirst() == true) {
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
	public long countAllScans() {
		long result = countTable(Scan.TABLE_NAME);
		return result;
	}

	@Override
	public long countAllAccessPoints() {
		long result = countTable(AccessPoint.TABLE_NAME);
		return result;
	}

	@Override
	public long countAllMeasurePoints() {
		long result = countTable(MeasurePoint.TABLE_NAME);
		return result;
	}

	@Override
	public long countAllFloors() {
		long result = countTable(Floor.TABLE_NAME);
		return result;
	}

	@Override
	public long countAllBuildings() {
		long result = countTable(Building.TABLE_NAME);
		return result;
	}

	@Override
	public List<Scan> getScans(Floor floor, long compass, long range) {
		List<Scan> result = new LinkedList<Scan>();
		List<MeasurePoint> mps = getMeasurePoints(floor);
		for (MeasurePoint mp : mps) {
			List<Scan> scans = getScans(mp);
			if (range >= 360) {
				result.addAll(scans);
			} else {
				for (Scan scan : scans) {
					if (DataHelper.isInRange(scan.getCompass(), compass,
							Constants.ANGLE_DIFF)) {
						result.add(scan);
					}
				}
			}
		}
		return result;
	}

	public void clearDatabase() {
		storage.clearDatabase();
	}

}
