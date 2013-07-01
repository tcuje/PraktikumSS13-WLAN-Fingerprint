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

	/**
	 * 
	 * @param aps
	 * @param compass
	 * @param kontrollvariable
	 *            0,1,2 steuert das Verhalten der Funktion 0 ueberlaesst den
	 *            Ablauf den Zeitvariablen
	 * @return
	 */
	public LocationResult getLocation(List<ScanResult> aps, int compass,
			int kontrollvariable) {
		theTime = time.getTime().getTime();
		aps = deleteDoubles(aps);

		if (theTime > timeSinceBuilding + 40000 || kontrollvariable == 1) {
			tempBuilding = findBuilding(aps);
			tempFloor = findFloor(aps, tempBuilding);
			timeSinceFloor = theTime;
			timeSinceBuilding = theTime;
		} else if (theTime > timeSinceFloor + 10000 || kontrollvariable == 2) {
			tempFloor = findFloor(aps, tempBuilding);
			timeSinceFloor = theTime;
			timeSinceBuilding = theTime;
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
		if (entries.isEmpty()) {
			return null;
		}
		AccessPoint ap = entries.get(0);
		Scan scan = dataHandler.getScan(ap);
		if (scan == null) {
			return null;
		}
		MeasurePoint mp = dataHandler.getMeasurePoint(scan);
		if (mp == null) {
			return null;
		}
		Floor map = dataHandler.getFloor(mp);
		if (map == null) {
			return null;
		}
		Building result = dataHandler.getBuilding(map);
		return result;
	}

	private Floor findFloor(List<ScanResult> aps, Building b) {
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
			for (int k = 0; k < 5 && k < aps.size(); k++) {
				String mac = aps.get(k).BSSID;
				int l;
				boolean success = false;
				for (l = 0; l < entries.size(); l++) {
					if (mac.compareTo(entries.get(l).getBssid()) == 0) {
						success = true;
						break;
					}
				}
				if (success) {
					errorValue += (double) ((100 + (double) aps.get(k).level) / 100)
							* (Math.abs((int) ((aps.get(k).level) - entries
									.get(l).getLevel())));
				} else {
					errorValue += (double) ((100 + (double) aps.get(k).level) / 100)
							* (Math.abs((int) ((aps.get(k).level) + 100)));
				}
			}
			if (errorValue == 0) {
				LocationResult result = new LocationResult(building, map,
						dataHandler.getMeasurePoint(scanEntries.get(j))
								.getPosx(), dataHandler.getMeasurePoint(
								scanEntries.get(j)).getPosy());
				return result;
			}

			ScanError scanErrorObject = new ScanError();
//			errorValue=errorValue*10000;
//			errorValue=(double)Math.round(errorValue);
//			errorValue=errorValue/10000;
//			scanErrorObject.setScanError(scanEntries.get(j), (Math.pow(errorValue, 2)));
			scanErrorObject.setScanError(scanEntries.get(j), errorValue);
			errorList.add(scanErrorObject);
		}
		errorList = sortScanError(errorList);
		double x = 0;
		double y = 0;
		double errorSum = 0;
		for (int h = 0; h < errorList.size(); h++) {
			if (h > 5) {
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
		if (errorSum != 0) {
			x = x / errorSum;
			y = y / errorSum;
		}
		LocationResult result = new LocationResult(building, map, x, y);
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
