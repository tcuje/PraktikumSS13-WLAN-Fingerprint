package de.rwth.ti.wps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


public class IPMapView extends View {
	
	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mGestureDetector;
	private float mScaleFactor = 1.f;
	private float mXFocus = 0;
	private float mYFocus = 0;
	private float mXScaleFocus = 0;
	private float mYScaleFocus = 0;
	private float mXPoint = 0;
	private float mYPoint = 0;
	private float mXMPoint = 0;
	private float mYMPoint = 0;
	private int mHeight = 0;
	private int mWidth = 0;
	private int mViewHeight = 0;
	private int mViewWidth = 0;
	private boolean mMeasureMode = false; 
	private Context myContext;
	private ArrayList<Path> myPaths;
	private ArrayList<Paint> myPaints;
	
	public IPMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myPaths = new ArrayList<Path>();
		myPaints = new ArrayList<Paint>();
		myContext = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
	}
	
	public IPMapView(Context context, AttributeSet attrs, InputStream inputStream) {
		super(context, attrs);
		myPaths = new ArrayList<Path>();
		myPaints = new ArrayList<Paint>();
		myContext = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
		newMap(inputStream);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mScaleDetector.onTouchEvent(ev);
		mGestureDetector.onTouchEvent(ev);
	   
	    return true;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	};
	
	@Override
	 protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
	     super.onSizeChanged(xNew, yNew, xOld, yOld);
		 mScaleFactor = xNew/((float)mWidth);
	     mViewHeight = yNew;
	     mViewWidth =xNew;
	     
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		//canvas.scale(mScaleFactor, mScaleFactor,mXScaleFocus, mYScaleFocus);
		canvas.translate(mXFocus, mYFocus);
		//System.out.println((canvas.getClipBounds()).exactCenterX());
		canvas.scale(mScaleFactor, mScaleFactor);
		Paint p = new Paint();
		p.setColor(-16777216);
		p.setAlpha(255);
		p.setStyle(Paint.Style.FILL);
		
		Paint paint = new Paint();

	    paint.setStrokeWidth(2);
	    paint.setColor(android.graphics.Color.RED);     
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setAntiAlias(true);
		/*
		Path path = new Path();
	    path.setFillType(Path.FillType.EVEN_ODD);
	    path.moveTo(0,0);
	    path.lineTo(1000,1000);
	    path.lineTo(30,1000);
	    path.lineTo(0,0);
	    path.close();
	    canvas.drawPath(path, paint);
		*/
	    if(myPaths.size() != 0){
	    	for (Path aPath: myPaths) {
	    		canvas.drawPath(aPath, paint);
			}
	    }
	    //Punkt für Standort einzeichen
	    if(!mMeasureMode){
	    	paint.setColor(android.graphics.Color.BLUE);
	    	paint.setStyle(Paint.Style.FILL_AND_STROKE);
	    	canvas.drawCircle(mXPoint, mYPoint, 10, paint);
	    }
	    //Messpunkt einzeichen
	    if(mMeasureMode){
	    	paint.setColor(android.graphics.Color.GREEN);
	    	paint.setStyle(Paint.Style.FILL_AND_STROKE);
	    	canvas.drawCircle(mXMPoint, mYMPoint, 10, paint);
	    }
		canvas.restore();
	}
	
	/// creates a new map out of an InputStream(svg file)
	public void newMap(InputStream inputStream){
		
		/*
        FileReader reader = null;
		try {
			
			reader = new FileReader("kartewaltershottkey.svg");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        */
        XmlPullParserFactory factory = null;
		try {
			factory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        factory.setNamespaceAware(true);
        XmlPullParser xpp = null;
		try {
			xpp = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        try {
			xpp.setInput(inputStream,null);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int eventType = 1;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        while (eventType != XmlPullParser.END_DOCUMENT) {
         if(eventType == XmlPullParser.START_DOCUMENT) {
         //    System.out.println("Start document");
         } else if(eventType == XmlPullParser.START_TAG) {
           //  System.out.println("Start tag "+xpp.getName());
             String test = xpp.getName();
             if(xpp.getName().equals("svg")){
            	 mHeight= Integer.valueOf(xpp.getAttributeValue(null, "height"));
            	 mWidth= Integer.valueOf(xpp.getAttributeValue(null, "width"));
             }
             if(xpp.getName().equals("path")){
            	 Path aPath = new Path();
             //    System.out.println("Attribut d "+xpp.getAttributeValue(null, "d"));
                 String aPathRout[] = xpp.getAttributeValue(null, "d").split(" ");
                 for(int i = 0; i < aPathRout.length; i++){
                	 String pointsCB[];
                	 String aCoordinate[];
                	 switch (aPathRout[i].charAt(0)) {
					case 'M':
						aPathRout[i] = aPathRout[i].substring(1);
						if(aPathRout[i].length()==0){
							i++; //für den fall das ein lerzeichen zwichen Befehl und koordinate steht
						}
						aCoordinate = aPathRout[i].split(",");
						aPath.moveTo(Float.parseFloat(aCoordinate[0]), Float.parseFloat(aCoordinate[1]));
						break;
					case 'L':
						aPathRout[i] = aPathRout[i].substring(1);
						if(aPathRout[i].length()==0){
							i++; //für den fall das ein lerzeichen zwichen Befehl und koordinate steht
						}
						aCoordinate = aPathRout[i].split(",");
						aPath.lineTo(Float.parseFloat(aCoordinate[0]), Float.parseFloat(aCoordinate[1]));
						break;
					case 'C':
						aPathRout[i] = aPathRout[i].substring(1);
						if(aPathRout[i].length()==0){
							i++; //für den fall das ein lerzeichen zwichen Befehl und koordinate steht
						}
						aCoordinate = new String[6];
						aCoordinate[0] = (aPathRout[i].split(","))[0];
						aCoordinate[1] = (aPathRout[i].split(","))[1];
						i++;
						aCoordinate[2] = (aPathRout[i].split(","))[0];
						aCoordinate[3] = (aPathRout[i].split(","))[1];
						i++;
						aCoordinate[4] = (aPathRout[i].split(","))[0];
						aCoordinate[5] = (aPathRout[i].split(","))[1];
						aPath.cubicTo(Float.parseFloat(aCoordinate[4]), Float.parseFloat(aCoordinate[5]), 
								Float.parseFloat(aCoordinate[0]), Float.parseFloat(aCoordinate[1]), 
								Float.parseFloat(aCoordinate[2]), Float.parseFloat(aCoordinate[3]));
						
						break;
					case 'z':
						aPath.close();
						break;
					case 'Z':
						aPath.close();
						break;
					default:
						break;
					}
                	myPaths.add(aPath);
                 }
             }
         } else if(eventType == XmlPullParser.END_TAG) {
            // System.out.println("End tag "+xpp.getName());
         } else if(eventType == XmlPullParser.TEXT) {
            // System.out.println("Text "+xpp.getText());
         }
         try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
       // System.out.println("End document");
        invalidate();
	}
	
	///sets the point of current location
	public void setPoint(float x, float y) {
		mXFocus = -x + mViewWidth/2;
		mYFocus = -y + mViewHeight/2;
		mXPoint = x;
		mYPoint = y;
		mScaleFactor = 1.f;
		invalidate();
	}
	
	///sets if measuremode is on or off
	public void setMeasureMode(boolean measuremode) {
		mMeasureMode = measuremode;
	}
	
	///returns the Point set by the user
	public float[] getMeasurPoint(){
		float ret[] = {mXMPoint, mYMPoint};
		return ret;
	}
	
	protected void setMeasurePoint(float x, float y) {
		mXMPoint = (x/mScaleFactor) - (mXFocus/mScaleFactor);
		mYMPoint = (y/mScaleFactor) - (mYFocus/mScaleFactor);
		//mXMPoint = (x-mXFocus)/mScaleFactor;
		//mYMPoint = (y-mYFocus)/mScaleFactor;
		invalidate();
	}
	
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
		@Override
		public void onLongPress(MotionEvent e) {
			if(mMeasureMode){
				setMeasurePoint(e.getX(), e.getY());
			}
			super.onLongPress(e);
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(!mScaleDetector.isInProgress()){
				//mXFocus -= distanceX*mScaleFactor*10;
				//mYFocus -= distanceY*mScaleFactor*10;
				mXFocus -= distanceX;
				mYFocus -= distanceY;
				invalidate();
			}
			return true;
		};
	};
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();
	        mXScaleFocus = detector.getFocusX();
	        mYScaleFocus = detector.getFocusY();
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

	        invalidate();
	        return true;
	    }
	}

}
