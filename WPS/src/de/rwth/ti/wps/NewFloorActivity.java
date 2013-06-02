package de.rwth.ti.wps;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.rwth.ti.common.ChooseFileDialog;
import de.rwth.ti.common.Constants;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;

public class NewFloorActivity extends SuperActivity {

	private EditText inputBuilding;
	private Spinner buildingSelectSpinner;
	private ArrayAdapter<CharSequence> buildingAdapter;
	private List<Building> buildingList;
	private EditText floorLevelEdit;
	private EditText floorNameEdit;
	private int floorLevel;
	private TextView floorName;
	private byte[] floorFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_floor);
		inputBuilding = (EditText) findViewById(R.id.inputBuilding);
		buildingSelectSpinner = (Spinner) findViewById(R.id.buildingSelectSpinner);
		buildingAdapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		buildingAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		buildingSelectSpinner.setAdapter(buildingAdapter);
		floorLevelEdit = (EditText) findViewById(R.id.floorLevelEdit);
		floorNameEdit = (EditText) findViewById(R.id.floorNameEdit);
		floorName = (TextView) findViewById(R.id.floorPathView);
		floorName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// display choose file dialog
				ChooseFileDialog directoryChooserDialog = new ChooseFileDialog(
						NewFloorActivity.this,
						new ChooseFileDialog.ChosenFileListener() {

							@Override
							public void onChosenFile(String chosenDir) {
								floorName.setText(new File(chosenDir).getName());
								// FIXME check and load the file as future
								// object
//								floorFile = chosenDir;
							}
						});
				// Load directory chooser dialog for initial 'm_chosenDir'
				// directory.
				// The registered callback will be called upon final directory
				// selection.
				directoryChooserDialog.chooseDirectory(Constants.SD_APP_DIR);
			}
		});
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
		floorLevel = 0;
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		refreshBuildingSpinner();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_floor:
			break;
		default:
			return super.onOptionsItemSelected(item);

		}
		return true;
	}

	private void refreshBuildingSpinner() {
		buildingAdapter.clear();
		buildingList = storage.getAllBuildings();
		for (Building b : buildingList) {
			buildingAdapter.add(b.getName());
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
			String fName = createFloorNameFromLevel(tFloorLevel);
			floorLevel = tFloorLevel;
			floorNameEdit.setText(fName);
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

	public void createBuilding(View view) {
		String bName = inputBuilding.getText().toString();
		Building b = storage.createBuilding(bName);
		if (b == null) {
			// XXX handle error
			return;
		}
		inputBuilding.setText("");
		refreshBuildingSpinner();
		// set the new building as selected
		int index = buildingAdapter.getPosition(bName);
		if (index == -1) {
			// XXX handle error
			return;
		}
		buildingSelectSpinner.setSelection(index);
	}

	public void editBuilding(View view) {
		// TODO open alert to edit building name
	}

	public void deleteBuilding(View view) {
		// TODO open alert yes/no question
	}

	public void createFloor(View view) {
		int pos = buildingSelectSpinner.getSelectedItemPosition();
		if (pos == Spinner.INVALID_POSITION) {
			// XXX handle error
			return;
		}
		Building b = buildingList.get(pos);
		Floor f = storage.createFloor(b, floorName.getText().toString(),
				floorFile, floorLevel, cmgr.getAzimut());
		if (f == null) {
			// XXX handle error
			return;
		}
		// TODO floor successfull created
	}

	public void editFloor(View view) {
		// TODO open alert to edit floor name
	}

	public void deleteFloor(View view) {
		// TODO open alert yes/no question
	}

}
