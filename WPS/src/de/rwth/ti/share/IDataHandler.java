package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
<<<<<<< HEAD
import de.rwth.ti.db.Map;
=======
import de.rwth.ti.db.Floor;
>>>>>>> origin/dev
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
<<<<<<< HEAD
	 * @return Returns a list with all maps from the database
	 */
	List<Map> getAllMaps();
=======
	 * @return Returns a list with all floors from the database
	 */
	List<Floor> getAllFloors();
>>>>>>> origin/dev

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
<<<<<<< HEAD
	 * @return Returns the number of maps stored in the database
	 */
	long countMaps();
=======
	 * @return Returns the number of floors stored in the database
	 */
	long countFloors();
>>>>>>> origin/dev

	/**
	 * 
	 * @return Returns the number of buildings stored in the database
	 */
	long countBuildings();

}
