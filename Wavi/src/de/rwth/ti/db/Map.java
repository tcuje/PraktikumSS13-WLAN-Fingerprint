package de.rwth.ti.db;

/**
 * This class represents a map or floor
 * 
 */
public class Map {

	public static final String TABLE_NAME = "map";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATA = "data";
	public static final String COLUMN_BUILDING = "building_id";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_DATA, COLUMN_BUILDING };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DATA + " text null," +COLUMN_BUILDING+ " text null );";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private String data;
	private String building_id;
	
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
	
	public String getBuilding_id(){
		return building_id;
	}
	
	public void setBuilding_id(String building_id){
		this.building_id=building_id;
	}
	
	
	

}
