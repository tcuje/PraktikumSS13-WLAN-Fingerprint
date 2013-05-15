package de.rwth.ti.db;

/**
 * This class represents a measure point
 * 
 */
public class MeasurePoint {

	public static final String TABLE_NAME = "measpts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MAPID = "mapid";
	public static final String COLUMN_POS_X = "posx";
	public static final String COLUMN_POS_Y = "posy";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_MAPID,
			COLUMN_POS_X, COLUMN_POS_Y };
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MAPID + " integer REFERENCES " + Map.TABLE_NAME + "("
			+ Map.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
			+ COLUMN_POS_X + " real, " + COLUMN_POS_Y + " real);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long mapId;
	private double posx;
	private double posy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMapId() {
		return mapId;
	}

	public void setMapId(long mapId) {
		this.mapId = mapId;
	}

	public double getPosx() {
		return posx;
	}

	public void setPosx(double posx) {
		this.posx = posx;
	}

	public double getPosy() {
		return posy;
	}

	public void setPosy(double posy) {
		this.posy = posy;
	}

}
