package de.rwth.ti.db;

import de.rwth.ti.common.Constants;

/**
 * This class represents a map or floor. Multiple objects represent a building
 * 
 */
public class Floor {

	public static final String TABLE_NAME = "floors";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_BID = "bid";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_FILE = "file";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_NORTH = "north";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_BID,
			COLUMN_NAME, COLUMN_FILE, COLUMN_LEVEL, COLUMN_NORTH };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BID + " integer, " + COLUMN_NAME + " text null, "
			+ COLUMN_FILE + " blob, " + COLUMN_LEVEL + " integer, "
			+ COLUMN_NORTH + " integer);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long bId; // building id
	private String name; // name for this floor e.g. "Foyer"
	private byte[] file; // byte array that describes layout file
	private long level;
	private long north; // angle pointing to north pole on this floor

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBId() {
		return bId;
	}

	public void setBId(long bId) {
		this.bId = bId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public long getLevel() {
		return level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	public long getNorth() {
		return north;
	}

	public void setNorth(long north) {
		this.north = north;
	}

	/**
	 * 
	 * @param other
	 * @return Returns true, if lower case names are equal
	 */
	public boolean compare(Floor other) {
		String name1 = this.getName().toLowerCase(Constants.LOCALE);
		String name2 = other.getName().toLowerCase(Constants.LOCALE);
		boolean result = name1.equals(name2);
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof Floor) {
			Floor f = (Floor) other;
			if (this.getId() == f.getId()) {
				return true;
			}
		}
		return false;
	}

}
