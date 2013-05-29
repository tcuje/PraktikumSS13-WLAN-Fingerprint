package de.rwth.ti.loc;

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

	public Location(IMeasureDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	public Building findBuilding(List<ScanResult> aps) {
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


	public Floor findMap(List<ScanResult> aps, Building b) {
		if (aps.isEmpty() || b == null) {
			return null;
		}
		String mac = aps.get(0).BSSID;
		List<AccessPoint> entries = dataHandler.getAccessPoint(mac);
		AccessPoint ap = entries.get(0);
		Scan scan = dataHandler.getScan(ap);
		MeasurePoint mp = dataHandler.getMeasurePoint(scan);
		List<Floor> maps = dataHandler.getFloors(b);
		return maps.get(0);
	}

	public LocationResult findMP(List<ScanResult> aps, Floor map, int compass) {
		if (aps.isEmpty() || map == null) {
			return null;
		}
		List<Scan> scanEntries = dataHandler.getScans(map, compass);
		List<ScanError> errorList = new LinkedList<ScanError>();
		for (int j = 0; j < scanEntries.size(); j++) {
			double errorValue = 0;
			List<AccessPoint> entries = dataHandler.getAccessPoints(scanEntries
					.get(j));
			for (int k = 0; k < 3; k++) {
				String mac = aps.get(k).BSSID;
				int l;
				boolean success = false;
				for (l = 0; l < entries.size(); l++) {
					if (mac == entries.get(l).getBssid()) {
						success = true;
						break;
					}
				}
				if (success) {
					errorValue += ((100 + aps.get(k).level) / 100)
							* (Math.abs((aps.get(k).level)
									- entries.get(l).getLevel()));
				} else {
					errorValue += ((100 + aps.get(k).level) / 100)
							* (Math.abs((aps.get(k).level) + 100));
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
		LocationResult result = new LocationResult(map, x, y);
		return result;

	}

	public List<ScanError> sortScanError(List<ScanError> scanErrorList) {
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
