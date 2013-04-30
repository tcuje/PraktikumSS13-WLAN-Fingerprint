package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.Scan;

/**
 * This interface handles the information exchange between positioning and data
 * storage
 * 
 */
public interface IMeasureDataHandler {

	/**
	 * 
	 * @return Returns a list with all known scans
	 */
	public List<Scan> getAllScans();

}
