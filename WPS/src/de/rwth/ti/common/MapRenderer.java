package de.rwth.ti.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;
import de.rwth.ti.db.MeasurePoint;

public class MapRenderer implements Runnable {

	private ImageView view;
	private byte[] imageData;
	private List<MeasurePoint> measurePoints;

	public MapRenderer(ImageView target, byte[] file, List<MeasurePoint> mps) {
		view = target;
		imageData = file;
		measurePoints = mps;
	}

	@Override
	public void run() {
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
		ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
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
		int mHeight = 0;
		int mWidth = 0;
		List<Path> myPaths = new LinkedList<Path>();
		List<Path> myFillPaths = new LinkedList<Path>();
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
		// FIXME
//		mMinScaleFactor = this.getWidth() / ((float) mWidth);
//		mMaxScaleFactor = 10.0f * mMinScaleFactor;
		Bitmap map = Bitmap.createBitmap(mWidth, mHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(map);
		// draw background color
		canvas.drawRGB(0, 0, 0);
		Paint mPaint = new Paint();
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
		if (measurePoints != null) {
			for (MeasurePoint mp : measurePoints) {
				mPaint.setColor(Constants.COLOR_MEASURE_POINTS);
				mPaint.setStyle(Paint.Style.FILL);
				// FIXME point size
				canvas.drawCircle((float) mp.getPosx(), (float) mp.getPosy(),
						10, mPaint);
				// draw quality marker
				double q = mp.getQuality();
				if (q <= 0.25) {
					mPaint.setColor(Constants.COLOR_MEASURE_POINTS_BAD);
				} else if (q <= 0.75) {
					mPaint.setColor(Constants.COLOR_MEASURE_POINTS_MEDIUM);
				} else {
					mPaint.setColor(Constants.COLOR_MEASURE_POINTS_BEST);
				}
				mPaint.setStyle(Paint.Style.STROKE);
				// FIXME point size
				canvas.drawCircle((float) mp.getPosx(), (float) mp.getPosy(),
						10, mPaint);
			}
		}
		view.setImageBitmap(map);
	}
}
