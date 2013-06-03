package de.rwth.ti.loc;

import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;

/**
 * This class represents the result of a location process
 * 
 */
public class LocationResult {
	private Building building;
	private Floor map;
	private double x;
	private double y;

	public LocationResult(Building building, Floor map, double x, double y) {
		this.map = map;
		this.building=building;
		this.x = x;
		this.y = y;
	}

	public Floor getMap() {
		return map;
	}
	public Building getBuilding(){
		return building;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
