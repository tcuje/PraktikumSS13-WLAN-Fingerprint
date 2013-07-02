package de.rwth.ti.loc;

import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;

/**
 * This class represents the result of a location process
 * 
 */
public class LocationResult {

	private Building building;
	private Floor floor;
	private double x;
	private double y;

	public LocationResult(Building building, Floor floor, double x, double y) {
		this.floor = floor;
		this.building = building;
		this.x = x;
		this.y = y;
	}

	public Floor getFloor() {
		return floor;
	}

	public Building getBuilding() {
		return building;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
