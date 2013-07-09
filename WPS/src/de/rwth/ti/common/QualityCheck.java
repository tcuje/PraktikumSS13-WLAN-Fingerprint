package de.rwth.ti.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.share.IMeasureDataHandler;

public class QualityCheck {

	/**
	 * 
	 * @param dataHandler
	 * @param mp
	 * @return Returns a quality value for this measure point. Best value is 1.0
	 */
	public static double getQuality(IMeasureDataHandler dataHandler,
			MeasurePoint mp) {
		double result = 1.0;
		List<Scan> scans = dataHandler.getScans(mp);
		// contains all access points for this scan
		Map<String, List<AccessPoint>> allAps = new HashMap<String, List<AccessPoint>>();
		for (Scan scan : scans) {
			List<AccessPoint> aps = dataHandler.getAccessPoints(scan,
					Constants.IMPORTANT_APS);
			for (AccessPoint ap : aps) {
				String mac = ap.getBssid();
				// get the list for this ap
				List<AccessPoint> apList = allAps.get(mac);
				if (apList == null) {
					apList = new LinkedList<AccessPoint>();
					allAps.put(mac, apList);
				}
				// add it
				apList.add(ap);
			}
		}
		// check the overall number of aps
		if (allAps.size() < Math.floor(Constants.IMPORTANT_APS / 2 + 1)) {
			// less than 50% of important aps
			result *= 0.25;
		} else if (allAps.size() < Constants.IMPORTANT_APS) {
			// less than 100% of important aps
			result *= 0.5;
		}
		// check all aps
		double apsQuality = 0;
		for (List<AccessPoint> apList : allAps.values()) {
			double apScore = 1.0;
			// check popularity for this ap
			double pop = (double) apList.size() / scans.size();
			if (pop < 0.25) {
				// ap is in less than 25% scans
				apScore *= 0.25;
			} else if (pop < 0.5) {
				// ap is in less than 50% scans
				apScore *= 0.5;
			}
			// check signal strength for this ap
			double avgLevel = 0;
			for (AccessPoint ap : apList) {
				avgLevel += ap.getLevel();
			}
			avgLevel /= apList.size();
			if (avgLevel < -80) {
				// ap average level is very low
				apScore *= 0.25;
			} else if (avgLevel < -60) {
				apScore *= 0.5;
			}
			apsQuality += apScore;
		}
		apsQuality /= allAps.size();
		result *= apsQuality;
		return result;
	}

}
