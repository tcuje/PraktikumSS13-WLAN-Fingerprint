package de.rwth.ti.db;

/**
 * This class represents a map or floor
 * 
 */
public class buildingDB {

	public static final String TABLE_NAME = "buildingDB";
	public static final String COLUMN_BSSID = "AP";
	public static final String COLUMN_BUILDING = "building_id";

	public static final String[] ALL_COLUMNS = { COLUMN_BSSID, COLUMN_BUILDING, };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_BSSID + " text primary key not null, "
			+ COLUMN_BUILDING + " integer not null,);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private String data;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
