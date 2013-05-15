package de.rwth.ti.db;

/**
 * This class represents the table where each scan is stored
 * 
 */
public class Scan {

	public static final String TABLE_NAME = "scans";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MPID = "mpid";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_COMPASS = "compass";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_MPID,
			COLUMN_TIME, COLUMN_COMPASS };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MPID + " integer REFERENCES " + MeasurePoint.TABLE_NAME
			+ "(" + MeasurePoint.COLUMN_ID
			+ ") ON UPDATE CASCADE ON DELETE CASCADE, " + COLUMN_TIME
			+ " integer, " + COLUMN_COMPASS + " integer);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long mpid; // measure point id
	private long time;
	private long compass;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMpid() {
		return mpid;
	}

	public void setMpid(long mpid) {
		this.mpid = mpid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getCompass() {
		return compass;
	}

	public void setCompass(long compass) {
		this.compass = compass;
	}

}
