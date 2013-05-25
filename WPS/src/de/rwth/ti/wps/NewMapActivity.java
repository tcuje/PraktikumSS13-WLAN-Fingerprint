package de.rwth.ti.wps;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.rwth.ti.db.Building;

public class NewMapActivity extends SuperActivity
	implements OnItemSelectedListener {
	
	private static final int CHOOSE_MAP_FILE = 1;
	
	private Spinner buildingSelectSpinner;
	//private Spinner floorSelectSpinner;
	private EditText floorLevelEdit;
	private EditText floorNameEdit;
	//private TextView mapPathView;
	
	List<Building> buildingList;
	//List<Floor> floorList;
	
	Building selectedBuilding;
	//Floor selectedFloor;
	int floorLevel;
	String floorName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_map);
		
		// Show the Up button in the action bar.
		//setupActionBar();
		
		buildingSelectSpinner = (Spinner) findViewById(R.id.buildingSelectSpinner);
		buildingSelectSpinner.setOnItemSelectedListener(this);
		//floorSelectSpinner = (Spinner) findViewById(R.id.floorSelectSpinner);
		//floorSelectSpinner.setOnItemSelectedListener(this);
		floorLevelEdit = (EditText) findViewById(R.id.floorLevelEdit);
		floorNameEdit = (EditText) findViewById(R.id.floorNameEdit);
		//mapPathView = (TextView) findViewById(R.id.mapPathView);
		
		TextWatcher textWatch = new TextWatcher() {
			public void afterTextChanged(Editable s) {
				onFloorLevelChanged();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {};
			public void onTextChanged(CharSequence s, int start, int count, int after) {};
		};
		
		floorLevelEdit.addTextChangedListener(textWatch);
		
		selectedBuilding = null;
		//selectedFloor = null;
		floorLevel = 0;
		floorName = "";
	}
	
	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();

		refreshBuildingSpinner();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 *
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_new_map:		//intent = new Intent(this, NewMapActivity.class);
											break;
			//case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//								NavUtils.navigateUpFromSameTask(this);
			//								break;
			default:
											return super.onOptionsItemSelected(item);
			
		}
		
		return true;
	}
	
	private void refreshBuildingSpinner() {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		buildingList = storage.getAllBuildings();
		for (Building b : buildingList) {
			adapter.add(b.getName());
		}
		
		buildingSelectSpinner.setAdapter(adapter);
	}
	
	/*private void refreshFloorSpinner() {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		if (selectedBuilding != null)
		{
			floorList = storage.getFloors(selectedBuilding);
		
			for (Floor f : floorList) {
				adapter.add(f.getName());
			}
		}
		
		floorSelectSpinner.setAdapter(adapter);
	}*/
	
	private void onFloorLevelChanged() {
		//floorNameEdit.setText(floorLevelEdit.getText());
		String tFloorName = floorNameEdit.getText().toString().trim();
		int tFloorLevel;
		String tFloorLevelText = floorLevelEdit.getText().toString().trim();
		if (tFloorLevelText.equals("") || tFloorLevelText.equals("-")) {
			tFloorLevel = 0;
		}
		else {
			tFloorLevel = Integer.parseInt(tFloorLevelText);
		}
		
		if (tFloorName.equals("") || tFloorName.equals(createFloorNameFromLevel(floorLevel))) {
			floorName = createFloorNameFromLevel(tFloorLevel);
			floorLevel = tFloorLevel;
			floorNameEdit.setText(floorName);
		}
	}
	
	private String createFloorNameFromLevel(int level) {
		String tString = "";
		if (level < 0) {
			tString = String.valueOf((-1) * level) + ". " + getString(R.string.floor_basement);
		}
		else if (level > 0) {
			tString = String.valueOf(level) + ". " + getString(R.string.floor_upper);
		}
		else {
			tString = getString(R.string.floor_ground);
		}
		return tString;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		if (parent == buildingSelectSpinner)
		{
			//selectedBuilding = buildingList.get(pos);
			//refreshFloorSpinner();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public void createMap(View view) {
		
	}
	
	public void chooseMapFile(View view) {
		Intent chooseMapIntent = new Intent();
		chooseMapIntent.setType("image/*");
		chooseMapIntent.setAction(Intent.ACTION_GET_CONTENT);
		//startActivityForResult(Intent.createChooser(chooseMapIntent, "@string/activity_new_map_choose_map"), CHOOSE_MAP_FILE);
		startActivityForResult(chooseMapIntent, CHOOSE_MAP_FILE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_MAP_FILE) {
			if (resultCode == RESULT_OK) {
				Uri mapUri = data.getData();
				
				if (mapUri != null) {
		            //User had pick an image.
		            Cursor cursor = getContentResolver().query(mapUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
		            cursor.moveToFirst();

		            //Link to the image
		            final String imageFilePath = cursor.getString(0);
		            cursor.close();
		            
		            //ImageView mapView = (ImageView) findViewById(R.id.mapView);
		            //mapView.setImageURI(mapUri);
		            //TextView textView = (TextView) findViewById(R.id.mapPath);
		            //textView.setText(imageFilePath);
		        }
			}
		}
	}
}
