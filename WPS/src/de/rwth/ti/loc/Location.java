package de.rwth.ti.loc;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.net.wifi.ScanResult;
import de.rwth.ti.common.Cardinal;
import de.rwth.ti.common.Constants;
import de.rwth.ti.common.DataHelper;
import de.rwth.ti.common.Tuple;
import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.share.IMeasureDataHandler;

public class Location {

	public enum CONTROL_STATE {
		NONE, FORCE_CHECK_BUILDING, FORCE_CHECK_FLOOR
	}

	private static IMeasureDataHandler dataHandler;
	private static long timeSinceFloor = 0;
	private static long timeSinceBuilding = 0;
	private static LocationResult lastScan = null;
	private static LocationResult secondToLastScan = null;
	private static Building tempBuilding;
	private static Floor tempFloor;
	private static List<LocationResult> last_ten_results = new LinkedList<LocationResult>();
	private static int accuracy;
	private static List<Tuple<Scan, List<AccessPoint>>> cache = new LinkedList<Tuple<Scan, List<AccessPoint>>>();
	private static long theTime;

	public Location(IMeasureDataHandler dataHandler) {
		Location.dataHandler = dataHandler;
	}

	/**
	 * 
	 * @param aps
	 * @param compass
	 * @param kontrollvariable
	 * @return
	 */
	public LocationResult getLocation(List<ScanResult> aps, Cardinal direction,
			CONTROL_STATE kontrollvariable) {
		if (aps.size() == 0) {
			LocationResult returnResult = new LocationResult(null, null, 0, 0,
					0);
			returnResult.setError(3);
			return returnResult;
		}
		theTime = Calendar.getInstance().getTimeInMillis();
		aps = deleteDoubles(aps);
		aps = DataHelper.sortScanResults(aps);
		if (theTime > timeSinceBuilding + 40000
				|| kontrollvariable == CONTROL_STATE.FORCE_CHECK_BUILDING) {
			Building lastBuilding = tempBuilding;
			Floor lastFloor = tempFloor;
			tempBuilding = findBuilding(aps);
			if (tempBuilding != null) {
				timeSinceBuilding = theTime;
				tempFloor = findFloor(aps, tempBuilding);
				if (tempFloor != null) {
					timeSinceFloor = theTime;
					if (lastBuilding == null
							|| lastBuilding.getId() != tempBuilding.getId()
							|| lastFloor.getId() != tempFloor.getId()) {
						last_ten_results.clear();
						secondToLastScan = null;
						lastScan = null;
						reloadCache();
					}
				} else {
					LocationResult returnResult = new LocationResult(null,
							null, 0, 0, 0);
					returnResult.setError(2);
					return returnResult;
				}
			} else {
				LocationResult returnResult = new LocationResult(null, null, 0,
						0, 0);
				returnResult.setError(1);
				return returnResult;
			}

		} else if (theTime > timeSinceFloor + 10000
				|| kontrollvariable == CONTROL_STATE.FORCE_CHECK_FLOOR) {
			Floor lastFloor = tempFloor;
			tempFloor = findFloor(aps, tempBuilding);
			if (tempFloor != null) {
				if (tempFloor.equals(lastFloor) == false) {
					last_ten_results.clear();
					secondToLastScan = null;
					lastScan = null;
					reloadCache();
				}
				timeSinceFloor = theTime;
				timeSinceBuilding = theTime;
			} else {
				LocationResult returnResult = new LocationResult(null, null, 0,
						0, 0);
				returnResult.setError(1);
				return returnResult;
			}
		}
		LocationResult result = findMP(aps, tempFloor, tempBuilding, direction);
		secondToLastScan = lastScan;
		lastScan = result;
		while (secondToLastScan == null) {
			last_ten_results.add(result);
			result = findMP(aps, tempFloor, tempBuilding, direction);
			if (result == null) {
				break;
			}
			secondToLastScan = lastScan;
			lastScan = result;
		}
		if (last_ten_results.size() >= 10) {
			last_ten_results.remove(0);
		}

		if (!(lastScan == null || result == null)) {
			if (filter_chooser(last_ten_results, result)) {
				result = filterLP2(secondToLastScan, lastScan, result, accuracy);
			} else {
				result = filterLP(secondToLastScan, lastScan, result, accuracy);
			}

		}
		last_ten_results.add(result);
		return result;
	}

	private static void reloadCache() {
		cache.clear();
		List<Scan> scanEntries = dataHandler.getScans(tempFloor, 0, 360);
		for (Scan sc : scanEntries) {
			Tuple<Scan, List<AccessPoint>> tp = new Tuple<Scan, List<AccessPoint>>(
					sc, new LinkedList<AccessPoint>());
			List<AccessPoint> entries = dataHandler.getAccessPoints(sc,
					Constants.IMPORTANT_APS);
			tp.right.addAll(entries);
			cache.add(tp);
		}
	}

	private LocationResult filterLP(LocationResult secondToLastScan,
			LocationResult lastScan, LocationResult scan, int accuracy) {
		LocationResult result;
		double x = (0.7 * scan.getX() + 0.2 * lastScan.getX() + 0.1 * secondToLastScan
				.getX());
		double y = (0.7 * scan.getY() + 0.2 * lastScan.getY() + 0.1 * secondToLastScan
				.getY());
		result = new LocationResult(scan.getBuilding(), scan.getFloor(), x, y,
				accuracy);
		return result;
	}

	private boolean filter_chooser(List<LocationResult> last_ten_results,
			LocationResult aktuell) {
		double x10 = 0;
		double y10 = 0;
		double x1 = 0;
		double y1 = 0;
		for (int j = 1; j < last_ten_results.size(); j++) {
			x10 += Math.abs(last_ten_results.get(j).getX()
					- last_ten_results.get(j - 1).getX());
			y10 += Math.abs(last_ten_results.get(j).getY()
					- last_ten_results.get(j - 1).getY());
		}
		x10 = x10 / (last_ten_results.size() - 1);
		y10 = y10 / (last_ten_results.size() - 1);
		x1 = Math.abs(aktuell.getX()
				- last_ten_results.get(last_ten_results.size() - 1).getX());
		y1 = Math.abs(aktuell.getY()
				- last_ten_results.get(last_ten_results.size() - 1).getY());
		if ((x1 > 1.5 * x10) || (y1 > 1.5 * y10)) {
			return true;
		}
		return false;
	}

	private LocationResult filterLP2(LocationResult secondToLastScan,
			LocationResult lastScan, LocationResult scan, int accuracy) {
		LocationResult result;
		double x = (0.3 * scan.getX() + 0.5 * lastScan.getX() + 0.2 * secondToLastScan
				.getX());
		double y = (0.3 * scan.getY() + 0.5 * lastScan.getY() + 0.2 * secondToLastScan
				.getY());
		result = new LocationResult(scan.getBuilding(), scan.getFloor(), x, y,
				accuracy);
		return result;
	}

	private Building findBuilding(List<ScanResult> aps) {
		if (aps.isEmpty()) {
			return null;
		}
		List<Building> bldID = new LinkedList<Building>();
		for (int i = 0; i < 3 && i < aps.size(); i++) {
			String mac = aps.get(i).BSSID;
			List<AccessPoint> entries = dataHandler.getAccessPoint(mac);
			if (entries.isEmpty()) {
				bldID.add(null);
				continue;
			}
			AccessPoint ap = entries.get(0);
			Scan scan = dataHandler.getScan(ap);
			if (scan == null) {
				bldID.add(null);
				continue;
			}
			MeasurePoint mp = dataHandler.getMeasurePoint(scan);
			if (mp == null) {
				bldID.add(null);
				continue;
			}
			Floor map = dataHandler.getFloor(mp);
			if (map == null) {
				bldID.add(null);
				continue;
			}
			bldID.add(dataHandler.getBuilding(map));
		}
		for (int j = 0; j < bldID.size(); j++) {
			if (bldID.get(j) == null) {
				bldID.remove(j);
				j--;
			}
		}
		if (bldID.size() <= 2 && bldID.size() > 0) {
			return bldID.get(0);
		} else if (bldID.size() == 0) {
			return null;
		} else if (bldID.get(0).getId() == bldID.get(1).getId()
				|| bldID.get(0).getId() == bldID.get(2).getId()) {
			return bldID.get(0);
		} else if (bldID.get(1).getId() == bldID.get(2).getId()) {
			return bldID.get(1);
		}
		return bldID.get(0);
	}

	private Floor findFloor(List<ScanResult> aps, Building b) {
		if (aps.isEmpty() || b == null) {
			return null;
		}
		List<Floor> floorlist = new LinkedList<Floor>();
		for (int i = 0; i < 3 && i < aps.size(); i++) {
			String mac = aps.get(i).BSSID;
			List<AccessPoint> entries = dataHandler.getAccessPoint(mac);
			if (entries.isEmpty()) {
				floorlist.add(null);
				continue;
			}
			AccessPoint ap = entries.get(0);
			if (ap == null) {
				floorlist.add(null);
				continue;
			}
			Scan scan = dataHandler.getScan(ap);
			if (scan == null) {
				floorlist.add(null);
				continue;
			}
			MeasurePoint mp = dataHandler.getMeasurePoint(scan);
			if (mp == null) {
				floorlist.add(null);
				continue;
			}
			floorlist.add(dataHandler.getFloor(mp));
		}
		for (int j = 0; j < floorlist.size(); j++) {
			if (floorlist.get(j) == null) {
				floorlist.remove(j);
				j--;
			}
		}
		if (floorlist.size() <= 2 && floorlist.size() > 0) {
			return floorlist.get(0);
		} else if (floorlist.size() == 0) {
			return null;
		} else if (floorlist.get(0).getId() == floorlist.get(1).getId()
				|| floorlist.get(0).getId() == floorlist.get(2).getId()) {
			return floorlist.get(0);
		} else if (floorlist.get(1).getId() == floorlist.get(2).getId()) {
			return floorlist.get(1);
		}
		return floorlist.get(0);
	}

	/**
	 * @param aps
	 * @param map
	 * @param building
	 * @param compass
	 * @return
	 */
	private LocationResult findMP(List<ScanResult> aps, Floor map,
			Building building, Cardinal compass) {
		accuracy = 2;
		if (aps.isEmpty() || map == null) {
			return null;
		}
		List<Scan> scanEntries = dataHandler.getScans(map,
				compass.getAsAzimuth(), 90);
		List<Scan> scanEntriesALL = dataHandler.getScans(map,
				compass.getAsAzimuth(), 360);
		if (6 * scanEntries.size() < scanEntriesALL.size()) {
			scanEntries = scanEntriesALL;
		}
		if (scanEntries.size() == 0) {
			LocationResult returnResult = new LocationResult(null, null, 0, 0,
					0);
			returnResult.setError(4);
			return returnResult;
		}
		List<ScanError> errorList = new LinkedList<ScanError>();
		if (aps.size() > 1) {
			int level = Math.abs(aps.get(0).level + aps.get(1).level) / 2;
			if (level > 60 && level < 80) {
				accuracy -= 1;
				if (level > 80) {
					accuracy -= 1;
				}
			}
		} else {
			if (aps.get(0).level < -60) {
				accuracy = accuracy - 1;
				if (aps.get(0).level < -80) {
					accuracy = accuracy - 1;
				}
			}
		}
		for (Tuple<Scan, List<AccessPoint>> tple : cache) {
			double errorValue = 0;
			for (int k = 0; k < 5 && k < aps.size(); k++) {
				String mac = aps.get(k).BSSID;
				AccessPoint found = null;
				for (AccessPoint ap : tple.right) {
					mac = mac.substring(0, mac.length() - 1);
					if (mac.compareTo(ap.getBssid().substring(0,
							(ap.getBssid()).length() - 1)) == 0) {
						found = ap;
						break;
					}
				}
				if (found != null) {
					int levelDifference = (Math
							.abs((int) ((aps.get(k).level) - found.getLevel())));
					errorValue += (double) ((100 + (double) aps.get(k).level) / 100)
							* levelDifference;
				} else {
					int levelDifference = (Math
							.abs((int) ((aps.get(k).level) + 100)));
					errorValue += (double) ((100 + (double) aps.get(k).level) / 100)
							* levelDifference;
				}
			}
			if (errorValue == 0) {
				MeasurePoint mp = dataHandler.getMeasurePoint(tple.left);
				LocationResult result = new LocationResult(building, map,
						mp.getPosx(), mp.getPosy(), 2);
				return result;
			}
			ScanError scanErrorObject = new ScanError();
//			errorValue=errorValue*10000;
//			errorValue=(double)Math.round(errorValue);
//			errorValue=errorValue/10000;
//			scanErrorObject.setScanError(scan, (Math.pow(errorValue, 2)));
			scanErrorObject.setScanError(tple.left, errorValue);
			errorList.add(scanErrorObject);
		}
		if (errorList.size() == 0) {
			LocationResult returnResult = new LocationResult(null, null, 0, 0,
					0);
			returnResult.setError(5);
			return returnResult;
		}
		errorList = sortScanError(errorList);
		double x = 0;
		double y = 0;
		double errorSum = 0;
		for (int h = 0; h <= 5 && h < errorList.size(); h++) {
			MeasurePoint mp = dataHandler.getMeasurePoint(errorList.get(h)
					.getScan());
			x += (1 / (errorList.get(h).getError())) * mp.getPosx();
			y += (1 / (errorList.get(h).getError())) * mp.getPosy();
			errorSum += (1 / (errorList.get(h).getError()));
		}
		if (errorSum != 0) {
			x = x / errorSum;
			y = y / errorSum;
		}
		LocationResult result = new LocationResult(building, map, x, y,
				accuracy);
		return result;

	}

	public static List<ScanResult> deleteDoubles(List<ScanResult> aps) {
		List<ScanResult> tempListe = new LinkedList<ScanResult>();
		for (ScanResult ap : aps) {
			boolean elementVorhanden = false;
			for (ScanResult known : tempListe) {
				String id1 = ap.BSSID.substring(0, ap.BSSID.length() - 1);
				String id2 = known.BSSID.substring(0, known.BSSID.length() - 1);
				if (id1.equals(id2) == true) {
					elementVorhanden = true;
					break;
				}
			}
			if (!elementVorhanden) {
				tempListe.add(ap);
			}
		}
		return tempListe;
	}

	private List<ScanError> sortScanError(List<ScanError> scanErrorList) {
		for (int i = 0; i < scanErrorList.size(); i++) {
			for (int j = 0; j < scanErrorList.size() - 1; j++) {
				if (scanErrorList.get(j).getError() > scanErrorList.get(j + 1)
						.getError()) {
					ScanError tempobject = scanErrorList.get(j);
					scanErrorList.set(j, scanErrorList.get(j + 1));
					scanErrorList.set(j + 1, tempobject);
				}
			}
		}
		return scanErrorList;
	}

}
