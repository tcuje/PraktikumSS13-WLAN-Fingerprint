package de.rwth.ti.wps;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.db.Scan;

/**
 * This class handles wifi scanning, provides auto scanning and is activated if
 * scan results are available
 * 
 */
public class ScanManager extends BroadcastReceiver {

	private SuperActivity app;
	private WifiManager wifi;
	private WifiLock wl;
	private Timer tim;
	private TimerTask scantask;
	private boolean onlineMode;
	private MeasurePoint mpoint;

	public ScanManager(SuperActivity superActivity) {
		this.app = superActivity;
		// Setup WiFi
		wifi = (WifiManager) superActivity.getSystemService(Context.WIFI_SERVICE);
		wl = wifi.createWifiLock("Wavi");

		// Setup auto scan timer
		if (tim == null) {
			tim = new Timer();
		}
		if (scantask == null) {
			scantask = new TimerTask() {
				@Override
				public void run() {
					if (wifi.startScan() == false) {
						// XXX handle error
					}
				}
			};
		}
	}

	public void onStart() {
		app.registerReceiver(this, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wl.acquire();
	}

	public void onStop() {
		try {
			app.unregisterReceiver(this);
		} catch (IllegalArgumentException ex) {
			// just ignore it
		}
		wl.release();
	}

	/**
	 * This activates wifi scanning to retrieve the data for a specified
	 * measurepoint (offline mode). If wifi is disabled it is auto activated.
	 * 
	 * @param mp
	 *            measurepoint where the user device is located
	 * @return Returns <code>true</code> if the operation succeeded, i.e., the
	 *         scan was initiated
	 */
	public boolean startSingleScan(MeasurePoint mp) {
		mpoint = mp;
		if (wifi.setWifiEnabled(true) == false) {
			return false;
		}
		onlineMode = false;
		return wifi.startScan();
	}

	/**
	 * This activates wifi scanning for online navigation. If wifi is disabled
	 * it is auto activated.
	 * 
	 * @param period
	 *            amount of time in milliseconds between scans.
	 * @return Returns <code>true</code> if wifi is enabled, <code>false</code>
	 *         otherwise
	 */
	public boolean startAutoScan(int period) {
		onlineMode = true;
		if (wifi.setWifiEnabled(true) == false) {
			return false;
		}
		tim.schedule(scantask, 0, period);
		return true;
	}

	public void stopAutoScan() {
		onlineMode = false;
		tim.cancel();
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		List<ScanResult> results = wifi.getScanResults();
		if (onlineMode == false) {
			// measure mode, save the access points to database
			if (results != null && !results.isEmpty()) {
				if (mpoint != null) {
					Date d = new Date();
					Scan scan = app.getStorage().createScan(mpoint,
							d.getTime() / 1000,
							app.getCompassManager().getAzimut());
					for (ScanResult result : results) {
						app.getStorage().createAccessPoint(scan, result.BSSID,
								result.level, result.frequency, result.SSID,
								result.capabilities);
					}
					mpoint = null;
				}
			} else {
				// no access points found
			}
		} else {
			// online mode
			// TODO localisation
		}
	}
}
