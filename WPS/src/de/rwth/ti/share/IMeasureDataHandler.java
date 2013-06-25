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
	 * @return Returns all <code>Floor</code>s for the <code>Building</code>
	 */
	public List<Floor> getFloors(Building b);

	/**
	 * 
	 * @param mp
	 *            floor
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
	 * @param scan
	 * @param limit
	 * @return Returns a limited list of <code>AccessPoint</code>s for the
	 *         <code>Scan</code> ordered by ap level
	 */
	public List<AccessPoint> getAccessPoints(Scan scan, int limit);

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
	 * @param floor
	 * @return Returns all <code>MeasurePoint</code>s for the <code>Floor</code>
	 */
	List<MeasurePoint> getMeasurePoints(Floor floor);

	/**
	 * @param mp
	 * @return Returns the <code>Floor</code> for the <code>MeasurePoint</code>
	 */
	public Floor getFloor(MeasurePoint mp);

	/**
	 * 
	 * @param floor
	 * @return Returns the <code>Building</code> for the <code>Floor</code>
	 */
	public Building getBuilding(Floor floor);

	// FIXME Building getBuilding(int id)und Floor getFloor(int id)

	/**
	 * 
	 * @param floor
	 * @param azimuth
	 * @return Returns a list of all <code>Scan</code>s on specified floor
	 *         within 45deg of the compass reading
	 */
	public List<Scan> getScans(Floor floor, int azimuth);

}
