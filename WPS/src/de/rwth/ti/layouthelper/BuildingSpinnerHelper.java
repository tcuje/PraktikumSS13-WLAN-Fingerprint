package de.rwth.ti.layouthelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.StorageHandler;
import de.rwth.ti.wps.R;

public class BuildingSpinnerHelper implements OnItemSelectedListener {

	private List<OnBuildingChangedListener> listenerList;

	private Activity activity;
	private StorageHandler storage;
	private List<Spinner> spinnerList;
	private List<Building> buildingList;
	// private ArrayAdapter<String> adapter;
	private SpinnerAdapter adapter;
	private Building selectedBuilding;

	private BuildingSpinnerHelper(Activity activity, StorageHandler storage,
			List<Spinner> spinner) {
		this.activity = activity;
		this.storage = storage;
		spinnerList = spinner;
		listenerList = new ArrayList<OnBuildingChangedListener>();

		// adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item,
		// R.id.spinner_item_text);

		// adapter = new ArrayAdapter<String>(activity,
		adapter = new SpinnerAdapter(activity, R.layout.spinner_item,
				R.id.spinner_item_text);
		// adapter.setDropDownViewResource(R.layout.spinner_item_dropdown_test);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		for (Spinner tSpinner : spinnerList) {
			tSpinner.setAdapter(adapter);
			tSpinner.setOnItemSelectedListener(this);
			tSpinner.setEmptyView(activity
					.findViewById(R.id.spinner_view_empty));
		}
	}

	public static BuildingSpinnerHelper createInstance(Activity activity,
			OnBuildingChangedListener listener, StorageHandler storage,
			Spinner spinner) {
		ArrayList<Spinner> spinnerList = new ArrayList<Spinner>();
		spinnerList.add(spinner);

		BuildingSpinnerHelper helper = new BuildingSpinnerHelper(activity,
				storage, spinnerList);
		helper.addListener(listener);
		return helper;
	}

	public static BuildingSpinnerHelper createInstance(Activity activity,
			OnBuildingChangedListener listener, StorageHandler storage,
			List<Spinner> spinnerList) {
		BuildingSpinnerHelper helper = new BuildingSpinnerHelper(activity,
				storage, spinnerList);
		helper.addListener(listener);
		return helper;
	}

	public boolean addListener(OnBuildingChangedListener listener) {
		if (!listenerList.contains(listener)) {
			return listenerList.add(listener);
		}
		return false;
	}

	public boolean removeListener(OnBuildingChangedListener listener) {
		if (listenerList.contains(listener)) {
			return listenerList.remove(listener);
		}
		return false;
	}

	public boolean addSpinner(Spinner spinner) {
		if (!spinnerList.contains(spinner)) {
			if (spinnerList.add(spinner)) {
				spinner.setAdapter(adapter);
				spinner.setOnItemSelectedListener(this);
				spinner.setEmptyView(activity
						.findViewById(R.id.spinner_view_empty));
				return true;
			}
		}
		return false;
	}

	public boolean removeSpinner(Spinner spinner) {
		if (spinnerList.contains(spinner)) {
			if (spinnerList.remove(spinner)) {
				spinner.setAdapter(null);
				spinner.setOnItemSelectedListener(null);
				spinner.setEmptyView(null);
				return true;
			}
		}
		return false;
	}

	public Building getSelectedBuilding() {
		return selectedBuilding;
	}

	public List<Spinner> getSpinnerList() {
		return spinnerList;
	}

	public void refresh() {
		adapter.clear();

		buildingList = storage.getAllBuildings();
		for (Building b : buildingList) {
			adapter.add(b.getName());
		}

		if (buildingList.size() == 0) {
			// setSelectedBuilding(null);

			// adapter.add(activity.getString(R.string.spinner_empty));
			for (Spinner tSpinner : spinnerList) {
				tSpinner.setEnabled(false);
			}
		} else {
			setSelectedBuilding(buildingList.get(0));
		}
	}

	private void notifyListener() {
		for (OnBuildingChangedListener tListener : listenerList) {
			tListener.buildingChanged(this);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (buildingList.size() > 0) {
			setSelectedBuilding(buildingList.get(pos));
		} else {
			setSelectedBuilding(null);
		}

		for (Spinner tSpinner : spinnerList) {
			tSpinner.setSelection(pos);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		setSelectedBuilding(null);
	}

	public void setSelectedBuilding(Building buildingSelected) {
		this.selectedBuilding = buildingSelected;
		notifyListener();
	}

	public void setSelectedPosition(int position) {
		for (Spinner tSpinner : spinnerList) {
			tSpinner.setSelection(position);
		}
	}

	public void setSelectedPosition(String text) {
		int position = adapter.getPosition(text);
		setSelectedPosition(position);
	}
}
