package de.rwth.ti.db;

public class MeasurePoints {
	
	public static final String TABLE_NAME = "measpts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_X = "x";
	public static final String COLUMN_Y = "y";
	public static final String COLUMN_kid = "kid";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_X,
			COLUMN_Y, COLUMN_kid };

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_X + " integer, " + COLUMN_Y + " integer, " + COLUMN_kid + " integer);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private int x;
	private int y;
	private int kid;
	
	
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
	public int getKid() {
		return kid;
	}
	public void setKid(int kid) {
		this.kid = kid;
	}

}
