package de.rwth.ti.wps;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.layouthelper.BuildingSpinnerHelper;
import de.rwth.ti.layouthelper.CustomTabHelper;
import de.rwth.ti.layouthelper.FloorSpinnerHelper;
import de.rwth.ti.layouthelper.OnBuildingChangedListener;
import de.rwth.ti.layouthelper.OnFloorChangedListener;

public class DataActivity extends SuperActivity implements
	OnBuildingChangedListener, OnFloorChangedListener, OnClickListener {

	Building selectedBuilding;
	Building newBuilding;
	Floor selectedFloor;
	Floor newFloor;
	List<MeasurePoint> MPList;
	List<Floor> FloorList;
	
		
	TextView buildingHeader;
	TextView floorHeader;
	TextView measurePointHeader;
	LinearLayout buildingLayout;
	LinearLayout floorLayout;
	LinearLayout measurePointLayout;
	
	CustomTabHelper tabHelper;
	BuildingSpinnerHelper buildingHelper;
	FloorSpinnerHelper floorHelper;
	Spinner buildingSpinner;
	
	Button BuildingRenameButton;
	Button BuildingDeleteButton;
	Button FloorSaveButton;
	Button FloorDeleteButton;
	Button MeasurePointDeleteOnFloorButton;
	Button MeasurePointDeleteLastButton;
	Button MeasurePointDeleteAllButton;
	
	EditText buildingEdit;
	EditText floorEdit;
	EditText floorLevelEdit;

	
	
	
	
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
		
		buildingLayout = (LinearLayout) findViewById(R.id.dataBuildingLayout);
		floorLayout = (LinearLayout) findViewById(R.id.dataFloorLayout);
		measurePointLayout = (LinearLayout) findViewById(R.id.dataMeasurePointLayout);

		tabHelper = CustomTabHelper.createInstance(buildingHeader, buildingLayout);
		tabHelper.addTabItem(floorHeader, floorLayout);
		tabHelper.addTabItem(measurePointHeader, measurePointLayout);
		
		buildingHelper = BuildingSpinnerHelper.createInstance(this, this, storage, (Spinner) findViewById(R.id.dataBuildingBuildingSpinner));
		buildingHelper.addSpinner((Spinner) findViewById(R.id.dataFloorBuildingSpinner));
		buildingHelper.addSpinner((Spinner) findViewById(R.id.dataMeasurePointBuildingSpinner));
		floorHelper = FloorSpinnerHelper.createInstance(this, this, storage, (Spinner) findViewById(R.id.dataFloorFloorSpinner));
		floorHelper.addSpinner((Spinner) findViewById(R.id.dataMeasurePointFloorSpinner));
		buildingHelper.addListener(floorHelper);
		
		BuildingDeleteButton = (Button) findViewById(R.id.dataBuildingDeleteButton);
		buildingEdit = (EditText) findViewById(R.id.dataBuildingRenameEdit);
		floorEdit = (EditText) findViewById(R.id.dataFloorRenameEdit);
		floorLevelEdit = (EditText) findViewById(R.id.dataFloorFloorLevelEdit);
		
				
		BuildingRenameButton= (Button) findViewById(R.id.dataBuildingRenameButton);
		FloorSaveButton= (Button) findViewById(R.id.dataFloorSaveButton);
		FloorDeleteButton= (Button) findViewById(R.id.dataFloorDeleteButton);
		MeasurePointDeleteOnFloorButton= (Button) findViewById(R.id.dataMeasurePointDeleteOnFloorButton);
		MeasurePointDeleteLastButton= (Button) findViewById(R.id.dataMeasurePointDeleteLastButton);
		MeasurePointDeleteAllButton= (Button) findViewById(R.id.dataMeasurePointDeleteAllButton);
		

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
		
		if (selectedBuilding.getName().equals("Seminargebäude")) {
			buildingLayout.setVisibility(View.VISIBLE);
			floorLayout.setVisibility(View.GONE);
		}
		else {
			buildingLayout.setVisibility(View.GONE);
			floorLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void floorChanged(FloorSpinnerHelper helper) {
		selectedFloor = helper.getSelectedFloor();
		if (selectedFloor != null) {
			floorLevelEdit.setText(String.valueOf(selectedFloor.getLevel()));
		}
	}
	
	
	/////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	
	public void dataDeleteBuilding (View v){
	
		if(selectedBuilding != null){
			storage.deleteBuilding(selectedBuilding);
		buildingHelper.refresh();
		}
	}
	
	public void dataRenameBuilding (View v){
		String BuildingName = buildingEdit.getText().toString().trim();
		newBuilding=new Building();
		newBuilding.setName(BuildingName);
		newBuilding.setId(selectedBuilding.getId());
		storage.changeBuilding(newBuilding);
		buildingHelper.refresh();

	}
	
	public void dataDeleteAllMP(View v){
		MPList=storage.getAllMeasurePoints();
		for (MeasurePoint MP : MPList)
		storage.deleteMea1surePoint(MP);
		buildingHelper.refresh();

	}
	
	public void dataDeleteFloorMP (View v){
		if (selectedBuilding!=null){
			FloorList=storage.getFloors(selectedBuilding);
			if (selectedFloor!=null) {
				MPList=storage.getMeasurePoints(selectedFloor);
				for (MeasurePoint MP : MPList)
					storage.deleteMea1surePoint(MP);
				buildingHelper.refresh();
				floorHelper.refresh();

			}
		}
		
		
	}

	public void dataDeleteLastMP(View v){
		MPList=storage.getAllMeasurePoints();
		MeasurePoint deleteMP=MPList.get(MPList.size() - 1);
		storage.deleteMea1surePoint(deleteMP);
		buildingHelper.refresh();

	}
	
	
	public void dataDeleteFloor (View v){
		if (selectedBuilding!=null){
			FloorList=storage.getFloors(selectedBuilding);
			if (selectedFloor!=null) {
					storage.deleteFloor(selectedFloor);
					buildingHelper.refresh();
					floorHelper.refresh();


			}
		}
	}
		
	public void dataFloorSave (View v){
		String FloorName = floorEdit.getText().toString().trim();
		long FloorLevel =Long.valueOf( floorLevelEdit.getText().toString().trim()).longValue();;
		if (selectedBuilding!=null){
			if (selectedFloor!=null) {
				newFloor= new Floor();
				newFloor.setId(selectedFloor.getId());
				newFloor.setLevel(FloorLevel);
				newFloor.setName(FloorName);
				storage.changeFloor(newFloor);
				buildingHelper.refresh();
				floorHelper.refresh();

				
			}			
		}
	}
	
	@Override
	public void onClick(View v) {


	}
}
