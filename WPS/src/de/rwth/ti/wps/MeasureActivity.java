package de.rwth.ti.wps;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.wps.CompassManager.OnCustomEventListener;

public class MeasureActivity extends SuperActivity{
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private IPMapView mMapView;
	private Building mBuilding;
	private Floor mFloor;
	private CompassManager.Direction mDirection;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measure);
		
		mMapView = (IPMapView) findViewById(R.id.map_view);
		
		AssetManager assetManager = this.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("seminargebaude2.svg");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
		mMapView.newMap(inputStream);
		mMapView.setMeasureMode(true);
		mDirection = CompassManager.Direction.NORTH;
		((TextView)findViewById(R.id.direction_text_view)).setText("Nach Norden aussrichten");
		cmgr.setCustomEventListener(new OnCustomEventListener(){
		    public void onEvent(){
		    	showArrow();
		    }
		    });
	}

	protected void showArrow() {
		((TextView)findViewById(R.id.direction_text_view)).setText("Nach Norden aussrichten" + cmgr.getAzimut());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//FIXME gebäude aus spinner verwenden
		mBuilding = storage.createBuilding("Haus "
 				 + (storage.countBuildings() + 1));
		mFloor = storage.createFloor(mBuilding, "Ebene "
				 + (storage.countFloors() + 1), null,
						(storage.countFloors() + 1), 15);	
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		/*
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
		*/
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		/*
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
			case R.id.action_measure:		//intent = new Intent(this, MeasureActivity.class);
											break;
			//case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
											//NavUtils.navigateUpFromSameTask(this);
											//break;
			default:						
											return super.onOptionsItemSelected(item);
		}
	
		return true;
	}

	public void measure(View view){
		
		if (view.getId() == R.id.measure_button) {
			float[] p = mMapView.getMeasurPoint();
			MeasurePoint mp = storage.createMeasurePoint(mFloor, p[0], p[1]);
			boolean check = scm.startSingleScan(mp);
			if (check == false) {
				Toast.makeText(this, "Fehler beim Scanstart", Toast.LENGTH_LONG)
						.show();
			}else{
				mDirection = CompassManager.Direction.values()[(mDirection.ordinal() + 1)%4];
				switch (mDirection) {
				case NORTH:
					((TextView)findViewById(R.id.direction_text_view)).setText("Nach Norden aussrichten");
					break;
				case EAST:
					((TextView)findViewById(R.id.direction_text_view)).setText("Nach Osten aussrichten");
					break;
				case SOUTH:
					((TextView)findViewById(R.id.direction_text_view)).setText("Nach Süden aussrichten");
					break;
				case WEST:
					((TextView)findViewById(R.id.direction_text_view)).setText("Nach Westen aussrichten");
					break;
				default:
					break;
				}
			}
		}
	}
	
	/*
	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		//TextView textView = (TextView) findViewById(R.id.testView);
		//textView.setText("Geb�ude " + Integer.toString(position));
		
		return true;
	}*/
	
}

