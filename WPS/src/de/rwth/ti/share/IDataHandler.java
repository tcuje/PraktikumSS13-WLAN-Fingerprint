package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

public interface IDataHandler {

	/**
	 * 
	 * @return Returns a list with all accesspoints from the database
	 */
	List<AccessPoint> getAllAccessPoints();

	/**
	 * 
	 * @return Returns a list with all scans from the database
	 */
	List<Scan> getAllScans();

	/**
	 * 
	 * @return Returns a list with all measurepoints from the database
	 */
	List<MeasurePoint> getAllMeasurePoints();

	/**
	 * 
	 * @return Returns a list with all maps from the database
	 */
	List<Floor> getAllFloors();

	/**
	 * 
	 * @return Returns a list with all buildings from the database
	 */
	List<Building> getAllBuildings();

	/**
	 * 
	 * @return Returns the number of scans stored in the database
	 */
	long countScans();

	/**
	 * 
	 * @return Returns the number of accesspoints stored in the database
	 */
	long countAccessPoints();

	/**
	 * 
	 * @return Returns the number of measurepoints stored in the database
	 */
	long countMeasurePoints();

	/**
	 * 
	 * @return Returns the number of maps stored in the database
	 */
	long countFloors();

	/**
	 * 
	 * @return Returns the number of buildings stored in the database
	 */
	long countBuildings();

}
