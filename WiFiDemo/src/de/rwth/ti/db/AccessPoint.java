package de.rwth.ti.db;

/**
 * This class represents the table where access points for each scan are stored
 * 
 * @author tcuje
 * 
 */
public class AccessPoint {

	public static final String TABLE_NAME = "apps";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SCAN = "scan";
	public static final String COLUMN_BSSID = "bssid";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_FREQ = "freq";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_SCAN,
			COLUMN_BSSID, COLUMN_LEVEL, COLUMN_FREQ };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SCAN + " integer, " + COLUMN_BSSID + " text not null, "
			+ COLUMN_LEVEL + " integer, " + COLUMN_FREQ + " integer);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long scan;
	private String bssid;
	private int level;
	private int freq;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getScan() {
		return scan;
	}

	public void setScan(long scan) {
		this.scan = scan;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

}