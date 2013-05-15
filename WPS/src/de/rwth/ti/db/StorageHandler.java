package de.rwth.ti.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.rwth.ti.share.IDataHandler;
import de.rwth.ti.share.IGUIDataHandler;
import de.rwth.ti.share.IMeasureDataHandler;

/**
 * This class handles the database or persistent storage access
 * 
 */
public class StorageHandler implements IDataHandler, IGUIDataHandler,
		IMeasureDataHandler {

	private SQLiteDatabase db;
	private Storage storage;

	public StorageHandler(Context context) {
		storage = new Storage(context);
	}

	public void onStart() throws SQLException {
		db = storage.getWritableDatabase();
	}

	public void onStop() {
		storage.close();
	}

	// FIXME make this async
	public void exportDatabase(String filename) throws IOException {
		db.close();
		storage.exportDatabase(filename);
		db = storage.getWritableDatabase();
	}

	// FIXME make this async
	public void importDatabase(String filename) throws IOException {
		db.close();
		storage.importDatabase(filename);
		db = storage.getWritableDatabase();
	}

	@Override
	public AccessPoint createAccessPoint(Scan scan, String bssid, long level,
			long freq, String ssid, String props) {
		ContentValues values = new ContentValues();
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
		Scan result = cursorToScan(cursor);
		return result;
	}

	@Override
	public MeasurePoint createMeasurePoint(Map m, double x, double y) {
		ContentValues values = new ContentValues();
		if (m != null) {
			values.put(MeasurePoint.COLUMN_MAPID, m.getId());
		}
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
		result.setMapId(cursor.getLong(1));
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
		cursor.moveToFirst();
		MeasurePoint result = cursorToMeasurePoint(cursor);
		cursor.close();
		return result;
	}

	@Override
	public Map createMap(Building b, String name, String file, long level,
			long north) {
		ContentValues values = new ContentValues();
		if (b != null) {
			values.put(Map.COLUMN_BID, b.getId());
		}
		if (name != null) {
			values.put(Map.COLUMN_NAME, name);
		}
		if (file != null) {
			values.put(Map.COLUMN_FILE, file);
		}
		values.put(Map.COLUMN_LEVEL, level);
		values.put(Map.COLUMN_NORTH, north);
		long insertId = db.insert(Map.TABLE_NAME, null, values);
		Cursor cursor = db.query(Map.TABLE_NAME, Map.ALL_COLUMNS, Map.COLUMN_ID
				+ "=?", new String[] { String.valueOf(insertId) }, null, null,
				null);
		Map result = null;
		if (cursor.moveToFirst()) {
			result = cursorToMap(cursor);
		}
		cursor.close();
		return result;
	}

	private Map cursorToMap(Cursor cursor) {
		Map result = new Map();
		result.setId(cursor.getLong(0));
		result.setBId(cursor.getLong(1));
		result.setName(cursor.getString(2));
		result.setFile(cursor.getBlob(3));
		result.setLevel(cursor.getLong(4));
		result.setNorth(cursor.getLong(5));
		return result;
	}

	private List<Map> cursorToMaps(Cursor cursor) {
		List<Map> result = new ArrayList<Map>(cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				Map map = cursorToMap(cursor);
				result.add(map);
			} while (cursor.moveToNext() == true);
		}
		cursor.close();
		return result;
	}

	@Override
	public List<Map> getAllMaps() {
		Cursor cursor = db.query(Map.TABLE_NAME, Map.ALL_COLUMNS, null, null,
				null, null, null);
		List<Map> result = cursorToMaps(cursor);
		return result;
	}

	@Override
	public List<Map> getMaps(Building b) {
		Cursor cursor = db.query(Map.TABLE_NAME, Map.ALL_COLUMNS,
				Map.COLUMN_BID + "=?",
				new String[] { String.valueOf(b.getId()) }, null, null, null,
				null);
		List<Map> result = cursorToMaps(cursor);
		return result;
	}

	@Override
	public Map getMap(MeasurePoint mp) {
		Cursor cursor = db.query(Map.TABLE_NAME, Map.ALL_COLUMNS, Map.COLUMN_ID
				+ "=?", new String[] { String.valueOf(mp.getMapId()) }, null,
				null, null, null);
		Map result = cursorToMap(cursor);
		return result;
	}

	@Override
	public boolean changeMap(Map map) {
		ContentValues values = new ContentValues();
		if (map.getName() != null) {
			values.put(Map.COLUMN_NAME, map.getName());
		}
		int result = db.update(Map.TABLE_NAME, values, Map.COLUMN_ID,
				new String[] { String.valueOf(map.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteMap(Map map) {
		int result = db.delete(Map.TABLE_NAME, Map.COLUMN_ID,
				new String[] { String.valueOf(map.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeBuilding(Building building) {
		ContentValues values = new ContentValues();
		if (building.getName() != null) {
			values.put(Map.COLUMN_NAME, building.getName());
		}
		int result = db.update(Building.TABLE_NAME, values, Building.COLUMN_ID,
				new String[] { String.valueOf(building.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteBuilding(Building building) {
		int result = db.delete(Building.TABLE_NAME, Building.COLUMN_ID,
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
		values.put(AccessPoint.COLUMN_PROPS, ap.getSsid());
		int result = db.update(AccessPoint.TABLE_NAME, values,
				AccessPoint.COLUMN_ID,
				new String[] { String.valueOf(ap.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteAccessPoint(AccessPoint ap) {
		int result = db.delete(AccessPoint.TABLE_NAME, AccessPoint.COLUMN_ID,
				new String[] { String.valueOf(ap.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean changeMeasurePoint(MeasurePoint mp) {

		ContentValues values = new ContentValues();
		values.put(MeasurePoint.COLUMN_MAPID, mp.getId());
		int result = db.update(AccessPoint.TABLE_NAME, values,
				AccessPoint.COLUMN_ID,
				new String[] { String.valueOf(mp.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteMea1surePoint(MeasurePoint mp) {
		int result = db.delete(MeasurePoint.TABLE_NAME, MeasurePoint.COLUMN_ID,
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
		int result = db.update(AccessPoint.TABLE_NAME, values,
				AccessPoint.COLUMN_ID,
				new String[] { String.valueOf(sc.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteScan(Scan sc) {
		int result = db.delete(Scan.TABLE_NAME, Scan.COLUMN_ID,
				new String[] { String.valueOf(sc.getId()) });
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public Building createBuilding(String name) {
		ContentValues values = new ContentValues();
		if (name != null) {
			values.put(Building.COLUMN_NAME, name);
		}
		if (values.size() == 0) {
			return null;
		}
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
				null, null, null, null, null);
		List<Building> result = cursorToBuildings(cursor);
		return result;
	}

	@Override
	public Building getBuilding(Map map) {
		Cursor cursor = db
				.query(Building.TABLE_NAME, Building.ALL_COLUMNS,
						Building.COLUMN_ID + "=?",
						new String[] { String.valueOf(map.getBId()) }, null,
						null, null);
		cursor.moveToFirst();
		Building result = cursorToBuilding(cursor);
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
	public long countMaps() {
		long result = countTable(Map.TABLE_NAME);
		return result;
	}

	@Override
	public long countBuildings() {
		long result = countTable(Building.TABLE_NAME);
		return result;
	}

}
