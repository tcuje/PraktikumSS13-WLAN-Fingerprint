package de.rwth.ti.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.net.wifi.ScanResult;

public class DataHelper {

	/**
	 * 
	 * @param angle1
	 * @param angle2
	 * @param range
	 * @return Returns true if angle2 is within or in range to angle1
	 */
	public static boolean isInRange(double angle1, double angle2, double range) {
		double diff = Math.abs(angle1 - angle2);
		diff = diff % 360;
		if (diff <= range || diff >= 360 - range) {
			return true;
		}
		return false;
	}

	public static List<ScanResult> sortScanResults(List<ScanResult> results) {
		Collections.sort(results, new Comparator<ScanResult>() {

			@Override
			public int compare(ScanResult lhs, ScanResult rhs) {
				return (lhs.level > rhs.level ? -1
						: (lhs.level == rhs.level ? 0 : 1));
			}
		});
		return results;
	}

	public static String addLeadingZeros(String text, int length) {
		while (text.length() < length) {
			text = '0' + text;
		}
		return text;
	}

}
