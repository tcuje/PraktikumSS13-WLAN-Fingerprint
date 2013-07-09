package de.rwth.ti.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import de.rwth.ti.db.MeasurePoint;
import de.rwth.ti.loc.LocationResult;

public class IPMapView extends View {

	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mGestureDetector;
	private float mScaleFactor = 1.f;
	private float mMinScaleFactor = 1.f;
	private float mMaxScaleFactor = 10.f;
	private float mXFocus = 0;
	private float mYFocus = 0;
	private float mXScaleFocus = 0;
	private float mYScaleFocus = 0;
	private float mAccXPoint = 0;
	private float mAccYPoint = 0;
	//private PointF mPoint = null;
	private LocationResult location = null;
	private PointF mMPoint = null;
	private float mHeight = 0;
	private float mWidth = 0;
	private float mViewHeight = 0;
	private float mViewWidth = 0;
	private boolean mMeasureMode = true;
	private ArrayList<Path> myPaths;
	private ArrayList<Path> myFillPaths;
	private ArrayList<MeasurePoint> myOldPoints;
	private Paint mPaint = new Paint();
	private Rect mRect = new Rect();
	private OnScaleChangeListener onScaleChangeListener;

	public IPMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myPaths = new ArrayList<Path>();
		myFillPaths = new ArrayList<Path>();
		myOldPoints = new ArrayList<MeasurePoint>();
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	public IPMapView(Context context, AttributeSet attrs,
			InputStream inputStream) {
		this(context, attrs);
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
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		if (mWidth != 0) {
			mMinScaleFactor = xNew / ((float) mWidth);
		}
		mScaleFactor = mMinScaleFactor;
		mViewHeight = yNew;
		mViewWidth = xNew;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		// canvas.drawColor(android.graphics.Color.GRAY);
		boolean test = false;
		canvas.getClipBounds(mRect);
		if (mRect.top == 0 && mRect.left == 0 && mRect.right == mViewWidth
				&& mRect.bottom == mViewHeight) {
			test = true;
		}
		// mXScaleFocus -= mXFocus;
		// mYScaleFocus -= mYFocus;
		canvas.scale(mScaleFactor, mScaleFactor);
		// canvas.scale(mScaleFactor, mScaleFactor, mXScaleFocus, mYScaleFocus);
		canvas.translate(mXFocus, mYFocus);
		canvas.getClipBounds(mRect);
		if (test) {
			mAccXPoint = mRect.exactCenterX();
			mAccYPoint = mRect.exactCenterY();
			// System.out.println("Mitte: "+mAccXPoint+","+mAccYPoint);
		}

		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0.432f);
		mPaint.setColor(android.graphics.Color.GRAY);
		mPaint.setStyle(Paint.Style.STROKE);

		if (myFillPaths != null) {
			for (Path aPath : myFillPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}

		mPaint.setColor(android.graphics.Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);

		if (myPaths != null) {
			for (Path aPath : myPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}
		// draw old measure points
		mPaint.setStyle(Style.FILL);
		for (MeasurePoint aPoint : myOldPoints) {
			mPaint.setColor(Color.BLACK);
			canvas.drawCircle((float) aPoint.getPosx(),
					(float) aPoint.getPosy(), 2, mPaint);
			if (aPoint.getQuality() < 0.25) {
				mPaint.setColor(Color.RED);
			} else if (aPoint.getQuality() < 0.75) {
				mPaint.setColor(Color.YELLOW);
			} else {
				mPaint.setColor(Color.GREEN);
			}
			canvas.drawCircle((float) aPoint.getPosx(),
					(float) aPoint.getPosy(), 1.5f, mPaint);
		}
		// draw position
		if (!mMeasureMode && location != null) {
			if (location.getAccuracy()==0){
				mPaint.setColor(android.graphics.Color.RED);
			}else if (location.getAccuracy()==1){
				mPaint.setColor(android.graphics.Color.YELLOW);
			}
			else if (location.getAccuracy()==2){
				mPaint.setColor(android.graphics.Color.GREEN);
			}
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle((float)location.getX(), (float)location.getY(), 3, mPaint);
		}
		// draw active measure point
		if (mMeasureMode && mMPoint != null) {
			mPaint.setColor(android.graphics.Color.GREEN);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle(mMPoint.x, mMPoint.y, 3, mPaint);
		}
//		// draw grid
//		mPaint.setColor(Color.BLACK);
//		for (int x = 0; x < mWidth; x += mScaleFactor) {
//			canvas.drawLine(x, 0, x, mHeight, mPaint);
//		}
//		for (int y = 0; y < mHeight; y += mScaleFactor) {
//			canvas.drawLine(0, y, mWidth, y, mPaint);
//		}
		// restore it
		canvas.restore();
	}

	public void clearMap() {
		myPaths.clear();
		myFillPaths.clear();
		myOldPoints.clear();
		setScaleFactor(1.0f);
		mXFocus = 0;
		mYFocus = 0;
		mXScaleFocus = 0;
		mYScaleFocus = 0;
		mAccXPoint = 0;
		mAccYPoint = 0;
		//mPoint = null;
		location = null;
		mMPoint = null;
		mHeight = 0;
		mWidth = 0;
	}

	public void newMap(InputStream inputStream) {
		/*
		 * FileReader reader = null; try {
		 * 
		 * reader = new FileReader("kartewaltershottkey.svg"); } catch
		 * (FileNotFoundException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
		this.clearMap();

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
					mWidth = 0;
					mHeight = 0;
					String attr = xpp.getAttributeValue(null, "height");
					for (int n = 0; n < attr.length(); n++) {
						String val = attr.substring(0, attr.length() - n);
						try {
							mHeight = Float.valueOf(val);
							break;
						} catch (NumberFormatException ex) {
						}
					}
					attr = xpp.getAttributeValue(null, "width");
					for (int n = 0; n < attr.length(); n++) {
						String val = attr.substring(0, attr.length() - n);
						try {
							mWidth = Float.valueOf(val);
							break;
						} catch (NumberFormatException ex) {
						}
					}
					if (mWidth != 0) {
						mMinScaleFactor = mViewWidth / ((float) mWidth);
					}
				}
				if (xpp.getName().equals("path")) {
					Path aPath = new Path();
					System.out.println("Attribut d "
							+ xpp.getAttributeValue(null, "d"));
					String aPathRout[] = xpp.getAttributeValue(null, "d")
							.split(" ");
					for (int i = 0; i < aPathRout.length; i++) {
						String aCoordinate[] = new String[] { null, null };
						switch (aPathRout[i].charAt(0)) {
						case 'M':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							if (aPathRout[i].contains(",")) {
								aCoordinate = aPathRout[i].split(",");
							} else {
								aCoordinate[0] = aPathRout[i];
								i++;
								aCoordinate[1] = aPathRout[i];
							}
							aPath.moveTo(Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]));
							break;
						case 'm':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							if (aPathRout[i].contains(",")) {
								aCoordinate = aPathRout[i].split(",");
							} else {
								aCoordinate[0] = aPathRout[i];
								i++;
								aCoordinate[1] = aPathRout[i];
							}
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
							if (aPathRout[i].contains(",")) {
								aCoordinate = aPathRout[i].split(",");
							} else {
								aCoordinate[0] = aPathRout[i];
								i++;
								aCoordinate[1] = aPathRout[i];
							}
							aPath.lineTo(Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]));
							break;
						case 'l':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							if (aPathRout[i].contains(",")) {
								aCoordinate = aPathRout[i].split(",");
							} else {
								aCoordinate[0] = aPathRout[i];
								i++;
								aCoordinate[1] = aPathRout[i];
							}
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
							if (aPathRout[i].contains(",")) {
								String[] sp = aPathRout[i].split(",");
								aCoordinate[0] = sp[0];
								aCoordinate[1] = sp[1];
							} else {
								aCoordinate[0] = aPathRout[i];
								i++;
								aCoordinate[1] = aPathRout[i];
							}
							i++;
							if (aPathRout[i].contains(",")) {
								String[] sp = aPathRout[i].split(",");
								aCoordinate[2] = sp[0];
								aCoordinate[3] = sp[1];
							} else {
								aCoordinate[2] = aPathRout[i];
								i++;
								aCoordinate[3] = aPathRout[i];
							}
							i++;
							if (aPathRout[i].contains(",")) {
								String[] sp = aPathRout[i].split(",");
								aCoordinate[4] = sp[0];
								aCoordinate[5] = sp[1];
							} else {
								aCoordinate[4] = aPathRout[i];
								i++;
								aCoordinate[5] = aPathRout[i];
							}
							aPath.cubicTo(Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]),
									Float.parseFloat(aCoordinate[2]),
									Float.parseFloat(aCoordinate[3]),
									Float.parseFloat(aCoordinate[4]),
									Float.parseFloat(aCoordinate[5]));
							break;
						case 'c':
							aPathRout[i] = aPathRout[i].substring(1);
							if (aPathRout[i].length() == 0) {
								i++; // f�r den fall das ein lerzeichen zwichen
										// Befehl und koordinate steht
							}
							aCoordinate = new String[6];
							if (aPathRout[i].contains(",")) {
								String[] sp = aPathRout[i].split(",");
								aCoordinate[0] = sp[0];
								aCoordinate[1] = sp[1];
							} else {
								aCoordinate[0] = aPathRout[i];
								i++;
								aCoordinate[1] = aPathRout[i];
							}
							i++;
							if (aPathRout[i].contains(",")) {
								String[] sp = aPathRout[i].split(",");
								aCoordinate[2] = sp[0];
								aCoordinate[3] = sp[1];
							} else {
								aCoordinate[2] = aPathRout[i];
								i++;
								aCoordinate[3] = aPathRout[i];
							}
							i++;
							if (aPathRout[i].contains(",")) {
								String[] sp = aPathRout[i].split(",");
								aCoordinate[4] = sp[0];
								aCoordinate[5] = sp[1];
							} else {
								aCoordinate[4] = aPathRout[i];
								i++;
								aCoordinate[5] = aPathRout[i];
							}
							aPath.cubicTo(
									Float.parseFloat(aCoordinate[0]),
									Float.parseFloat(aCoordinate[1]),
									Float.parseFloat(aCoordinate[2]),
									Float.parseFloat(aCoordinate[3]),
									(Float.parseFloat(aCoordinate[4]) * mWidth),
									(Float.parseFloat(aCoordinate[5]) * mHeight));
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
		invalidate();
	}

	public void addOldPoint(MeasurePoint punkt) {
		myOldPoints.add(punkt);
	}

/**	public void setPoint(float x, float y) {
		if (mPoint == null) {
			mPoint = new PointF(x, y);
		} else {
			mPoint.set(x, y);
		}
		invalidate();
	}
**/
	public void setPoint(LocationResult passedLocation) {
		location = new LocationResult(passedLocation.getBuilding(), passedLocation.getFloor(), passedLocation.getX(), passedLocation.getY(), passedLocation.getAccuracy());
		invalidate();
	}
	
	/**public void focusPoint() {
		if (mPoint != null) {
			mXFocus = -mPoint.x + mViewWidth / (2 * mScaleFactor);
			mYFocus = -mPoint.y + mViewHeight / (2 * mScaleFactor);
			invalidate();
		}
	}
	**/
	public void focusPoint() {
		if (location != null) {
			mXFocus = -(float)location.getX() + mViewWidth / (2 * mScaleFactor);
			mYFocus = -(float)location.getY() + mViewHeight / (2 * mScaleFactor);
			invalidate();
		}
	}
	public void zoomPoint() {
		setScaleFactor((mMaxScaleFactor - mMinScaleFactor) / 2);
		focusPoint();
	}

	public boolean getMeasureMode() {
		return mMeasureMode;
	}

	public void setMeasureMode(boolean measuremode) {
		mMeasureMode = measuremode;
	}

	public PointF getMeasurePoint() {
		return mMPoint;
	}

	protected void setMeasurePoint(float x, float y) {
		mMPoint = new PointF();
		mMPoint.x = (x / mScaleFactor) - (mViewWidth / (2 * mScaleFactor))
				+ mAccXPoint;
		mMPoint.y = (y / mScaleFactor) - (mViewHeight / (2 * mScaleFactor))
				+ mAccYPoint;
		// mXMPoint = (x-mXFocus)/mScaleFactor;
		// mYMPoint = (y-mYFocus)/mScaleFactor;
		invalidate();
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mMeasureMode) {
				setMeasurePoint(e.getX(), e.getY());
			}
			super.onSingleTapConfirmed(e);
			return true;
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
			if (detector.getTimeDelta() == 0) {
				mXScaleFocus = (((detector.getFocusX()) / mScaleFactor)
						- (mViewWidth / (2 * mScaleFactor)) + mAccXPoint);
				mYScaleFocus = (((detector.getFocusY()) / mScaleFactor)
						- (mViewHeight / (2 * mScaleFactor)) + mAccYPoint);
			}
			float scale = mScaleFactor * detector.getScaleFactor();
			// Don't let the object get too small or too large.
			scale = Math.max(mMinScaleFactor / 4,
					Math.min(scale, mMaxScaleFactor));
			setScaleFactor(scale);
			mXFocus = -mXScaleFocus + detector.getFocusX() / mScaleFactor;
			mYFocus = -mYScaleFocus + detector.getFocusY() / mScaleFactor;
			invalidate();
			return true;
		}
	}

	private void setScaleFactor(float scale) {
		mScaleFactor = scale;
		if (onScaleChangeListener != null) {
			onScaleChangeListener.onScaleChange(scale);
		}
	}

	public interface OnScaleChangeListener {
		public void onScaleChange(float scale);
	}

	public void setOnScaleChangeListener(OnScaleChangeListener listener) {
		onScaleChangeListener = listener;
	}

}
