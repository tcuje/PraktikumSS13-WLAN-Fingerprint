package de.rwth.ti.db;

/**
 * This class represents a measure point
 * 
 */
public class MeasurePoint {

	public static final String TABLE_NAME = "measpts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FLOORID = "floorid";
	public static final String COLUMN_POS_X = "posx";
	public static final String COLUMN_POS_Y = "posy";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_FLOORID,
			COLUMN_POS_X, COLUMN_POS_Y };
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_FLOORID + " integer, " + COLUMN_POS_X + " real, "
			+ COLUMN_POS_Y + " real);";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private long id;
	private long floorId;
	private double posx;
	private double posy;
	private double quality;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFloorId() {
		return floorId;
	}

	public void setFloorId(long floorId) {
		this.floorId = floorId;
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

	public double getQuality() {
		return quality;
	}

	public void setQuality(double quality) {
		this.quality = quality;
	}

	/**
	 * 
	 * @param other
	 * @return Returns true, if both coordinates are equal
	 */
	public boolean compare(MeasurePoint other) {
		// boolean resultX = true;
		// boolean resultY = true;
		boolean resultX = (this.getPosx() - other.getPosx()) < 2 * Float.MIN_VALUE;
		boolean resultY = (this.getPosy() - other.getPosy()) < 2 * Float.MIN_VALUE;
		return resultX && resultY;
	}

}
