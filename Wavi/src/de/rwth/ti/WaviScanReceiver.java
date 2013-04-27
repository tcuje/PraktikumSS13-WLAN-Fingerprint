package de.rwth.ti;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import de.rwth.ti.db.Scan;

/**
 * This class gets activated when wifi scan results are available and stores
 * them into database
 * 
 * @author tcuje
 * 
 */
public class WaviScanReceiver extends BroadcastReceiver {

	Wavi wifiDemo;

	public WaviScanReceiver(Wavi wifiDemo) {
		super();
		this.wifiDemo = wifiDemo;
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		List<ScanResult> results = wifiDemo.wifi.getScanResults();
		wifiDemo.textStatus.setText("Folgende Wifi's gefunden:\n");
		if (results != null && !results.isEmpty()) {
			Date d = new Date();
			Scan scan = wifiDemo.getStorage().addScan(d.getTime() / 1000);
			for (ScanResult result : results) {
				wifiDemo.textStatus
						.append(result.BSSID + "\t" + result.level + "\t"
								+ result.frequency + "\t'" + result.SSID
								+ "'\n");
				wifiDemo.getStorage().addAp(scan, result.BSSID, result.level,
						result.frequency, result.SSID);
			}
		} else {
			wifiDemo.textStatus.append("keine");
		}
	}
}
