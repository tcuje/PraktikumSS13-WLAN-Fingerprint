package de.rwth.ti.common;

public enum Cardinal {

	NORTH, EAST, SOUTH, WEST;

	public static Cardinal getFromAzimuth(double azimuth) {
		while (azimuth < 0) {
			azimuth += 360;
		}
		azimuth = azimuth % 360;
		int ordinal = (int) (azimuth / 90);
		Cardinal result = Cardinal.values()[ordinal];
		return result;
	}

	public long getAsAzimuth() {
		long result = this.ordinal() * 90;
		return result;
	}
}
