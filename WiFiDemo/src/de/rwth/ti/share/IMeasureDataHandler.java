package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Map;
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
	 * @return Returns all <code>Map</code>s for the <code>Building</code>
	 */
	public List<Map> getMaps(Building b);

	/**
	 * 
	 * @param mp
	 * @return Returns all <code>Scan</code>s for the <code>MeasurePoint</code>
	 */
	public List<Scan> getScans(MeasurePoint mp);

	/**
	 * 
	 * @param ap
	 * @return Returns the Scan for the AccessPoint
	 */
	public Scan getScan(AccessPoint ap);
	
	/**
	 * 
	 * @param map, compass
	 * @return Returns all Scans for the Map fitting the compass argument (compass+/-45°)
	 */
	public List<Scan> getScans(Map map, int compass);				//TODO

	/**
	 * 
	 * @param scan
	 * @return Returns all <code>AccessPoint</code>s for the <code>Scan</code>
	 */
	public List<AccessPoint> getAccessPoints(Scan scan);

	/**
	 * 
	 * @param bssid
	 * @return Returns a list with all entries for the mac
	 */
	public List<AccessPoint> getAccessPoint(String bssid);

	/**
	 * 
	 * @param scan
	 * @return Returns the <code>MeasurePoint</code> for the <code>Scan</code>
	 */
	public MeasurePoint getMeasurePoint(Scan scan);

	/**
	 * @param mp
	 * @return Returns the <code>Map</code> for the <code>MeasurePoint</code>
	 */
	public Map getMap(MeasurePoint mp);

	/**
	 * 
	 * @param map
	 * @return Returns the <code>Building</code> for the <code>Map</code>
	 */
	public Building getBuilding(Map map);

}
