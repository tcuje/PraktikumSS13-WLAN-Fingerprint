package de.rwth.ti.wps;

import java.io.ByteArrayInputStream;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.common.CompassManager;
import de.rwth.ti.common.CompassManager.OnCustomEventListener;
import de.rwth.ti.common.IPMapView;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.loc.Location;
import de.rwth.ti.loc.LocationResult;

public class MeasureActivity extends SuperActivity implements
		OnItemSelectedListener {

	private List<Building> buildingList;
	private ArrayAdapter<CharSequence> buildingAdapter;
	private Spinner buildingSpinner;
	private Building buildingSelected;
	private List<Floor> floorList;
	private ArrayAdapter<CharSequence> floorAdapter;
	private Spinner floorSpinner;
	private Floor floorSelected;
	private IPMapView mapView;
	private CompassManager.Direction direction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measure);

		buildingAdapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		buildingSpinner = (Spinner) findViewById(R.id.buildingSelectSpinner);
		buildingSpinner.setAdapter(buildingAdapter);
		buildingSpinner.setOnItemSelectedListener(this);

		floorAdapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		floorSpinner = (Spinner) findViewById(R.id.floorSelectSpinner);
		floorSpinner.setAdapter(floorAdapter);
		floorSpinner.setOnItemSelectedListener(this);

		mapView = (IPMapView) findViewById(R.id.map_view);
		mapView.setMeasureMode(true);

		direction = CompassManager.Direction.NORTH;
		((TextView) findViewById(R.id.direction_text_view))
				.setText("Nach Norden aussrichten");
		cmgr.setCustomEventListener(new OnCustomEventListener() {
			public void onEvent() {
				showArrow();
			}
		});
	}

	protected void showArrow() {
		((TextView) findViewById(R.id.direction_text_view))
				.setText("Nach Norden aussrichten" + cmgr.getAzimut());
	}

	@Override
	public void onStart() {
		super.onStart();
		buildingAdapter.clear();
		buildingList = storage.getAllBuildings();
		for (Building b : buildingList) {
			buildingAdapter.add(b.getName());
		}
		if (buildingList.size() == 0) {
			buildingSelected = null;
		} else {
			buildingSelected = buildingList.get(0);
		}
	}
	//FIXME wieder entfernen
	public void localisation(View view) {
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiLock wl = wifi.createWifiLock("WPS");
		List<ScanResult> results = wifi.getScanResults();
		Location myLoc = new Location(storage);
		LocationResult myLocRes = myLoc.getLocation(results, 0, 0);
		mapView.setPoint((float)myLocRes.getX() , (float)myLocRes.getY());
	}
	
	public void changeMeasureMode(View  view){	
			mapView.setMeasureMode(!(mapView.getMeasureMode()));
	}

	public void measure(View view) {
		if (view.getId() == R.id.measure_button) {
			// check if building/floor is selected
			if (buildingSelected == null) {
				Toast.makeText(this, R.string.error_empty_input,
						Toast.LENGTH_LONG).show();
				return;
			}
			if (floorSelected == null) {
				Toast.makeText(this, R.string.error_empty_input,
						Toast.LENGTH_LONG).show();
				return;
			}
			float[] p = mapView.getMeasurePoint();
			if (p == null) {
				Toast.makeText(this, R.string.error_no_measure_point,
						Toast.LENGTH_LONG).show();
				return;
			}
			MeasurePoint mp = storage.createMeasurePoint(floorSelected, p[0], p[1]);
			
			boolean check = scm.startSingleScan(mp);
			if (check == false) {
				Toast.makeText(this, R.string.error_scanning, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, R.string.success_scanning, Toast.LENGTH_LONG)
				.show();
				direction = CompassManager.Direction.values()[(direction
						.ordinal() + 1) % 4];
				switch (direction) {
				case NORTH:
					((TextView) findViewById(R.id.direction_text_view))
							.setText("Nach Norden aussrichten");
					break;
				case EAST:
					((TextView) findViewById(R.id.direction_text_view))
							.setText("Nach Osten aussrichten");
					break;
				case SOUTH:
					((TextView) findViewById(R.id.direction_text_view))
							.setText("Nach Süden aussrichten");
					break;
				case WEST:
					((TextView) findViewById(R.id.direction_text_view))
							.setText("Nach Westen aussrichten");
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (parent == buildingSpinner) {
			buildingSelected = buildingList.get(pos);
			// update floor spinner
			floorList = storage.getFloors(buildingSelected);
			floorAdapter.clear();
			for (Floor f : floorList) {
				floorAdapter.add(f.getName());
			}
			if (floorList.size() == 0) {
				floorSelected = null;
			} else {
				onItemSelected(floorSpinner, view, 0, id);
			}
		} else if (parent == floorSpinner) {
			floorSelected = floorList.get(pos);
			// update map view
			byte[] file = floorSelected.getFile();
			if (file != null) {
				ByteArrayInputStream bin = new ByteArrayInputStream(file);
				mapView.newMap(bin);
			} else {
				Toast.makeText(this, R.string.error_no_floor_file,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent == buildingSpinner) {
			// clear floor spinner
			floorList.clear();
			floorAdapter.clear();
			buildingSelected = null;
			floorSelected = null;
		} else if (parent == floorSpinner) {
			buildingSelected = null;
			floorSelected = null;
			// TODO clear map view
		}
	}

}
