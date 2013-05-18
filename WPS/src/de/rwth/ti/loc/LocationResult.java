package de.rwth.ti.loc;

import de.rwth.ti.db.Map;

/**
 * This class represents the result of a location process
 * 
 */
public class LocationResult {

	private Map map;
	private double x;
	private double y;

	public LocationResult(Map map, double x, double y) {
		this.map = map;
		this.x = x;
		this.y = y;
	}

	public Map getMap() {
		return map;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
