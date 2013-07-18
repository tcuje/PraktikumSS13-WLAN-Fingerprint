package de.rwth.ti.wps;

import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;
import de.rwth.ti.layouthelper.BuildingSpinnerHelper;
import de.rwth.ti.layouthelper.ConfirmDialogHelper;
import de.rwth.ti.layouthelper.ConfirmDialogListener;
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
		ConfirmDialogListener, OnBuildingChangedListener,
		OnFloorChangedListener {

	public enum Action {
		renameBuilding, deleteBuilding, saveFloor, deleteFloor, deleteFloorMP, deleteLastMP, deleteAllMP, deleteLastScan, deleteAllScan;

		public static Action getFromId(int id) {
			return Action.values()[id];
		}

		public int toInt() {
			return this.ordinal();
		}
	};

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

	private ConfirmDialogHelper dialogHelper;
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

		dialogHelper = new ConfirmDialogHelper(this, this);

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

		TextWatcher textWatch = new TextWatcher() {
			public void afterTextChanged(Editable s) {
				onFloorLevelChanged();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			};

			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
			};
		};
		floorLevelEdit.addTextChangedListener(textWatch);
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
		} else {
			floorLevelEdit.setText("");
		}
	}

	public void onFloorLevelChanged() {
		int tFloorLevel;
		String tFloorLevelText = floorLevelEdit.getText().toString().trim();
		if (tFloorLevelText.equals("") || tFloorLevelText.equals("-")) {
			tFloorLevel = 0;
		} else {
			tFloorLevel = Integer.parseInt(tFloorLevelText);
		}
		if (selectedFloor == null || tFloorLevel == selectedFloor.getLevel()) {
			floorEdit.setText("");
		} else {
			floorEdit.setText(createFloorNameFromLevel(tFloorLevel));
		}
	}

	// ///////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	public void onDeleteButtonClick(View v) {
		int objectId = -1;
		int actionId = -1;
		switch (v.getId()) {
		case R.id.dataBuildingDeleteButton:
			objectId = R.string.building;
			actionId = Action.deleteBuilding.ordinal();
			break;
		case R.id.dataFloorDeleteButton:
			objectId = R.string.floor;
			actionId = Action.deleteFloor.ordinal();
			break;
		case R.id.dataMeasurePointDeleteOnFloorButton:
			objectId = R.string.measurepoints;
			actionId = Action.deleteFloorMP.ordinal();
			break;
		case R.id.dataMeasurePointDeleteLastButton:
			objectId = R.string.measurepoint;
			actionId = Action.deleteLastMP.ordinal();
			break;
		case R.id.dataMeasurePointDeleteAllButton:
			objectId = R.string.measurepoints;
			actionId = Action.deleteAllMP.ordinal();
			break;
		case R.id.dataScanDeleteLastButton:
			objectId = R.string.scan;
			actionId = Action.deleteLastScan.ordinal();
			break;
		case R.id.dataScanDeleteAllButton:
			objectId = R.string.scans;
			actionId = Action.deleteAllScan.ordinal();
			break;
		default:
			break;
		}

		if (objectId != -1 && actionId != -1) {
			dialogHelper.createDialog(R.string.action_data, String.format(
					getString(R.string.confirm_delete_question),
					getString(objectId)), actionId);
		}
	}

	public void confirmed(int id) {
		Action lastAction = Action.getFromId(id);
		switch (lastAction) {
		case deleteBuilding:
			dataDeleteBuilding();
			break;
		case deleteFloor:
			dataDeleteFloor();
			break;
		case deleteFloorMP:
			dataDeleteFloorMP();
			break;
		case deleteLastMP:
			dataDeleteLastMP();
			break;
		case deleteAllMP:
			dataDeleteAllMP();
			break;
		case deleteLastScan:
			dataDeleteLastScan();
			break;
		case deleteAllScan:
			dataDeleteAllScan();
			break;
		default:
			break;

		}
	}

	public void dataRenameBuilding(View v) {
		String buildingName = buildingEdit.getText().toString().trim();
		if (buildingName.length() >= 3) {
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
		} else {
			makeToast(R.string.error_short_name);
		}
	}

	public void dataDeleteBuilding() {
		if (selectedBuilding != null) {
			if (getStorage().deleteBuilding(selectedBuilding)) {
				buildingHelper.refresh();
				buildingEdit.setText("");
				makeToast(String.format(getString(R.string.success_delete),
						getString(R.string.building)));
			} else {
				makeToast(String.format(getString(R.string.error_delete),
						getString(R.string.building)));
			}
		}
	}

	public void dataSaveFloor(View v) {
		String floorName = floorEdit.getText().toString().trim();
		long floorLevel = Long.valueOf(
				floorLevelEdit.getText().toString().trim()).longValue();
		if (floorName.length() >= 3) {
			if (selectedBuilding != null) {
				if (selectedFloor != null) {
					newFloor = new Floor();
					newFloor.setId(selectedFloor.getId());
					newFloor.setBId(selectedFloor.getBId());
					newFloor.setFile(selectedFloor.getFile());
					newFloor.setLevel(floorLevel);
					newFloor.setName(floorName);
					if (getStorage().changeFloor(newFloor)) {
						buildingHelper.refresh();
						floorEdit.setText("");
						makeToast(String.format(
								getString(R.string.success_save),
								getString(R.string.floor)));
					} else {
						makeToast(String.format(getString(R.string.error_save),
								getString(R.string.floor)));
					}
				}
			}
		} else {
			makeToast(R.string.error_short_name);
		}
	}

	public void dataDeleteFloor() {
		if (selectedBuilding != null) {
			// FloorList = storage.getFloors(selectedBuilding);
			if (selectedFloor != null) {
				if (getStorage().deleteFloor(selectedFloor)) {
					buildingHelper.refresh();
					floorEdit.setText("");
					makeToast(String.format(getString(R.string.success_delete),
							getString(R.string.floor)));
				} else {
					makeToast(String.format(getString(R.string.error_delete),
							getString(R.string.floor)));
				}
			}
		}
	}

	public void dataDeleteFloorMP() {
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

	public void dataDeleteLastMP() {
		List<MeasurePoint> mpList = getStorage().getAllMeasurePoints();
		if (mpList.size() > 0) {
			MeasurePoint deleteMP = mpList.get(mpList.size() - 1);
			if (getStorage().deleteMeasurePoint(deleteMP)) {
				makeToast(String.format(getString(R.string.success_delete),
						getString(R.string.measurepoint)));
			} else {
				makeToast(String.format(getString(R.string.error_delete),
						getString(R.string.measurepoint)));
			}
		}
	}

	public void dataDeleteAllMP() {
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

	public void dataDeleteLastScan() {
		List<Scan> scanList = getStorage().getAllScans();
		if (scanList.size() > 0) {
			Scan sc = scanList.get(scanList.size() - 1);
			if (getStorage().deleteScan(sc)) {
				makeToast(String.format(getString(R.string.success_delete),
						getString(R.string.scan)));
			} else {
				makeToast(String.format(getString(R.string.error_delete),
						getString(R.string.scan)));
			}
		}
	}

	public void dataDeleteAllScan() {
		List<Scan> scanList = getStorage().getAllScans();
		boolean success = true;
		for (Scan sc : scanList) {
			if (getStorage().deleteScan(sc) == false) {
				success = false;
			}
		}
		if (success) {
			makeToast(String.format(getString(R.string.success_delete),
					getString(R.string.scans)));
		} else {
			makeToast(String.format(getString(R.string.error_delete),
					getString(R.string.scans)));
		}
	}

}
