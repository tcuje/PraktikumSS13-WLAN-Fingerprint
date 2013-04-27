package de.rwth.ti.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class handles the database or persistent storage access
 * 
 * @author tcuje
 * 
 */
public class StorageHandler {

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

	/**
	 * Add the access point to measurement database
	 * 
	 * @return The id for the new access point
	 */
	public long addAp(Scan scan, String bssid, int level, int freq,
			String ssid, String props) {
		ContentValues values = new ContentValues();
		values.put(AccessPoint.COLUMN_SCAN, scan.getId());
		values.put(AccessPoint.COLUMN_BSSID, bssid);
		values.put(AccessPoint.COLUMN_LEVEL, level);
		values.put(AccessPoint.COLUMN_FREQ, freq);
		values.put(AccessPoint.COLUMN_SSID, ssid);
		values.put(AccessPoint.COLUMN_PROPS, props);
		long insertId = db.insert(AccessPoint.TABLE_NAME, null, values);
		return insertId;
	}

	public AccessPoint cursorToAp(Cursor cursor) {
		AccessPoint result = new AccessPoint();
		result.setId(cursor.getLong(0));
		result.setScan(cursor.getLong(1));
		result.setBssid(cursor.getString(2));
		result.setLevel(cursor.getInt(3));
		result.setFreq(cursor.getInt(4));
		result.setSsid(cursor.getString(5));
		result.setProps(cursor.getString(6));
		return result;
	}

	/**
	 * 
	 * @param cpid
	 *            checkpoint id
	 * @param time
	 *            timestamp in seconds since 1.1.1970
	 * @param compass
	 *            azimut value
	 * @return Returns the readonly scan object
	 */
	public Scan addScan(long cpid, long time, double compass) {
		ContentValues values = new ContentValues();
		values.put(Scan.COLUMN_CPID, cpid);
		values.put(Scan.COLUMN_TIME, time);
		values.put(Scan.COLUMN_COMPASS, compass);
		long insertId = db.insert(Scan.TABLE_NAME, null, values);
		Cursor cursor = db.query(Scan.TABLE_NAME, Scan.ALL_COLUMNS,
				Scan.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Scan result = cursorToScan(cursor);
		cursor.close();
		return result;
	}

	public Scan cursorToScan(Cursor cursor) {
		Scan result = new Scan();
		result.setId(cursor.getLong(0));
		result.setCpid(cursor.getLong(1));
		result.setTime(cursor.getLong(2));
		result.setCompass(cursor.getLong(3));
		return result;
	}

	/**
	 * 
	 * @param mapid
	 *            The id of the map where the checkpoint is placed
	 * @param x
	 *            The x coordinate for this checkpoint on the referenced map
	 * @param y
	 *            The y coordinate for this checkpoint on the referenced map
	 * @return Returns the id for the newly added checkpoint
	 */
	public Checkpoint addCheckpoint(long mapid, double x, double y) {
		ContentValues values = new ContentValues();
		values.put(Checkpoint.COLUMN_MID, mapid);
		values.put(Checkpoint.COLUMN_POS_X, x);
		values.put(Checkpoint.COLUMN_POS_Y, y);
		long insertId = db.insert(Checkpoint.TABLE_NAME, null, values);
		Cursor cursor = db
				.query(Checkpoint.TABLE_NAME, Checkpoint.ALL_COLUMNS,
						Checkpoint.COLUMN_ID + " = " + insertId, null, null,
						null, null);
		cursor.moveToFirst();
		Checkpoint result = cursorToCheckpoint(cursor);
		cursor.close();
		return result;
	}

	public Checkpoint cursorToCheckpoint(Cursor cursor) {
		Checkpoint result = new Checkpoint();
		result.setId(cursor.getLong(0));
		result.setMid(cursor.getLong(1));
		result.setPosx(cursor.getDouble(2));
		result.setPosy(cursor.getDouble(3));
		return result;
	}

	/**
	 * 
	 * @param name
	 *            Name for the new map or <code>null</code>
	 * @param filename
	 *            absolute filepath for the new map or <code>null</code>
	 * @return The new map or null if all parameters are <code>null</code>
	 */
	public Map addMap(String name, String filename) {
		ContentValues values = new ContentValues();
		if (name != null) {
			values.put(Map.COLUMN_NAME, name);
		}
		if (filename != null) {
			values.put(Map.COLUMN_FILE, filename);
		}
		if (values.size() == 0) {
			return null;
		}
		long insertId = db.insert(Map.TABLE_NAME, null, values);
		Cursor cursor = db.query(Map.TABLE_NAME, Map.ALL_COLUMNS, Map.COLUMN_ID
				+ " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Map result = cursorToMap(cursor);
		cursor.close();
		return result;
	}

	public Map cursorToMap(Cursor cursor) {
		Map result = new Map();
		result.setId(cursor.getLong(0));
		result.setName(cursor.getString(1));
		result.setFile(cursor.getString(2));
		return result;
	}

	public long countScans() {
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Scan.TABLE_NAME,
				null);
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		cursor.close();
		return result;
	}

	public long countAccessPoints() {
		Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM "
				+ AccessPoint.TABLE_NAME, null);
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		cursor.close();
		return result;
	}

}
