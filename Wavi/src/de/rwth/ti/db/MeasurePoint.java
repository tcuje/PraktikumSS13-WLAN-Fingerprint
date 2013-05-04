package de.rwth.ti.db;

/**
 * This class represents a measure point
 * 
 */
public class MeasurePoint {

	public static final String TABLE_NAME = "measpts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_X = "x";
	public static final String COLUMN_Y = "y";
	public static final String COLUMN_MID = "map_id";			//"kid"->"map_id"

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_X, COLUMN_Y,
			COLUMN_MID };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_X + " integer, " + COLUMN_Y + " integer, " + COLUMN_MID
			+ " integer);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private int x;
	private int y;
	private int mid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

}
