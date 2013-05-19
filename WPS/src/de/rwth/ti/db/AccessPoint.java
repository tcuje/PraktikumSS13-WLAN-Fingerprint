package de.rwth.ti.db;

import java.util.Locale;

/**
 * This class represents the table where access points for each scan are stored
 * 
 */
public class AccessPoint {

	public static final String TABLE_NAME = "apps";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SCANID = "scanid";
	public static final String COLUMN_BSSID = "bssid";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_FREQ = "freq";
	public static final String COLUMN_SSID = "ssid";
	public static final String COLUMN_PROPS = "props";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_SCANID,
			COLUMN_BSSID, COLUMN_LEVEL, COLUMN_FREQ, COLUMN_SSID, COLUMN_PROPS };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SCANID + " integer REFERENCES " + Scan.TABLE_NAME + "("
			+ Scan.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
			+ COLUMN_BSSID + " text not null, " + COLUMN_LEVEL + " integer, "
			+ COLUMN_FREQ + " integer, " + COLUMN_SSID + " text null, "
			+ COLUMN_PROPS + " text null);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long scanId;
	private String bssid;
	private int level;
	private int freq;
	private String ssid;
	private String props;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getScanId() {
		return scanId;
	}

	public void setScanId(long scanId) {
		this.scanId = scanId;
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

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}

	/**
	 * 
	 * @param other
	 * @return Returns true, if bssid, frequency and level are equal
	 */
	public boolean compare(AccessPoint other) {
		String bssid1 = this.getBssid().toLowerCase(Locale.GERMAN);
		String bssid2 = other.getBssid().toLowerCase(Locale.GERMAN);
		boolean bssid = bssid1.equals(bssid2);
		boolean freq = this.getFreq() == other.getFreq();
		boolean level = this.getLevel() == other.getLevel();
		return bssid && freq && level;
	}
}
