package de.rwth.ti.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import de.rwth.ti.db.MeasurePoint;

public class IPMapView extends View {

	private static final float POINT_SIZE = 10;

	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mGestureDetector;
	private float mScaleFactor = 1.f;
	private float mMinScaleFactor = 1.0f;
	private float mMaxScaleFactor = 2.0f;
	private float mXFocus = 0;
	private float mYFocus = 0;
	private float mXScaleFocus = 0;
	private float mYScaleFocus = 0;
	private PointF mPoint = null;
	private PointF mMPoint = null;
	private int mHeight = 0;
	private int mWidth = 0;
	private int mViewHeight = 0;
	private int mViewWidth = 0;
	private boolean mMeasureMode = true;
	private List<Path> myPaths;
	private List<Path> myFillPaths;
	private List<MeasurePoint> myPoints;
	private Paint mPaint;
	private Bitmap map;
	private Rect zoomBounds;

	public IPMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myPaths = new ArrayList<Path>();
		myFillPaths = new ArrayList<Path>();
		myPoints = new ArrayList<MeasurePoint>();
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
		mPaint = new Paint();
		mPaint.setStrokeWidth(3);
		mPaint.setAntiAlias(true);
		zoomBounds = new Rect();
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
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		if (mWidth != 0) {
			mMinScaleFactor = xNew / ((float) mWidth);
			mMaxScaleFactor = 10.0f * mMinScaleFactor;
		}
//		mScaleFactor = mMinScaleFactor;
		mViewHeight = yNew;
		mViewWidth = xNew;
	}

	private void redrawMap() {
		map = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(map);
		// draw background color
		canvas.drawARGB(255, 0, 0, 0);
		if (myFillPaths != null) {
			mPaint.setColor(Constants.COLOR_FLOOR_FILL);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			for (Path aPath : myFillPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}
		if (myPaths != null) {
			mPaint.setColor(Constants.COLOR_FLOOR_WALL);
			mPaint.setStyle(Paint.Style.STROKE);
			for (Path aPath : myPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}
		if (myPoints != null) {
			mPaint.setColor(Constants.COLOR_MEASURE_POINTS);
			mPaint.setStyle(Paint.Style.FILL);
			for (MeasurePoint mp : myPoints) {
				canvas.drawCircle((float) mp.getPosx(), (float) mp.getPosy(),
						POINT_SIZE, mPaint);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		// FIXME zoom
		canvas.scale(mScaleFactor, mScaleFactor, mXScaleFocus, mYScaleFocus);
		canvas.translate(mXFocus, mYFocus);
		// draw pre rendered map from bitmap
		if (map != null) {
			canvas.drawBitmap(map, 0, 0, mPaint);
		}
		if (mMeasureMode == false) {
			// draw positon
			if (mPoint != null) {
				mPaint.setColor(Constants.COLOR_POSITION);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(mPoint.x, mPoint.y, POINT_SIZE, mPaint);
			}
		} else {
			// draw measure point
			if (mMPoint != null) {
				mPaint.setColor(Constants.COLOR_ACTIVE_POINT);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(mMPoint.x, mMPoint.y, POINT_SIZE, mPaint);
			}
		}
		// FIXME
//		// draw current zoom factor
//		canvas.translate(-mXFocus, -mYFocus);
//		canvas.scale(1 / mScaleFactor, 1 / mScaleFactor, mXScaleFocus,
//				mYScaleFocus);
//		mPaint.setColor(Color.BLACK);
//		String zStr = Float.toString(mScaleFactor);
//		int ind = zStr.indexOf(".");
//		if (ind != -1) {
//			int len = Math.min(ind + 3, zStr.length());
//			zStr = zStr.substring(0, len);
//		}
//		mPaint.getTextBounds(zStr, 0, zStr.length(), zoomBounds);
//		float x = canvas.getWidth() - zoomBounds.width();
//		float y = canvas.getHeight() - zoomBounds.height();
//		canvas.drawText(zStr, x, y, mPaint);
		// restore it
		canvas.restore();
	}

	public void setPoint(float x, float y) {
		if (mPoint == null) {
			mPoint = new PointF();
		}
		mPoint.set(x, y);
		invalidate();
	}

	public boolean getMeasureMode() {
		return mMeasureMode;
	}

	public void setMeasureMode(boolean measuremode) {
		mMeasureMode = measuremode;
	}

	public float[] getMeasurePoint() {
		float result[] = null;
		if (mMPoint != null) {
			result = new float[] { mMPoint.x, mMPoint.y };
		}
		return result;
	}

	protected void setMeasurePoint(float x, float y) {
		mMPoint = new PointF();
//		mMPoint.x = (x / mScaleFactor) - (mViewWidth / (2 * mScaleFactor))
//				+ mAccXPoint;
//		mMPoint.y = (y / mScaleFactor) - (mViewHeight / (2 * mScaleFactor))
//				+ mAccYPoint;
		// FIXME zoom
		mMPoint.x = (x - mXScaleFocus) / mScaleFactor - mXFocus;
		mMPoint.y = (y - mYScaleFocus) / mScaleFactor - mYFocus;
		invalidate();
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public void onLongPress(MotionEvent e) {
			if (mMeasureMode && map != null) {
				setMeasurePoint(e.getX(), e.getY());
			}
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (!mScaleDetector.isInProgress()) {
				mXFocus -= distanceX / mScaleFactor;
				mYFocus -= distanceY / mScaleFactor;
				invalidate();
			}
			return true;
		};
	};

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float f = detector.getScaleFactor();
			mScaleFactor *= f;
			mXScaleFocus = detector.getFocusX();
			mYScaleFocus = detector.getFocusY();
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(mMinScaleFactor,
					Math.min(mScaleFactor, mMaxScaleFactor));
			invalidate();
			return true;
		}
	}

	public void center() {
		if (mPoint != null) {
			mXFocus = -mPoint.x + mViewWidth / 2;
			mYFocus = -mPoint.y + mViewHeight / 2;
			invalidate();
		}
	}

	public void clear() {
		map = null;
		mPoint = null;
	}

	public float getScaleFactor() {
		return mScaleFactor;
	}

	public void newMap(InputStream inputStream, List<MeasurePoint> measurePoints) {
		myPoints.clear();
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
			xpp.setInput(inputStream, null);
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
			if (eventType == XmlPullParser.START_DOCUMENT) {
				System.out.println("Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				System.out.println("Start tag " + xpp.getName());
				if (xpp.getName().equals("svg")) {
					String val = xpp.getAttributeValue(null, "height");
					mHeight = Integer.valueOf(val);
					val = xpp.getAttributeValue(null, "width");
					mWidth = Integer.valueOf(val);
				}
				if (xpp.getName().equals("path")) {
					Path aPath = new Path();
					System.out.println("Attribut d "
							+ xpp.getAttributeValue(null, "d"));
					String aPathRout[] = xpp.getAttributeValue(null, "d")
							.split(" ");
					for (int i = 0; i < aPathRout.length; i++) {
						String aCoordinate[];
						switch (aPathRout[i].charAt(0)) {
						case 'M':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							aCoordinate = aPathRout[i].split(",");
							aPath.moveTo(Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]));
							break;
						case 'm':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							aCoordinate = aPathRout[i].split(",");
							aPath.moveTo(
									(Float.parseFloat(aCoordinate[0]) * mWidth),
									(Float.parseFloat(aCoordinate[1]) * mHeight));
							break;
						case 'L':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							aCoordinate = aPathRout[i].split(",");
							aPath.lineTo(Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]));
							break;
						case 'l':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							aCoordinate = aPathRout[i].split(",");
							aPath.lineTo(
									(Float.parseFloat(aCoordinate[0]) * mWidth),
									(Float.parseFloat(aCoordinate[1]) * mHeight));
							break;
						case 'C':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
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
							aPath.cubicTo(Float.parseFloat(aCoordinate[4]),
									Float.parseFloat(aCoordinate[5]),
									Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]),
									Float.parseFloat(aCoordinate[2]),
									Float.parseFloat(aCoordinate[3]));
							break;
						case 'c':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
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
							aPath.cubicTo(
									Float.parseFloat(aCoordinate[4]),
									Float.parseFloat(aCoordinate[5]),
									Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]),
									(Float.parseFloat(aCoordinate[2]) * mWidth),
									(Float.parseFloat(aCoordinate[3]) * mHeight));
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
						if (xpp.getAttributeValue(null, "fill-opacity") != null) {
							if (xpp.getAttributeValue(null, "fill-opacity")
									.equals("0")) {
								myPaths.add(aPath);
							} else {
								myFillPaths.add(aPath);
							}
						} else {
							myFillPaths.add(aPath);
						}
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				System.out.println("End tag " + xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				System.out.println("Text " + xpp.getText());
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
		System.out.println("End document");
		mMinScaleFactor = this.getWidth() / ((float) mWidth);
		mMaxScaleFactor = 10.0f * mMinScaleFactor;
		myPoints.addAll(measurePoints);
		redrawMap();
		invalidate();
	}
}
