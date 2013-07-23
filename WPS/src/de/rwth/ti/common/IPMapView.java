package de.rwth.ti.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
	private LocationResult location = null;
	private float mapFactor = 6.7f;
	private MeasurePoint mMPoint = null;
	private PointF mMPointOld = null;
	private PointF mMPointLast = null;
	private float mHeight = 0;
	private float mWidth = 0;
	private float mViewHeight = 0;
	private float mViewWidth = 0;
	private boolean mMeasureMode = true;
	private List<Path> myPaths;
	private List<Path> myFillPaths;
	private List<MeasurePoint> myOldPointsList;
//	private WPSQuadTree myOldPoints;
	private Paint mPaint;
	private Rect mRect;
	private OnScaleChangeListener onScaleChangeListener;

	public IPMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myPaths = new LinkedList<Path>();
		myFillPaths = new LinkedList<Path>();
		myOldPointsList = new LinkedList<MeasurePoint>();
//		myOldPoints = null;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
		mPaint = new Paint();
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0.432f);
		mRect = new Rect();
	}

	public void setMScaleFactor(float factor) {
		mScaleFactor = factor;
		if (onScaleChangeListener != null) {
			onScaleChangeListener.onScaleChange(mScaleFactor);
		}
		invalidate();
	}

	public void setMXFocus(float x) {
		mXFocus = x;
		invalidate();
	}

	public void setMYFocus(float y) {
		mYFocus = y;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mScaleDetector.onTouchEvent(ev);
		mGestureDetector.onTouchEvent(ev);
		return true;
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		if (mWidth != 0) {
			mMinScaleFactor = xNew / ((float) mWidth);
		}
		setMScaleFactor(mMinScaleFactor);
		mViewHeight = yNew;
		mViewWidth = xNew;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.parseColor("#F0F0F0"));
		canvas.save();
		boolean test = false;
		canvas.getClipBounds(mRect);
		if (mRect.top == 0 && mRect.left == 0 && mRect.right == mViewWidth
				&& mRect.bottom == mViewHeight) {
			test = true;
		}
		canvas.scale(mScaleFactor, mScaleFactor);
		canvas.translate(mXFocus, mYFocus);
		canvas.getClipBounds(mRect);
		if (test) {
			mAccXPoint = mRect.exactCenterX();
			mAccYPoint = mRect.exactCenterY();
		}

		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0.10f * mapFactor);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		// draw filled paths
		if (myFillPaths != null) {
			for (Path aPath : myFillPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}
		// draw walls
		mPaint.setColor(Color.parseColor("#1DB1E1"));
		mPaint.setStyle(Paint.Style.STROKE);
		/*
		 * if (myFillPaths != null) { for (Path aPath : myFillPaths) {
		 * canvas.drawPath(aPath, mPaint); } }
		 */

		if (myPaths != null) {
			for (Path aPath : myPaths) {
				canvas.drawPath(aPath, mPaint);
			}
		}
		// draw old measure points
		if (true) {
			mPaint.setStyle(Paint.Style.FILL);
			for (MeasurePoint aPoint : myOldPointsList) {
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
		}
		// draw position
		if (mMeasureMode == false && location != null) {
			mPaint.setColor(android.graphics.Color.BLACK);
			canvas.drawCircle((float) location.getX(), (float) location.getY(),
					3, mPaint);
			mPaint.setStyle(Paint.Style.FILL);
			if (location.getAccuracy() == 0) {
				mPaint.setARGB(100, 72, 189, 224);
				canvas.drawCircle((float) location.getX(),
						(float) location.getY(), 80f, mPaint);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setARGB(200, 72, 189, 224);
				canvas.drawCircle((float) location.getX(),
						(float) location.getY(), 80f, mPaint);
				mPaint.setARGB(200, 184, 11, 11);
			} else if (location.getAccuracy() == 1) {
				mPaint.setARGB(100, 72, 189, 224);
				canvas.drawCircle((float) location.getX(),
						(float) location.getY(), 40f, mPaint);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setARGB(200, 72, 189, 224);
				canvas.drawCircle((float) location.getX(),
						(float) location.getY(), 40f, mPaint);
				mPaint.setARGB(200, 247, 255, 47);
			} else if (location.getAccuracy() == 2) {
				mPaint.setARGB(100, 72, 189, 224);
				canvas.drawCircle((float) location.getX(),
						(float) location.getY(), 20f, mPaint);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setARGB(200, 72, 189, 224);
				canvas.drawCircle((float) location.getX(),
						(float) location.getY(), 20f, mPaint);
				mPaint.setARGB(200, 76, 255, 5);
			}
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float) location.getX(), (float) location.getY(),
					0.5f * mapFactor, mPaint);
		}
		// draw active measure point
		if (mMeasureMode == true && mMPoint != null) {
			if (mMPoint.getId() == -1) {
				mPaint.setColor(android.graphics.Color.GREEN);
			} else {
				mPaint.setColor(android.graphics.Color.RED);
			}
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle((float) mMPoint.getPosx(),
					(float) mMPoint.getPosy(), 0.5f * mapFactor, mPaint);
		}
		// restore it
		canvas.restore();
	}

	public void clearMap() {
		myPaths.clear();
		myFillPaths.clear();
//		myOldPoints = null;
		myOldPointsList.clear();
		setMScaleFactor(1.0f);
		mXFocus = 0;
		mYFocus = 0;
		mXScaleFocus = 0;
		mYScaleFocus = 0;
		mAccXPoint = 0;
		mAccYPoint = 0;
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
				// System.out.println("Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				// System.out.println("Start tag " + xpp.getName());
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
					// System.out.println("Attribut d "
					// + xpp.getAttributeValue(null, "d"));
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
							myPaths.add(aPath);
						}
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				// System.out.println("End tag " + xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				// System.out.println("Text " + xpp.getText());
			}
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				// new AlertDialog.Builder(getContext())
				// .setMessage(
				// "Error in File. Please Import again and check your File.")
				// .show();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("End document");
//		myOldPoints = new WPSQuadTree(mWidth, mHeight);
		invalidate();
	}

	public void addOldPoint(MeasurePoint punkt) {
//		myOldPoints.addPoint(punkt);
		myOldPointsList.add(punkt);
	}

	public void setPoint(LocationResult passedLocation) {
		location = new LocationResult(passedLocation.getBuilding(),
				passedLocation.getFloor(), passedLocation.getX(),
				passedLocation.getY(), passedLocation.getAccuracy());
		invalidate();
	}

	public void focusLocationPoint() {
		if (location == null) {
			return;
		}
		float x = -(float) location.getX() + mViewWidth / (2 * mScaleFactor);
		float y = -(float) location.getY() + mViewHeight / (2 * mScaleFactor);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			// Do something for HonyComb and above versions
			AnimatorSet anSet = new AnimatorSet();
			ObjectAnimator objAnX = ObjectAnimator.ofFloat(IPMapView.this,
					"mXFocus", mXFocus, x);
			ObjectAnimator objAnY = ObjectAnimator.ofFloat(IPMapView.this,
					"mYFocus", mYFocus, y);
			anSet.playTogether(objAnX, objAnY);
			anSet.setDuration(1000);
			anSet.start();
		} else {
			// do something for phones running an SDK before HoneyComb
			mXFocus = x;
			mYFocus = y;
		}
	}

	public void zoomLocationPoint() {
		if (location == null) {
			return;
		}
		float newScale = ((mMaxScaleFactor - mMinScaleFactor) / 2);
		float x = -(float) location.getX() + mViewWidth / (2 * newScale);
		float y = -(float) location.getY() + mViewHeight / (2 * newScale);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			// Do something for HonyComb and above versions
			AnimatorSet anSet = new AnimatorSet();
			ObjectAnimator objAnScale = ObjectAnimator.ofFloat(this,
					"mScaleFactor", mScaleFactor, newScale);
			ObjectAnimator objAnX = ObjectAnimator.ofFloat(this, "mXFocus",
					mXFocus, x);
			ObjectAnimator objAnY = ObjectAnimator.ofFloat(this, "mYFocus",
					mYFocus, y);
			anSet.playTogether(objAnScale, objAnX, objAnY);
			anSet.setDuration(1000);
			anSet.start();
		} else {
			// do something for phones running an SDK before HoneyComb
			mXFocus = x;
			mYFocus = y;
			mScaleFactor = newScale;
		}
	}

	public boolean getMeasureMode() {
		return mMeasureMode;
	}

	public void setMeasureMode(boolean measuremode) {
		mMeasureMode = measuremode;
	}

	public MeasurePoint getMeasurePoint() {
		return mMPoint;
	}

	public void next() {
		if (mMPointLast != null) {
			mMPointLast.x = mMPointLast.x + 2 * mapFactor;
			setMeasurePoint(mMPointLast.x, mMPointLast.y);
			invalidate();
		}
	}

	public void nextLine() {
		if (mMPointOld != null) {
			mMPointOld.y = mMPointOld.y + 2 * mapFactor;
			mMPointLast.set(mMPointOld);
			setMeasurePoint(mMPointLast.x, mMPointLast.y);
			invalidate();
		}
	}

	public void setMMPoint(MeasurePoint mp) {
		mMPoint = mp;
	}

	protected void setMeasurePoint(float x, float y) {
//		if (myOldPoints != null) {
//			mMPoint = myOldPoints.getMPoint(x, y);
//			if (mMPoint == null) {
		mMPoint = new MeasurePoint();
		mMPoint.setPosx(x);
		mMPoint.setPosy(y);
		mMPoint.setId(-1);
//			} else if (Math.abs(mMPoint.getPosx() - x) > mapFactor
//					|| Math.abs(mMPoint.getPosy() - y) > mapFactor) {
//				mMPoint = new MeasurePoint();
//				mMPoint.setPosx(x);
//				mMPoint.setPosy(y);
//				mMPoint.setId(-1);
//			}
//		}
	}

	protected void setMeasurePointTouch(float x, float y) {
		float xP = (x / mScaleFactor) - (mViewWidth / (2 * mScaleFactor))
				+ mAccXPoint;
		float yP = (y / mScaleFactor) - (mViewHeight / (2 * mScaleFactor))
				+ mAccYPoint;
		setMeasurePoint(xP, yP);
		if (mMPoint != null) {
			mMPointOld = new PointF();
			mMPointLast = new PointF();
			mMPointOld.x = (float) mMPoint.getPosx();
			mMPointOld.y = (float) mMPoint.getPosy();
			mMPointLast.set(mMPointOld);
		}
		// mXMPoint = (x-mXFocus)/mScaleFactor;
		// mYMPoint = (y-mYFocus)/mScaleFactor;
		invalidate();
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mMeasureMode) {
				setMeasurePointTouch(e.getX(), e.getY());
			}
			super.onSingleTapConfirmed(e);
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float newScale = Math.max(mMinScaleFactor / 1.5f,
					Math.min(mScaleFactor * 2.5f, mMaxScaleFactor));
			float x = (e.getX() / mScaleFactor)
					- (mViewWidth / (2 * mScaleFactor)) + mAccXPoint;
			float y = (e.getY() / mScaleFactor)
					- (mViewHeight / (2 * mScaleFactor)) + mAccYPoint;
			x = -x + mViewWidth / (2 * newScale);
			y = -y + mViewHeight / (2 * newScale);
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				// Do something for HonyComb and above versions
				AnimatorSet anSet = new AnimatorSet();
				ObjectAnimator objAnScale = ObjectAnimator.ofFloat(
						IPMapView.this, "mScaleFactor", mScaleFactor, newScale);
				ObjectAnimator objAnX = ObjectAnimator.ofFloat(IPMapView.this,
						"mXFocus", mXFocus, x);
				ObjectAnimator objAnY = ObjectAnimator.ofFloat(IPMapView.this,
						"mYFocus", mYFocus, y);
				anSet.playTogether(objAnScale, objAnX, objAnY);
				anSet.setDuration(1000);
				anSet.start();
			} else {
				// do something for phones running an SDK before HoneyComb
				mXFocus = x;
				mYFocus = y;
				setMScaleFactor(newScale);
			}
			return true;
		};

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
			scale = Math.max(mMinScaleFactor / 1.5f,
					Math.min(scale, mMaxScaleFactor));
			setMScaleFactor(scale);
			mXFocus = -mXScaleFocus + detector.getFocusX() / mScaleFactor;
			mYFocus = -mYScaleFocus + detector.getFocusY() / mScaleFactor;
			invalidate();
			return true;
		}
	}

	public interface OnScaleChangeListener {
		public void onScaleChange(float scale);
	}

	public void setOnScaleChangeListener(OnScaleChangeListener listener) {
		onScaleChangeListener = listener;
	}

	public void deleteOldMP(MeasurePoint deleteMP) {
		myOldPointsList.remove(deleteMP);
//		myOldPoints.remove(deleteMP);
		invalidate();
	}

}
