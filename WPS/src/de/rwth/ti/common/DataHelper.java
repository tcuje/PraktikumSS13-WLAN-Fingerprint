package de.rwth.ti.common;

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

}
