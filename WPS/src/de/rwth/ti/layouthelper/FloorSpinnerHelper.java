package de.rwth.ti.layouthelper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.StorageHandler;

public class FloorSpinnerHelper implements OnItemSelectedListener,
		OnBuildingChangedListener {

	private List<OnFloorChangedListener> listenerList;

	private StorageHandler storage;
	private List<Spinner> spinnerList;
	private List<Floor> floorList;
	private ArrayAdapter<CharSequence> adapter;
	private Building selectedBuilding;
	private Floor selectedFloor;

	private FloorSpinnerHelper(Context context, StorageHandler storage,
			List<Spinner> spinner) {
		this.storage = storage;
		spinnerList = spinner;
		selectedBuilding = null;
		selectedFloor = null;
		listenerList = new ArrayList<OnFloorChangedListener>();

		adapter = new ArrayAdapter<CharSequence>(context,
				android.R.layout.simple_spinner_item);
		for (Spinner tSpinner : spinnerList) {
			tSpinner.setAdapter(adapter);
			tSpinner.setOnItemSelectedListener(this);
		}
	}

	public static FloorSpinnerHelper createInstance(Context context,
			OnFloorChangedListener listener, StorageHandler storage,
			Spinner spinner) {
		ArrayList<Spinner> spinnerList = new ArrayList<Spinner>();
		spinnerList.add(spinner);

		FloorSpinnerHelper helper = new FloorSpinnerHelper(context, storage,
				spinnerList);
		helper.addListener(listener);
		return helper;
	}

	public static FloorSpinnerHelper createInstance(Context context,
			OnFloorChangedListener listener, StorageHandler storage,
			List<Spinner> spinnerList) {
		FloorSpinnerHelper helper = new FloorSpinnerHelper(context, storage,
				spinnerList);
		helper.addListener(listener);
		return helper;
	}

	public boolean addListener(OnFloorChangedListener listener) {
		if (!listenerList.contains(listener)) {
			return listenerList.add(listener);
		}
		return false;
	}

	public boolean removeListener(OnFloorChangedListener listener) {
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
				return true;
			}
		}
		return false;
	}

	public Floor getSelectedFloor() {
		return selectedFloor;
	}

	public List<Spinner> getSpinnerList() {
		return spinnerList;
	}

	@Override
	public void buildingChanged(BuildingSpinnerHelper helper) {
		refresh(helper.getSelectedBuilding());
	}

	public void refresh() {
		refresh(selectedBuilding);
	}

	public void refresh(Building building) {
		selectedBuilding = building;
		adapter.clear();
		if (selectedBuilding != null) {
			floorList = storage.getFloors(selectedBuilding);
			for (Floor b : floorList) {
				adapter.add(b.getName());
			}

			if (floorList.size() == 0) {
				selectedFloor = null;
			} else {
				selectedFloor = floorList.get(0);
			}
		}
	}

	private void notifyListener() {
		for (OnFloorChangedListener tListener : listenerList) {
			tListener.floorChanged(this);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		selectedFloor = floorList.get(pos);

		for (Spinner tSpinner : spinnerList) {
			tSpinner.setOnItemSelectedListener(null);
			tSpinner.setSelection(pos);
			tSpinner.setOnItemSelectedListener(this);
		}

		notifyListener();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		selectedFloor = null;
		notifyListener();
	}
}
