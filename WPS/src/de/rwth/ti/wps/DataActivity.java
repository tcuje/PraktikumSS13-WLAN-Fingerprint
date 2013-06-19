package de.rwth.ti.wps;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.layouthelper.BuildingSpinnerHelper;
import de.rwth.ti.layouthelper.CustomTabHelper;
import de.rwth.ti.layouthelper.FloorSpinnerHelper;
import de.rwth.ti.layouthelper.OnBuildingChangedListener;
import de.rwth.ti.layouthelper.OnFloorChangedListener;

public class DataActivity extends SuperActivity implements
	OnBuildingChangedListener, OnFloorChangedListener, OnClickListener {

	Building selectedBuilding;
	Floor selectedFloor;
	
	TextView buildingHeader;
	TextView floorHeader;
	TextView measurePointHeader;
	LinearLayout buildingLayout;
	LinearLayout floorLayout;
	LinearLayout measurePointLayout;
	
	CustomTabHelper tabHelper;
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
		
		
		// init ClickListeners
		findViewById(R.id.dataBuildingRenameButton).setOnClickListener(this);
		findViewById(R.id.dataBuildingDeleteButton).setOnClickListener(this);
		// TODO andere Buttons hinzufügen
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

	@Override
	public void onClick(View v) {
		// TODO Funktionen einfügen
		switch(v.getId()) {
		case R.id.dataBuildingRenameButton:
			// TODO Gebäude umbennen
			break;
		case R.id.dataBuildingDeleteButton:
			// TODO Gebäude löschen
			break;
		// ...
		}
		
	}
}
