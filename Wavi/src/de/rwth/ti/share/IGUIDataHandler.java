package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.Map;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

/**
 * 
 * This interface handles the information exchange between gui and data storage
 * 
 */
public interface IGUIDataHandler {

	/**
	 * 
	 * @return Returns a list with all buildings
	 */
	public List<Map> getAllMaps();

	/**
	 * 
	 * @param file
	 * @param building
	 * @param floor
	 * @param orientation
	 * @return
	 */
	public Map createMap(String file, String building, int floor,
			int orientation);

	/**
	 * 
	 * @param m
	 * @param x
	 * @param y
	 * @return
	 */
	public MeasurePoint createMeasurePoint(Map m, double x, double y);

	/**
	 * 
	 * @return
	 */
	public Scan createScan();

}
