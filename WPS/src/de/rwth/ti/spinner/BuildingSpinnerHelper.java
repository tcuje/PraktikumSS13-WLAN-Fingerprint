package de.rwth.ti.spinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.rwth.ti.db.Building;
import de.rwth.ti.db.StorageHandler;

public class BuildingSpinnerHelper implements
	OnItemSelectedListener {
	
	private List<OnBuildingChangedListener> listenerList;
	
	private StorageHandler storage;
	private List<Spinner> spinnerList;
	private List<Building> buildingList;
	private ArrayAdapter<CharSequence> adapter;
	private Building selectedBuilding;

	private BuildingSpinnerHelper(Context context, StorageHandler storage, List<Spinner> spinner) {
		this.storage = storage;
		spinnerList = spinner;
		listenerList = new ArrayList<OnBuildingChangedListener>();
		
		adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item);
		for (Spinner tSpinner : spinnerList) {
			tSpinner.setAdapter(adapter);
			tSpinner.setOnItemSelectedListener(this);
		}
	}
	
	public static BuildingSpinnerHelper createInstance(Context context, OnBuildingChangedListener listener, StorageHandler storage, Spinner spinner) {
		ArrayList<Spinner> spinnerList = new ArrayList<Spinner>();
		spinnerList.add(spinner);
		
		BuildingSpinnerHelper helper = new BuildingSpinnerHelper(context, storage, spinnerList);
		helper.addListener(listener);
		return helper;
	}
	
	public static BuildingSpinnerHelper createInstance(Context context, OnBuildingChangedListener listener, StorageHandler storage, List<Spinner> spinnerList) {
		BuildingSpinnerHelper helper = new BuildingSpinnerHelper(context, storage, spinnerList);
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
			selectedBuilding = null;
		} else {
			selectedBuilding = buildingList.get(0);
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
		selectedBuilding = buildingList.get(pos);
		
		for (Spinner tSpinner : spinnerList) {
			tSpinner.setSelection(pos);
		}
		
		notifyListener();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		selectedBuilding = null;
		notifyListener();
	}
}
