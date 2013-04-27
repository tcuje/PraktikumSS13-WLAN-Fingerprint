package de.rwth.ti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.rwth.ti.R.string;
import de.rwth.ti.db.StorageHandler;

/**
 * This is the main activity class
 * 
 * @author tcuje
 * 
 */
public class Wavi extends Activity implements OnClickListener {

	WifiManager wifi;
	WifiLock wl;
	BroadcastReceiver receiver;
	AlertDialog.Builder builder;
	StorageHandler storage;

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

		// Setup WiFi
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wl = wifi.createWifiLock("Wavi");

		// Register Broadcast Receiver
		if (receiver == null) {
			receiver = new WaviScanReceiver(this);
		}
		if (builder == null) {
			builder = new AlertDialog.Builder(this);
			android.content.DialogInterface.OnClickListener dialogOnClick = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						wifi.setWifiEnabled(true);
						startScan();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};
			builder.setPositiveButton(string.text_yes, dialogOnClick);
			builder.setNegativeButton(string.text_no, dialogOnClick);
		}
		if (storage == null) {
			storage = new StorageHandler(this);
			storage.open();
		}
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
		registerReceiver(receiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wl.acquire();
		textStatus.setText("Scans: " + storage.countScans() + "\n");
		textStatus.append("AccessPoints: " + storage.countAccessPoints());
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
		try {
			unregisterReceiver(receiver);
		} catch (IllegalArgumentException ex) {
			// just ignore it
		}
		wl.release();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.buttonScan) {
			// check if wifi is enabled or not
			if (wifi.isWifiEnabled() == false) {
				builder.setMessage(string.text_wifistate).show();
			} else {
				startScan();
			}
		}
	}

	private void startScan() {
		textStatus.setText("");
		wifi.startScan();
		Toast.makeText(this, string.text_wait, Toast.LENGTH_LONG).show();
	}

	public StorageHandler getStorage() {
		return storage;
	}
}