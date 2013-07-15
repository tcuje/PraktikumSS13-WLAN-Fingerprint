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
	 * @return Returns a list with all floors from the database
	 */
	List<Floor> getAllFloors();

	/**
	 * 
	 * @return Returns a list with all buildings in alphabetical order from the
	 *         database
	 */
	List<Building> getAllBuildings();

	/**
	 * 
	 * @return Returns the number of scans stored in the database
	 */
	long countAllScans();

	/**
	 * 
	 * @return Returns the number of accesspoints stored in the database
	 */
	long countAllAccessPoints();

	/**
	 * 
	 * @return Returns the number of measurepoints stored in the database
	 */
	long countAllMeasurePoints();

	/**
	 * 
	 * @return Returns the number of floors stored in the database
	 */
	long countAllFloors();

	/**
	 * 
	 * @return Returns the number of buildings stored in the database
	 */
	long countAllBuildings();

}
