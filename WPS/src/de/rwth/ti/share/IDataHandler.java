package de.rwth.ti.share;

import java.util.List;

import de.rwth.ti.db.AccessPoint;
import de.rwth.ti.db.Map;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

public interface IDataHandler {

	List<AccessPoint> getAllAccessPoints();

	List<Scan> getAllScans();

	List<MeasurePoint> getAllMeasurePoints();

	List<Map> getAllMaps();

	long countScans();

	long countAccessPoints();

	long countCheckpoints();

	long countMaps();

	long countBuildings();

}
