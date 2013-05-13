package de.rwth.ti.db;

/**
 * This class represents a map or level. Multiple objects represent a building
 * 
 */
public class Map {

	public static final String TABLE_NAME = "maps";
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
			+ COLUMN_BID + " integer REFERENCES " + Building.TABLE_NAME + "("
			+ Building.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
			+ COLUMN_NAME + " text null, " + COLUMN_FILE + " blob, "
			+ COLUMN_LEVEL + " integer, " + COLUMN_NORTH + " integer);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long bId; // building id
	private String name; // name for this floor e.g. "Foyer"
	private byte[] file; // byte array that describes layout file
	private long level;
	private long north; // angle pointing to north pole on this map

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

}
