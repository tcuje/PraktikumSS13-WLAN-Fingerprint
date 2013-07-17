package de.rwth.ti.common;

import android.graphics.PointF;
import de.rwth.ti.db.MeasurePoint;

public class WPSQuadKnot {

	private MeasurePoint value = null;
	private WPSQuadKnot tl, tr, bl, br;
	private float xMin, xMax, yMin, yMax;
	private IPMapView mapView;

	public WPSQuadKnot(float xMi, float xMa, float yMi, float yMa, IPMapView mView) {
		xMin = xMi;
		xMax = xMa;
		yMin = yMi;
		yMax = yMa;
		tl = null;
		tr = null;
		bl = null;
		br = null;
		//System.out.println("neuer Knoten: "+ xMin+"  " + xMax+"  " + yMin+"  " + yMax);
		mapView = mView;
	}

	public MeasurePoint getValue(){
		return value;
	}
	
	public void setValue(MeasurePoint mp){
		value = mp;
	}
	
	public void addMPoint(MeasurePoint mp) {
		if (tl == null && value == null) {
			value = mp;
		}else {
			if(tl == null){
			   tl = new WPSQuadKnot(xMin, (xMax+xMin)/2, yMin, (yMin+yMax)/2, mapView);
			   tr = new WPSQuadKnot((xMax+xMin)/2, xMax, yMin, (yMin+yMax)/2, mapView);
			   bl = new WPSQuadKnot(xMin, (xMax+xMin)/2, (yMin+yMax)/2, yMax, mapView);
			   br = new WPSQuadKnot((xMax+xMin)/2, xMax, (yMin+yMax)/2, yMax, mapView);
			   mapView.drawLine(new PointF((xMax+xMin)/2, yMin),new PointF((xMax+xMin)/2, yMax));
			   mapView.drawLine(new PointF(xMin, (yMin+yMax)/2),new PointF(xMax, (yMin+yMax)/2));
		    }
			if(value != null){
			   if(value.getPosx()>(xMax+xMin)/2){
			      if(value.getPosy()>(yMax+yMin)/2){
			         br.addMPoint(value);
				  }else{
			         tr.addMPoint(value);
			      }
			   }
			}
			value = null;
			if (mp.getPosx() > (xMax + xMin) / 2) {
				if (mp.getPosy() > (yMax + yMin) / 2) {
					br.addMPoint(mp);
				} else {
					tr.addMPoint(mp);
				}
			} else {
				if (mp.getPosy() > (yMax + yMin) / 2) {
					bl.addMPoint(mp);
				} else {
					tl.addMPoint(mp);
				}
			}
		}
	}
	
	public WPSQuadKnot getKnot(float x,float y){
		if(x>((xMax+xMin)/2)){
			if(y>((yMax+yMin)/2)){
				return br;
			} else {
				return tr;
			}
		}else{
			if(y > ((yMax+yMin)/2)){
				return bl;
			} else {
				return tl;
			}
		}
	}

}
