package de.rwth.ti.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class IPMapView extends View {

	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mGestureDetector;
	private float mScaleFactor = 1.f;
	private float mMinScaleFactor = 1.f;
	private float mXFocus = 0;
	private float mYFocus = 0;
	private float mXScaleFocus = 0;
	private float mYScaleFocus = 0;
	private float mAccXPoint = 0;
	private float mAccYPoint = 0;
	private float mXPoint = 0;
	private float mYPoint = 0;
	private float mXMPoint = 0;
	private float mYMPoint = 0;
	private int mHeight = 0;
	private int mWidth = 0;
	private int mViewHeight = 0;
	private int mViewWidth = 0;
	private boolean mMeasureMode = true;
	private Context myContext;
	private ArrayList<Path> myPaths;
	private ArrayList<Path> myFillPaths;
	private Paint mPaint = new Paint();
	private Rect mRect = new Rect();

	public IPMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myPaths = new ArrayList<Path>();
		myFillPaths = new ArrayList<Path>();
		myContext = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
	}

	public IPMapView(Context context, AttributeSet attrs,
			InputStream inputStream) {
		super(context, attrs);
		myPaths = new ArrayList<Path>();
		myFillPaths = new ArrayList<Path>();
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
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		mMinScaleFactor = xNew / ((float) mWidth);
		mScaleFactor = mMinScaleFactor;
		mViewHeight = yNew;
		mViewWidth = xNew;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		// canvas.drawColor(android.graphics.Color.GRAY);
		canvas.translate(mXFocus, mYFocus);
		canvas.scale(mScaleFactor, mScaleFactor, mXScaleFocus, mYScaleFocus);

		canvas.getClipBounds(mRect);
		mAccXPoint = mRect.exactCenterX();
		mAccYPoint = mRect.exactCenterY();

		mPaint.setStrokeWidth(3);
		mPaint.setColor(android.graphics.Color.GRAY);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setAntiAlias(true);

		if (myFillPaths != null) {
			for (Path aPath : myFillPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}

		mPaint.setColor(android.graphics.Color.YELLOW);
		mPaint.setStyle(Paint.Style.STROKE);

		if (myPaths != null) {
			for (Path aPath : myPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}
		// Punkt f�r Standort einzeichen
		if (!mMeasureMode) {
			mPaint.setColor(android.graphics.Color.BLUE);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle(mXPoint, mYPoint, 10, mPaint);
		}
		// Messpunkt einzeichen
		if (mMeasureMode) {
			mPaint.setColor(android.graphics.Color.GREEN);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle(mXMPoint, mYMPoint, 10, mPaint);
		}
		canvas.restore();
	}

	public void newMap(InputStream inputStream) {

		/*
		 * FileReader reader = null; try {
		 * 
		 * reader = new FileReader("kartewaltershottkey.svg"); } catch
		 * (FileNotFoundException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
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
				String test = xpp.getName();
				if (xpp.getName().equals("svg")) {
					mHeight = Integer.valueOf(xpp.getAttributeValue(null,
							"height"));
					mWidth = Integer.valueOf(xpp.getAttributeValue(null,
							"width"));
				}
				if (xpp.getName().equals("path")) {
					Path aPath = new Path();
					System.out.println("Attribut d "
							+ xpp.getAttributeValue(null, "d"));
					String aPathRout[] = xpp.getAttributeValue(null, "d")
							.split(" ");
					for (int i = 0; i < aPathRout.length; i++) {
						String pointsCB[];
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
		invalidate();
	}

	public void setPoint(float x, float y) {
		mXFocus = -x + mViewWidth / 2;
		mYFocus = -y + mViewHeight / 2;
		mXPoint = x;
		mYPoint = y;
		mScaleFactor = 1.f;
		invalidate();
	}

	public boolean getMeasureMode() {
		return mMeasureMode;
	}

	public void setMeasureMode(boolean measuremode) {
		mMeasureMode = measuremode;
	}

	public float[] getMeasurePoint() {
		if (mXMPoint == 0 && mYMPoint == 0) {
			return null;
		} else {
			float ret[] = { mXMPoint, mYMPoint };
			return ret;
		}
	}

	protected void setMeasurePoint(float x, float y) {
		mXMPoint = (x / mScaleFactor) - (mViewWidth / (2 * mScaleFactor))
				+ mAccXPoint;
		mYMPoint = (y / mScaleFactor) - (mViewHeight / (2 * mScaleFactor))
				+ mAccYPoint;
		// mXMPoint = (x-mXFocus)/mScaleFactor;
		// mYMPoint = (y-mYFocus)/mScaleFactor;
		invalidate();
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public void onLongPress(MotionEvent e) {
			if (mMeasureMode) {
				setMeasurePoint(e.getX(), e.getY());
			}
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (!mScaleDetector.isInProgress()) {
				mXFocus -= distanceX;
				mYFocus -= distanceY;
				invalidate();
			}
			return true;
		};
	};

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			mXScaleFocus = detector.getFocusX();
			mYScaleFocus = detector.getFocusY();
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(mMinScaleFactor,
					Math.min(mScaleFactor, 5.0f));

			invalidate();
			return true;
		}
	}

}
