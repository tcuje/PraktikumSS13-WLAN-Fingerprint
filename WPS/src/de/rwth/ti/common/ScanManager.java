package de.rwth.ti.common;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import de.rwth.ti.wps.SuperActivity;

/**
 * This class handles wifi scanning, provides auto scanning and is activated if
 * scan results are available
 * 
 */
public class ScanManager {

	private WifiManager wifi;
	private WifiLock wl;
	private Timer tim;
	private TimerTask scantask;

	public ScanManager(SuperActivity superActivity) {
		// Setup WiFi
		wifi = (WifiManager) superActivity
				.getSystemService(Context.WIFI_SERVICE);
		wl = wifi.createWifiLock("Wavi");

		// Setup auto scan timer
		if (tim == null) {
			tim = new Timer();
		}
	}

	public void onStart() {
		wl.acquire();
	}

	public void onStop() {
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
	public boolean startSingleScan() {
		if (wifi.setWifiEnabled(true) == false) {
			return false;
		}
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
		if (wifi.setWifiEnabled(true) == false) {
			return false;
		}
		if (scantask != null) {
			scantask.cancel();
		}
		scantask = new TimerTask() {
			@Override
			public void run() {
				wifi.startScan();
			}
		};
		tim.schedule(scantask, 0, period);
		return true;
	}

	public void stopAutoScan() {
		if (scantask != null) {
			scantask.cancel();
			scantask = null;
		}
	}

	public WifiManager getWifi() {
		return wifi;
	}

}
