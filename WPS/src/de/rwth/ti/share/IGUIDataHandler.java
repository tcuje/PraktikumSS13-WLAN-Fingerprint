package de.rwth.ti.share;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Map;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

/**
 * 
 * This interface handles the information exchange between gui and data storage
 * 
 */
public interface IGUIDataHandler extends IDataHandler {

	/**
	 * Creates a new <code>Building</code>
	 * 
	 * @param name
	 *            Name for the new <code>Building</code>
	 * @return Returns the new <code>Building</code> object on success,
	 *         <code>null</code> otherwise
	 */
	public Building createBuilding(String name);

	/**
	 * Creates a new <code>Map</code>
	 * 
	 * @param b
	 *            <code>Building</code> where the new <code>Map</code> belongs
	 *            to
	 * @param name
	 *            Name for the new <code>Map</code>
	 * @param file
	 *            File with further <code>Map</code> layout information
	 * @param level
	 *            Level of the <code>Map</code> inside the building
	 * @param north
	 *            Angle pointing to north
	 * @return Returns the new <code>Map</code> object on success,
	 *         <code>null</code> otherwise
	 */
	public Map createMap(Building b, String name, String file, long level,
			long north);

	/**
	 * Creates a new <code>MeasurePoint</code>
	 * 
	 * @param m
	 *            <code>Map</code> where the new <code>MeasurePoint</code>
	 *            belongs to
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @return Returns the new <code>MeasurePoint</code> object on success,
	 *         <code>null</code> otherwise
	 */
	public MeasurePoint createMeasurePoint(Map m, double x, double y);

	/**
	 * Creates a new <code>Scan</code>
	 * 
	 * @param mp
	 *            <code>MeasurePoint</code> where the new <code>Scan</code>
	 *            belongs to
	 * @param time
	 *            Time ellapsed in since 01.01.1970
	 * @param north
	 *            Angle pointing to north
	 * @return Returns the new <code>Scan</code> object on success,
	 *         <code>null</code> otherwise
	 */
	public Scan createScan(MeasurePoint mp, long time, double north);

	/**
	 * Creates a new <code>AccessPoint</code>
	 * 
	 * @param scan
	 *            <code>Scan</code> where the new <code>AccessPoint</code>
	 *            belongs to
	 * @param bssid
	 *            Mac address
	 * @param level
	 *            Signal strength
	 * @param freq
	 *            Frequency
	 * @param ssid
	 *            Wifi SSID
	 * @param props
	 *            Properties like encryption
	 * @return Returns the new <code>AccessPoint</code> object on success,
	 *         <code>null</code> otherwise
	 */
	public AccessPoint createAccessPoint(Scan scan, String bssid, long level,
			long freq, String ssid, String props);

	/**
	 * Persist a changed <code>Map</code> object
	 * 
	 * @param map
	 *            Changed object to be safed
	 * @return true on success, false otherwise
	 */
	boolean changeMap(Map map);

	/**
	 * Delete a <code>Map</code> object
	 * 
	 * @param map
	 *            Object to be deleted
	 * @return true on success, false otherwise
	 */
	boolean deleteMap(Map map);

	/**
	 * Persist a changed <code>Building</code> object
	 * 
	 * @param building
	 *            Changed object to be safed
	 * @return true on success, false otherwise
	 */
	boolean changeBuilding(Building building);

	/**
	 * Delete a <code>Building</code> object
	 * 
	 * @param building
	 *            Object to be deleted
	 * @return true on success, false otherwise
	 */
	boolean deleteBuilding(Building building);

	/**
	 * Persist a changed <code>AccessPoint</code> object
	 * 
	 * @param ap
	 *            Changed object to be safed
	 * @return true on success, false otherwise
	 */
	boolean changeAccessPoint(AccessPoint ap);

	/**
	 * Delete a <code>AccessPoint</code> object
	 * 
	 * @param ap
	 *            Object to be deleted
	 * @return true on success, false otherwise
	 */
	boolean deleteAccessPoint(AccessPoint ap);

	/**
	 * Persist a changed <code>MeasurePoint</code> object
	 * 
	 * @param mp
	 *            Changed object to be safed
	 * @return true on success, false otherwise
	 */
	boolean changeMeasurePoint(MeasurePoint mp);

	/**
	 * Delete a <code>MeasurePoint</code> object
	 * 
	 * @param mp
	 *            Object to be deleted
	 * @return true on success, false otherwise
	 */
	boolean deleteMea1surePoint(MeasurePoint mp);

	/**
	 * Persist a changed <code>Scan</code> object
	 * 
	 * @param scan
	 *            Changed object to be safed
	 * @return true on success, false otherwise
	 */
	boolean changeScan(Scan scan);

	/**
	 * Delete a <code>Scan</code> object
	 * 
	 * @param scan
	 *            Object to be deleted
	 * @return true on success, false otherwise
	 */
	boolean deleteScan(Scan scan);

}
