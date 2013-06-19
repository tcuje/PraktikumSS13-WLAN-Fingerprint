package de.rwth.ti.wps;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.spinner.BuildingSpinnerHelper;
import de.rwth.ti.spinner.FloorSpinnerHelper;
import de.rwth.ti.spinner.OnBuildingChangedListener;
import de.rwth.ti.spinner.OnFloorChangedListener;

public class DataActivity extends SuperActivity implements
	OnBuildingChangedListener, OnFloorChangedListener {

	Building selectedBuilding;
	Floor selectedFloor;
	
	TextView buildingHeader;
	TextView floorHeader;
	TextView measurePointHeader;
	LinearLayout buildingLayout;
	LinearLayout floorLayout;
	LinearLayout measurePointLayout;
	BuildingSpinnerHelper buildingHelper;
	FloorSpinnerHelper floorHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false, false);
		setContentView(R.layout.activity_data);
		// Show the Up button in the action bar.
		
		selectedBuilding = null;
		selectedFloor = null;
		
		buildingHeader = (TextView) findViewById(R.id.dataBuildingHeader);
		buildingHeader.setBackgroundColor(Color.LTGRAY);
		floorHeader = (TextView) findViewById(R.id.dataFloorHeader);
		floorHeader.setBackgroundColor(Color.LTGRAY);
		measurePointHeader = (TextView) findViewById(R.id.dataMeasurePointHeader);
		measurePointHeader.setBackgroundColor(Color.LTGRAY);
		
		buildingLayout = (LinearLayout) findViewById(R.id.dataBuildingLayout);
		floorLayout = (LinearLayout) findViewById(R.id.dataFloorLayout);
		measurePointLayout = (LinearLayout) findViewById(R.id.dataMeasurePointLayout);

		
		buildingHelper = BuildingSpinnerHelper.createInstance(this, this, storage, (Spinner) findViewById(R.id.dataBuildingBuildingSpinner));
		buildingHelper.addSpinner((Spinner) findViewById(R.id.dataFloorBuildingSpinner));
		buildingHelper.addSpinner((Spinner) findViewById(R.id.dataMeasurePointBuildingSpinner));
		floorHelper = FloorSpinnerHelper.createInstance(this, this, storage, (Spinner) findViewById(R.id.dataFloorFloorSpinner));
		floorHelper.addSpinner((Spinner) findViewById(R.id.dataMeasurePointFloorSpinner));
		buildingHelper.addListener(floorHelper);
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
			
		}
	}
}
