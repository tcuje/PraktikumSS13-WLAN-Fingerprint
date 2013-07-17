package de.rwth.ti.common;

import de.rwth.ti.db.MeasurePoint;

public class WPSQuadTree {

//	private float xMax;
//	private float yMax;
	private WPSQuadKnot root = null;
	private IPMapView mapView;

	public WPSQuadTree(float x, float y, IPMapView mView) {
//		xMax = x;
//		yMax = y;
		mapView = mView;
		root = new WPSQuadKnot(0, x, 0, y, mapView);
	}

	public void addPoint(MeasurePoint mp) {
		root.addMPoint(mp);
	}

	public MeasurePoint getMPoint(float x, float y) {
		WPSQuadKnot next = root;
		WPSQuadKnot actual = null;
		while (next != null) {
			actual = next;
			next = next.getKnot(x, y);
		}
		return actual.getValue();
	}

	public void remove(MeasurePoint deleteMP) {
		WPSQuadKnot next = root;
		WPSQuadKnot actual = null;
		while(next != null){
			actual = next;
			next = next.getKnot((float)deleteMP.getPosx(), (float)deleteMP.getPosy());
		}
		actual.setValue(null);
	}
}
