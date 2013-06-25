package de.rwth.ti.wps;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.common.ChooseFileDialog;
import de.rwth.ti.common.Constants;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;

public class NewFloorActivity extends SuperActivity implements
		OnItemSelectedListener {

	private EditText createBuildingEdit;
	private List<Building> buildingList;
	private ArrayAdapter<CharSequence> buildingAdapter;
	private Spinner buildingSpinner;
	private Building buildingSelected;

	private EditText floorLevelEdit;
	private EditText floorNameEdit;
	private EditText northEdit;
	private TextView floorFilenameView;
	private int floorLevel;
	private String floorName;
	private int north;
	private byte[] floorFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_floor);

		createBuildingEdit = (EditText) findViewById(R.id.createBuildingEdit);
		buildingAdapter = new ArrayAdapter<CharSequence>(this,
				R.layout.spinner_item);
		buildingSpinner = (Spinner) findViewById(R.id.buildingSelectSpinner);
		buildingSpinner.setAdapter(buildingAdapter);
		buildingSpinner.setOnItemSelectedListener(this);

		floorLevelEdit = (EditText) findViewById(R.id.floorLevelEdit);
		floorNameEdit = (EditText) findViewById(R.id.floorNameEdit);
		northEdit = (EditText) findViewById(R.id.northEdit);
		// TODO make this visible
		northEdit.setVisibility(View.GONE);
		floorFilenameView = (TextView) findViewById(R.id.filePathEdit);

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

		buildingSelected = null;
		floorLevel = 0;
		floorName = null;
		north = -1;
		floorFile = null;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		refreshBuildingSpinner();
	}

	public void createBuilding(View view) {
		String tBuildingName = createBuildingEdit.getText().toString().trim();
		String message = null;
		// check name
		if (tBuildingName.length() != 0) {
			Building b = getStorage().createBuilding(tBuildingName);
			// Gebäude konnte erfolgreich erstellt werden?
			if (b != null) {
				message = getString(R.string.success_create_building);
				// Löscht den eingegeben Text
				createBuildingEdit.setText("");
				// Lädt die Liste der Gebäude neu
				refreshBuildingSpinner();
				// Wählt das letzte Element aus, also den neuen Eintrag
				buildingSpinner.setSelection(buildingList.size() - 1);
			} else {
				message = getString(R.string.error_create_building);
			}
		} else {
			message = getString(R.string.error_empty_input);
		}
		if (message != null) {
			// User message
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	private void refreshBuildingSpinner() {
		buildingAdapter.clear();
		buildingList = getStorage().getAllBuildings();
		for (Building b : buildingList) {
			buildingAdapter.add(b.getName());
		}
		if (buildingList.size() == 0) {
			buildingSelected = null;
		} else {
			buildingSelected = buildingList.get(0);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (parent == buildingSpinner) {
			buildingSelected = buildingList.get(pos);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent == buildingSpinner) {
			buildingSelected = null;
		}
	}

	private void onFloorLevelChanged() {
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

	public void createFloor(View view) {
		String message = null;
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
				if (floorFile != null) {
					Floor f = getStorage().createFloor(buildingSelected,
							floorName, floorFile, floorLevel, north);
					// Floor erfolgreich erstellt
					if (f != null) {
						message = getString(R.string.success_create_floor);
						// Eingaben löschen
						floorLevelEdit.setText("");
						floorLevelEdit.requestFocus();
						floorNameEdit.setText("");
						northEdit.setText("");
						floorFilenameView.setText("");
						floorLevel = 0;
						floorName = null;
						north = -1;
						floorFile = null;
					} else {
						message = getString(R.string.error_create_floor);
					}
				} else {
					message = getString(R.string.error_no_floor_file);
				}
			} else {
				message = getString(R.string.error_no_building);
			}
		} else {
			message = getString(R.string.error_empty_input);
		}
		if (message != null) {
			// User message
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	public void chooseFile(View view) {
		// display choose file dialog
		ChooseFileDialog directoryChooserDialog = new ChooseFileDialog(
				NewFloorActivity.this,
				new ChooseFileDialog.ChosenFileListener() {
					@Override
					public void onChosenFile(String chosenFile) {
						File f = new File(chosenFile);
						if (f.exists() && f.isFile()) {
							// TODO show file loading state in progress bar
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							FileInputStream fis = null;
							try {
								fis = new FileInputStream(f);
							} catch (FileNotFoundException e) {
								// XXX handle error
							}
							byte[] buffer = new byte[Constants.FILE_BUFFER_SIZE];
							try {
								for (int len = fis.read(buffer); len > 0; len = fis
										.read(buffer)) {
									baos.write(buffer);
								}
								fis.close();
							} catch (IOException ex) {
								// XXX handle error
							}
							floorFilenameView.setText(new File(chosenFile)
									.getName());
							floorFile = baos.toByteArray();
							try {
								baos.close();
							} catch (IOException ex) {
								// XXX handle error
							}
						}
					}
				}, Constants.MAP_SUFFIX);
		directoryChooserDialog.chooseDirectory(Constants.SD_APP_DIR);
	}
}
