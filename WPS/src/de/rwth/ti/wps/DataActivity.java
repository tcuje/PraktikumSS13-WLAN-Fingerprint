package de.rwth.ti.wps;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.layouthelper.BuildingSpinnerHelper;
import de.rwth.ti.layouthelper.CustomTabHelper;
import de.rwth.ti.layouthelper.FloorSpinnerHelper;
import de.rwth.ti.layouthelper.OnBuildingChangedListener;
import de.rwth.ti.layouthelper.OnFloorChangedListener;

/**
 * 
 * This acitivity allows data modification
 * 
 */
public class DataActivity extends SuperActivity implements
		OnBuildingChangedListener, OnFloorChangedListener {

	private Building selectedBuilding;
	private Building newBuilding;
	private Floor selectedFloor;
	private Floor newFloor;

	private TextView buildingHeader;
	private TextView floorHeader;
	private TextView measurePointHeader;
	private TextView scanHeader;
	private LinearLayout buildingLayout;
	private LinearLayout floorLayout;
	private LinearLayout measurePointLayout;
	private LinearLayout scanLayout;

	private CustomTabHelper tabHelper;
	private BuildingSpinnerHelper buildingHelper;
	private FloorSpinnerHelper floorHelper;

	private EditText buildingEdit;
	private EditText floorEdit;
	private EditText floorLevelEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false, false);
		setContentView(R.layout.activity_data);
		// Show the Up button in the action bar.

		selectedBuilding = null;
		selectedFloor = null;

		buildingHeader = (TextView) findViewById(R.id.dataBuildingHeader);
		floorHeader = (TextView) findViewById(R.id.dataFloorHeader);
		measurePointHeader = (TextView) findViewById(R.id.dataMeasurePointHeader);
		scanHeader = (TextView) findViewById(R.id.dataScanHeader);

		buildingLayout = (LinearLayout) findViewById(R.id.dataBuildingLayout);
		floorLayout = (LinearLayout) findViewById(R.id.dataFloorLayout);
		measurePointLayout = (LinearLayout) findViewById(R.id.dataMeasurePointLayout);
		scanLayout = (LinearLayout) findViewById(R.id.dataScanLayout);

		tabHelper = CustomTabHelper.createInstance(buildingHeader,
				buildingLayout);
		tabHelper.addTabItem(floorHeader, floorLayout);
		tabHelper.addTabItem(measurePointHeader, measurePointLayout);
		tabHelper.addTabItem(scanHeader, scanLayout);

		buildingHelper = BuildingSpinnerHelper.createInstance(this, this,
				getStorage(),
				(Spinner) findViewById(R.id.dataBuildingBuildingSpinner));
		buildingHelper
				.addSpinner((Spinner) findViewById(R.id.dataFloorBuildingSpinner));
		buildingHelper
				.addSpinner((Spinner) findViewById(R.id.dataMeasurePointBuildingSpinner));
		floorHelper = FloorSpinnerHelper.createInstance(this, this,
				getStorage(),
				(Spinner) findViewById(R.id.dataFloorFloorSpinner));
		floorHelper
				.addSpinner((Spinner) findViewById(R.id.dataMeasurePointFloorSpinner));
		buildingHelper.addListener(floorHelper);

		buildingEdit = (EditText) findViewById(R.id.dataBuildingRenameEdit);
		floorEdit = (EditText) findViewById(R.id.dataFloorRenameEdit);
		floorLevelEdit = (EditText) findViewById(R.id.dataFloorFloorLevelEdit);
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		buildingHelper.refresh();
	}

	@Override
	public void buildingChanged(BuildingSpinnerHelper helper) {
		selectedBuilding = helper.getSelectedBuilding();
	}

	@Override
	public void floorChanged(FloorSpinnerHelper helper) {
		selectedFloor = helper.getSelectedFloor();
		if (selectedFloor != null) {
			floorLevelEdit.setText(String.valueOf(selectedFloor.getLevel()));
		}
	}

	// ///////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	private void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void dataDeleteBuilding(View v) {
		if (selectedBuilding != null) {
			if (getStorage().deleteBuilding(selectedBuilding)) {
				buildingHelper.refresh();
				makeToast(String.format(getString(R.string.success_delete),
						getString(R.string.building)));
			} else {
				makeToast(String.format(getString(R.string.error_delete),
						getString(R.string.building)));
			}
		}
	}

	public void dataRenameBuilding(View v) {
		String buildingName = buildingEdit.getText().toString().trim();
		newBuilding = new Building();
		newBuilding.setName(buildingName);
		newBuilding.setId(selectedBuilding.getId());
		if (getStorage().changeBuilding(newBuilding)) {
			buildingHelper.refresh();
			buildingEdit.setText("");
			makeToast(String.format(getString(R.string.success_save),
					getString(R.string.building)));
		} else {
			makeToast(String.format(getString(R.string.error_save),
					getString(R.string.building)));
		}
	}

	public void dataDeleteAllMP(View v) {
		List<MeasurePoint> mpList = getStorage().getAllMeasurePoints();
		boolean success = true;
		for (MeasurePoint MP : mpList) {
			if (!getStorage().deleteMeasurePoint(MP)) {
				success = false;
			}

		}
		if (success) {
			makeToast(String.format(getString(R.string.success_delete),
					getString(R.string.measurepoints)));
		} else {
			makeToast(String.format(getString(R.string.error_delete),
					getString(R.string.measurepoints)));
		}
	}

	public void dataDeleteFloorMP(View v) {
		if (selectedBuilding != null) {
			if (selectedFloor != null) {
				List<MeasurePoint> mpList = getStorage().getMeasurePoints(
						selectedFloor);
				boolean success = true;
				for (MeasurePoint MP : mpList) {
					if (!getStorage().deleteMeasurePoint(MP)) {
						success = false;
					}
				}
				if (success) {
					makeToast(String.format(getString(R.string.success_delete),
							getString(R.string.measurepoints)));
				} else {
					makeToast(String.format(getString(R.string.error_delete),
							getString(R.string.measurepoints)));
				}
			}
		}
	}

	public void dataDeleteLastMP(View v) {
		List<MeasurePoint> mpList = getStorage().getAllMeasurePoints();
		MeasurePoint deleteMP = mpList.get(mpList.size() - 1);
		if (getStorage().deleteMeasurePoint(deleteMP)) {
			makeToast(String.format(getString(R.string.success_delete),
					getString(R.string.measurepoint)));
		} else {
			makeToast(String.format(getString(R.string.error_delete),
					getString(R.string.measurepoint)));
		}
	}

	public void dataDeleteFloor(View v) {
		if (selectedBuilding != null) {
			// FloorList = storage.getFloors(selectedBuilding);
			if (selectedFloor != null) {
				if (getStorage().deleteFloor(selectedFloor)) {
					buildingHelper.refresh();
					floorHelper.refresh();
					makeToast(String.format(getString(R.string.success_save),
							getString(R.string.floor)));
				} else {
					makeToast(String.format(getString(R.string.error_save),
							getString(R.string.floor)));
				}
			}
		}
	}

	public void dataSaveFloor(View v) {
		String floorName = floorEdit.getText().toString().trim();
		long floorLevel = Long.valueOf(
				floorLevelEdit.getText().toString().trim()).longValue();
		if (selectedBuilding != null) {
			if (selectedFloor != null) {
				newFloor = new Floor();
				newFloor.setId(selectedFloor.getId());
				newFloor.setLevel(floorLevel);
				newFloor.setName(floorName);
				if (getStorage().changeFloor(newFloor)) {
					buildingHelper.refresh();
					floorHelper.refresh();
					makeToast(String.format(getString(R.string.success_save),
							getString(R.string.floor)));
				} else {
					makeToast(String.format(getString(R.string.error_save),
							getString(R.string.floor)));
				}
			}
		}
	}

	public void dataDeleteLastScan(View v) {
		List<Scan> scanList = getStorage().getAllScans();
		Scan sc = scanList.get(scanList.size() - 1);
		if (getStorage().deleteScan(sc)) {
			makeToast(String.format(getString(R.string.success_delete),
					getString(R.string.scan)));
		} else {
			makeToast(String.format(getString(R.string.error_delete),
					getString(R.string.scan)));
		}
	}

	public void dataDeleteAllScan(View v) {
		List<Scan> scanList = getStorage().getAllScans();
		boolean success = true;
		for (Scan sc : scanList) {
			if (getStorage().deleteScan(sc) == false) {
				success = false;
			}
		}
		if (success) {
			makeToast(String.format(getString(R.string.success_delete),
					getString(R.string.scan)));
		} else {
			makeToast(String.format(getString(R.string.error_delete),
					getString(R.string.scan)));
		}
	}

}
