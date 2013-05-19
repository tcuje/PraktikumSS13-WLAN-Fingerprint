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

	// replaced with IMeasureDataHandler.getAllBuildings(String bssid);
	// public Position building(List<ScanResult> aps, SQLiteDatabase dbimport) {
	// Position pos = null;
	// pos.setX(-1);
	// pos.setY(-1);
	// pos.setMap_id(-1);
	// pos.setBuilding_id(-1);
	// String bssid = aps.get(0).BSSID;
	//
	// SQLiteDatabase db = dbimport.openDatabase("buildingDB", null, 0);
	// Cursor c = db.rawQuery(
	// "SELECT COLUMN_BUILDING From buildingDB WHERE COLUMN_BSSID="
	// + bssid, null);
	// int building_id = c.getInt(c.getColumnIndex("building_id"));
	// pos.setBuilding_id(building_id);
	//
	// return pos;
	// }
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

	// public Position map(List<ScanResult> aps, SQLiteDatabase dbimport,
	// int building_id) {
	// Position pos = null;
	// pos.setX(-1);
	// pos.setY(-1);
	// pos.setMap_id(-1);
	// pos.setBuilding_id(building_id);
	// String bssid = aps.get(0).BSSID;
	// /*
	// * SQLiteDatabase map_db = dbimport.openDatabase("map", null, 0); Cursor
	// * c =
	// * map_db.rawQuery("SELECT COLUMN_ID From map WHERE COLUMN_BUILDING="
	// * +building_id, null); List <Integer> list_map_id = null; for (int i=0;
	// * i<c.getCount(); i++){ c.moveToFirst();
	// * list_map_id.add(c.getInt(c.getColumnIndex("_id"))); } map_db.close();
	// * c=null; SQLiteDatabase measpts_db = dbimport.openDatabase("measpts",
	// * null, 0); Cursor c_measpts =
	// * measpts_db.rawQuery("SELECT COLUMN_ID From measpts WHERE COLUMN_MID IN"
	// * +list_map_id ,null);
	// *
	// * List <Integer> list_measpts_id = null; for (int i=0;
	// * i<c_measpts.getCount(); i++){ c_measpts.moveToFirst();
	// * list_measpts_id
	// * .add(c_measpts.getInt(c_measpts.getColumnIndex("_id"))); }
	// * measpts_db.close();
	// *
	// * SQLiteDatabase accpts_db = dbimport.openDatabase("apps", null, 0); c
	// * = accpts_db.rawQuery(
	// *
	// "SELECT COLUMN_BSSID, COLUMN_MP_ID From apps ORDER BY COLUMN_LEVEL DESC WHERE COLUMN_SCAN IN"
	// * +list_measpts_id+ "AND COLUMN_BSSID="+bssid, null); int mp_id=
	// * c.getInt(c.getColumnIndex("mp_id"));
	// */
	//
	// SQLiteDatabase accpts_db = dbimport.openDatabase("apps", null, 0);
	// Cursor c_accpts = accpts_db.rawQuery(
	// "SELECT COLUMN_MP_ID From apps ORDER BY COLUMN_LEVEL DESC WHERE COLUMN_BSSID="
	// + bssid, null);
	// int mp_id = c_accpts.getInt(c_accpts.getColumnIndex("mp_id"));
	// accpts_db.close();
	// c_accpts = null;
	// SQLiteDatabase mp_db = dbimport.openDatabase("measpts", null, 0);
	// Cursor c_measpts = mp_db
	// .rawQuery("SELECT COLUMN_MID From measpts WHERE COLUMN_ID="
	// + mp_id, null);
	// int map_id = c_measpts.getInt(c_measpts.getColumnIndex("map_id"));
	// pos.setMap_id(map_id);
	//
	// return pos;
	// }
	public Floor findMap(List<ScanResult> aps, Building b) {
		if (aps.isEmpty() || b == null) {
			return null;
		}
		String mac = aps.get(0).BSSID;
		List<AccessPoint> entries = dataHandler.getAccessPoint(mac);
		AccessPoint ap = entries.get(0);
		Scan scan = dataHandler.getScan(ap);
		MeasurePoint mp = dataHandler.getMeasurePoint(scan);
<<<<<<< HEAD
		List<Map> maps = dataHandler.getMaps(b);
		return maps.get(0);
	}

	public MeasurePoint findMP(List<ScanResult> aps, Map map, int compass){
		if (aps.isEmpty() || map==null){
			return null;
		}
		List <Scan> scanEntries = dataHandler.getScans(map, compass);					//FIXME all scans on specified map within 45deg of the compass reading
		List <ScanError> errorList=null;
		for (int j=0; j<scanEntries.size(); j++){
			double errorValue=0;
			List <AccessPoint> entries = dataHandler.getAccessPoints(scanEntries.get(j));
			for (int k=0; k<3; k++){
				String mac = aps.get(k).BSSID;
				int l;
				boolean success=false;
				for (l=0; l<entries.size();l++){
					if (mac == entries.get(l).getBssid()){
						success=true;
						break;
					}
				}
				if (success){
					errorValue+=((100+aps.get(k).level)/100)*(Math.abs((aps.get(k).level)-entries.get(l).getLevel()));
				}
				else{
					errorValue+=((100+aps.get(k).level)/100)*(Math.abs((aps.get(k).level)+100));
				}
				
				
				
			}
			ScanError scanErrorObject=null;
			scanErrorObject.setScanError(scanEntries.get(j), errorValue);
			errorList.add(scanErrorObject);		
		}
		errorList = sortScanError(errorList);
		MeasurePoint returnObject=null;
		double x=0;
		double y=0;
		double errorSum=0;
		for (int h=0; h<errorList.size(); h++){
			if (h>3){
				break;
			}
			x+=(1/(errorList.get(h).getError()))*dataHandler.getMeasurePoint(errorList.get(h).getScan()).getPosx();
			y+=(1/(errorList.get(h).getError()))*dataHandler.getMeasurePoint(errorList.get(h).getScan()).getPosy();
			errorSum+=(1/(errorList.get(h).getError()));
		}
		x=x/errorSum;
		y=y/errorSum;
		returnObject.setMapId(map.getId());
		returnObject.setPosx(x);
		returnObject.setPosy(y);
		return returnObject;
		
		}
		
		
		
	public List <ScanError> sortScanError(List <ScanError> scanErrorList){
		for (int i=0; i<scanErrorList.size(); i++){
			for (int j=0; j<scanErrorList.size()-1;j++){
				if (scanErrorList.get(j).getError()>scanErrorList.get(j+1).getError()){
					ScanError tempobject=scanErrorList.get(j);
					scanErrorList.set(j, scanErrorList.get(j+1));
					scanErrorList.set(j+1, tempobject);				
				}
			}
		}
		
=======
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

>>>>>>> origin/dev
		return scanErrorList;
	}
}
