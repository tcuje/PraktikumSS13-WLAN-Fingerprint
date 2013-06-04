package de.rwth.ti.wps;

import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;

public class NewFloorActivity extends SuperActivity implements
		OnItemSelectedListener {

	private EditText createBuildingEdit;
	private Spinner buildingSelectSpinner;
	private EditText floorLevelEdit;
	private EditText floorNameEdit;
	private EditText northEdit;

	List<Building> buildingList;

	Building selectedBuilding;
	int floorLevel;
	String floorName;
	int north;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_floor);

		// Show the Up button in the action bar.
		// setupActionBar();
		createBuildingEdit = (EditText) findViewById(R.id.createBuildingEdit);
		buildingSelectSpinner = (Spinner) findViewById(R.id.buildingSelectSpinner);
		buildingSelectSpinner.setOnItemSelectedListener(this);
		// floorSelectSpinner = (Spinner) findViewById(R.id.floorSelectSpinner);
		// floorSelectSpinner.setOnItemSelectedListener(this);
		floorLevelEdit = (EditText) findViewById(R.id.floorLevelEdit);
		floorNameEdit = (EditText) findViewById(R.id.floorNameEdit);
		northEdit = (EditText) findViewById(R.id.northEdit);
		// mapPathView = (TextView) findViewById(R.id.mapPathView);

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

		selectedBuilding = null;
		// selectedFloor = null;
		floorLevel = 0;
		floorName = "";
		north = -1;
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
	 * private void setupActionBar() {
	 * 
	 * getActionBar().setDisplayHomeAsUpEnabled(true);
	 * 
	 * }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public void createBuilding(View view) {
		String tBuildingName = createBuildingEdit.getText().toString().trim();
		String message;

		// Name eingegeben
		if (tBuildingName.length() != 0) {
			Building b = storage.createBuilding(tBuildingName);

			// Gebäude konnte erfolgreich erstellt werden?
			if (b != null) {
				message = getString(R.string.success_create_building);

				// Löscht den eingegeben Text
				createBuildingEdit.setText("");
				// Lädt die Liste der Gebäude neu
				refreshBuildingSpinner();
				// Wählt das letzte Element aus, also den neuen Eintrag
				buildingSelectSpinner.setSelection(buildingList.size() - 1);
			} else {
				message = getString(R.string.error_create_building);
			}

		} else {
			message = getString(R.string.error_empty_input);
		}
		// User message
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private void refreshBuildingSpinner() {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		buildingList = storage.getAllBuildings();
		for (Building b : buildingList) {
			adapter.add(b.getName());
		}
		buildingSelectSpinner.setAdapter(adapter);
	}

	/*
	 * private void refreshFloorSpinner() { ArrayAdapter<CharSequence> adapter =
	 * new ArrayAdapter<CharSequence>(this,
	 * android.R.layout.simple_spinner_item);
	 * adapter.setDropDownViewResource(android
	 * .R.layout.simple_spinner_dropdown_item);
	 * 
	 * if (selectedBuilding != null) { floorList =
	 * storage.getFloors(selectedBuilding);
	 * 
	 * for (Floor f : floorList) { adapter.add(f.getName()); } }
	 * 
	 * floorSelectSpinner.setAdapter(adapter); }
	 */

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (parent == buildingSelectSpinner) {
			selectedBuilding = buildingList.get(pos);
			// refreshFloorSpinner();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent == buildingSelectSpinner) {
			selectedBuilding = null;
			// refreshFloorSpinner();
		}
	}

	private void onFloorLevelChanged() {
		// floorNameEdit.setText(floorLevelEdit.getText());
		String tFloorName = floorNameEdit.getText().toString().trim();
		int tFloorLevel;
		String tFloorLevelText = floorLevelEdit.getText().toString().trim();
		if (tFloorLevelText.equals("") || tFloorLevelText.equals("-")) {
			tFloorLevel = 0;
		} else {
			tFloorLevel = Integer.parseInt(tFloorLevelText);
		}

		if (tFloorName.equals("")
				|| tFloorName.equals(createFloorNameFromLevel(floorLevel))) {
			floorName = createFloorNameFromLevel(tFloorLevel);
			floorLevel = tFloorLevel;
			floorNameEdit.setText(floorName);
		}
	}

	private String createFloorNameFromLevel(int level) {
		String tString = "";
		if (level < 0) {
			tString = String.valueOf((-1) * level) + ". "
					+ getString(R.string.floor_basement);
		} else if (level > 0) {
			tString = String.valueOf(level) + ". "
					+ getString(R.string.floor_upper);
		} else {
			tString = getString(R.string.floor_ground);
		}
		return tString;
	}

	public void createMap(View view) {
		String message;

		floorName = floorNameEdit.getText().toString().trim();
		String tFloorLevelText = floorLevelEdit.getText().toString().trim();
		String tNorthText = northEdit.getText().toString().trim();

		// Kontrolliert, ob alle Inputs vernünftig gefüllt sind
		if (floorName.length() != 0 && tFloorLevelText.length() != 0
				&& !tFloorLevelText.equals("-") && tNorthText.length() != 0) {
			north = Integer.parseInt(tNorthText);

			// Überhaupt ein Gebäude vorhanden <=> Gebäude ausgewählt
			if (!buildingList.isEmpty()) {
				// Kartendatei ausgewählt
				// TODO: Bedingung hinzufügen, else Teil mit Nachricht
				if (true) {
					// TODO: Karten-ByteArray einfügen
					Floor f = storage.createFloor(selectedBuilding, floorName,
							null, floorLevel, north);

					// Floor erfolgreich erstellt
					if (f != null) {
						message = getString(R.string.success_create_floor);

						// Eingaben löschen
						buildingSelectSpinner.setSelection(0, true);
						floorLevelEdit.setText("");
						floorNameEdit.setText("");
						northEdit.setText("");
					} else {
						message = getString(R.string.error_create_floor);
					}
				}
			} else {
				message = getString(R.string.error_no_building);
			}
		} else {
			message = getString(R.string.error_empty_input);
		}
		// User message
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

}
