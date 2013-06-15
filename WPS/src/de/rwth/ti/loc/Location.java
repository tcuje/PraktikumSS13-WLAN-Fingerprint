package de.rwth.ti.loc;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.net.wifi.ScanResult;
import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.share.IMeasureDataHandler;

public class Location {

	private IMeasureDataHandler dataHandler;
	private long timeSinceFloor = 0;
	private long timeSinceBuilding = 0;
	private long theTime;
	private Building tempBuilding;
	private Floor tempFloor;
	Calendar time = Calendar.getInstance();

	public Location(IMeasureDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	public LocationResult getLocation(List<ScanResult> aps, int compass,
			int kontrollvariable) { // kontrollvariable 0,1,2 steuert das
									// Verhalten der Funktion
									// 0 ueberlaesst den Ablauf den
									// Zeitvariablen
		theTime = time.getTime().getTime(); // 1 erzwingt Gebaeudesuche
											// 2 erzwingt FloorSuche
		if (theTime > timeSinceBuilding + 40000 || kontrollvariable == 1) {
			tempBuilding = findBuilding(aps);
			tempFloor = findMap(aps, tempBuilding);
			LocationResult result = findMP(aps, tempFloor, tempBuilding,
					compass);
			timeSinceFloor = theTime;
			timeSinceBuilding = theTime;
			return result;
		} else if (theTime > timeSinceFloor + 10000 || kontrollvariable == 2) {
			tempFloor = findMap(aps, tempBuilding);
			LocationResult result = findMP(aps, tempFloor, tempBuilding,
					compass);
			timeSinceFloor = theTime;
			timeSinceBuilding = theTime;
			return result;
		}
		LocationResult result = findMP(aps, tempFloor, tempBuilding, compass);
		return result;

	}

	private Building findBuilding(List<ScanResult> aps) {
		if (aps.isEmpty()) {
			return null;
		}
		String mac = aps.get(0).BSSID;
		List<AccessPoint> entries = dataHandler.getAccessPoint(mac);
		AccessPoint ap = entries.get(0);
		Scan scan = dataHandler.getScan(ap);
		MeasurePoint mp = dataHandler.getMeasurePoint(scan);
		Floor map = dataHandler.getFloor(mp);
		Building result = dataHandler.getBuilding(map);
		return result;
	}

	private Floor findMap(List<ScanResult> aps, Building b) {
		if (aps.isEmpty() || b == null) {
			return null;
		}
		String mac = aps.get(0).BSSID;
		List<AccessPoint> entries = dataHandler.getAccessPoint(mac);
		AccessPoint ap = entries.get(0);
		Scan scan = dataHandler.getScan(ap);
		MeasurePoint mp = dataHandler.getMeasurePoint(scan);
		Floor floor = dataHandler.getFloor(mp);
		return floor;
	}

	private LocationResult findMP(List<ScanResult> aps, Floor map,
			Building building, int compass) {
		if (aps.isEmpty() || map == null) {
			return null;
		}
		List<Scan> scanEntries = dataHandler.getScans(map, compass);
		List<ScanError> errorList = new LinkedList<ScanError>();
		for (int j = 0; j < scanEntries.size(); j++) {
			double errorValue = 0;
			List<AccessPoint> entries = dataHandler.getAccessPoints(scanEntries
					.get(j));
			for (int k = 0; k < 3 && k < aps.size(); k++) {
				String mac = aps.get(k).BSSID;
				int l;
				boolean success = false;
				for (l = 0; l < entries.size(); l++) {
					if (mac.compareTo(entries.get(l).getBssid())==0) {
						success = true;
						break;
					}
				}
				if (success) {
					errorValue += (double)((100 + (double)aps.get(k).level) / 100) * (Math.abs((int)((aps.get(k).level) - entries.get(l).getLevel())));
				} else {
					errorValue += (double)((100 + aps.get(k).level) / 100)
							* (Math.abs((int)((aps.get(k).level) + 100)));
				}

			}
			ScanError scanErrorObject = new ScanError();
			scanErrorObject.setScanError(scanEntries.get(j), errorValue);
			errorList.add(scanErrorObject);
		}
		errorList = sortScanError(errorList);
		double x = 0;
		double y = 0;
		double errorSum = 0;
		for (int h = 0; h < errorList.size(); h++) {
			if (h > 3) {
				break;
			}
			x += (1 / (errorList.get(h).getError()))
					* dataHandler.getMeasurePoint(errorList.get(h).getScan())
							.getPosx();
			y += (1 / (errorList.get(h).getError()))
					* dataHandler.getMeasurePoint(errorList.get(h).getScan())
							.getPosy();
			errorSum += (1 / (errorList.get(h).getError()));
		}
		x = x / errorSum;
		y = y / errorSum;
		LocationResult result = new LocationResult(building, map, x, y);
		return result;

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
