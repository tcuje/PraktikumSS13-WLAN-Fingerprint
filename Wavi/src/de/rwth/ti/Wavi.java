package de.rwth.ti;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.rwth.ti.db.StorageHandler;

/**
 * This is the main activity class
 * 
 * @author tcuje
 * 
 */
public class Wavi extends Activity implements OnClickListener {

	ScanManager scm;
	StorageHandler storage;
	CompassManager cmgr;

	TextView textStatus;
	Button buttonScan;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Setup UI
		textStatus = (TextView) findViewById(R.id.textStatus);
		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonScan.setOnClickListener(this);

		// Setup Wifi
		if (scm == null) {
			scm = new ScanManager(this);
		}

		// Setup database storage
		if (storage == null) {
			storage = new StorageHandler(this);
		}

		// Setup compass manager
		if (cmgr == null) {
			cmgr = new CompassManager(this);
		}
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		storage.onStart();
		scm.onStart();
		cmgr.onStart();
		textStatus.setText("Scans: " + storage.countScans() + "\n");
		textStatus.append("AccessPoints: " + storage.countAccessPoints());
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		storage.onStop();
		scm.onStop();
		cmgr.onStop();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.buttonScan) {
			// FIXME get real data from gui
			scm.startSingleScan(storage.addCheckpoint(0, 0, 0));
		}
	}

	public StorageHandler getStorage() {
		return storage;
	}

	public CompassManager getCompassManager() {
		return cmgr;
	}

}