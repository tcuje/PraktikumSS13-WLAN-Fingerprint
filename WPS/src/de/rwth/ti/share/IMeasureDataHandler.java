package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

/**
 * This interface handles the information exchange between location and data
 * storage
 * 
 */
public interface IMeasureDataHandler extends IDataHandler {

	/**
	 * 
	 * @param b
	 *            building
	 * @return Returns all <code>Map</code>s for the <code>Building</code>
	 */
	public List<Floor> getFloors(Building b);

	/**
	 * 
	 * @param mp
	 *            map
	 * @return Returns all <code>Scan</code>s for the <code>MeasurePoint</code>
	 */
	public List<Scan> getScans(MeasurePoint mp);

	/**
	 * 
	 * @param ap
	 *            access point
	 * @return Returns the Scan for the AccessPoint
	 */
	public Scan getScan(AccessPoint ap);

	/**
	 * 
	 * @param scan
	 * @return Returns all <code>AccessPoint</code>s for the <code>Scan</code>
	 */
	public List<AccessPoint> getAccessPoints(Scan scan);

	/**
	 * 
	 * @param bssid
	 * @return Returns a sorted list with all entries for the mac
	 */
	public List<AccessPoint> getAccessPoint(String bssid);

	/**
	 * 
	 * @param scan
	 * @return Returns the <code>MeasurePoint</code> for the <code>Scan</code>
	 */
	public MeasurePoint getMeasurePoint(Scan scan);

	/**
	 * 
	 * @param map
	 * @return Returns all <code>MeasurePoint</code>s for the <code>Map</code>
	 */
	List<MeasurePoint> getMeasurePoints(Floor map);

	/**
	 * @param mp
	 * @return Returns the <code>Map</code> for the <code>MeasurePoint</code>
	 */
	public Floor getFloor(MeasurePoint mp);

	/**
	 * 
	 * @param map
	 * @return Returns the <code>Building</code> for the <code>Map</code>
	 */
	public Building getBuilding(Floor map);

	/**
	 * 
	 * @param map
	 * @param azimuth
	 * @return Returns a list of all <code>Scan</code>s on specified map within
	 *         45deg of the compass reading
	 */
	public List<Scan> getScans(Floor map, int azimuth);

}
