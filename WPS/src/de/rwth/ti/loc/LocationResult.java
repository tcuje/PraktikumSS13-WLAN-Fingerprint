package de.rwth.ti.loc;

import de.rwth.ti.db.Floor;

/**
 * This class represents the result of a location process
 * 
 */
public class LocationResult {

	private Floor map;
	private double x;
	private double y;

	public LocationResult(Floor map, double x, double y) {
		this.map = map;
		this.x = x;
		this.y = y;
	}

	public Floor getMap() {
		return map;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
