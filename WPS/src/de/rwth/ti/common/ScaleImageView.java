package de.rwth.ti.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {

	private static final int CLICK = 3;

	private Matrix matrix;

	private enum GestureState {
		NONE, DRAG, ZOOM
	}

	private GestureState mode = GestureState.NONE;

	private PointF last = new PointF();
	private PointF start = new PointF();
	private float minScale = 0.1f;
	private float maxScale = 30.0f;

	private int viewWidth, viewHeight;
	private float saveScale = 1f;
	private int oldMeasuredWidth, oldMeasuredHeight;

	private ScaleGestureDetector mScaleDetector;

	private float[] point = { 0, 0 };

	public ScaleImageView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix = new Matrix();
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mScaleDetector.onTouchEvent(event);
				PointF curr = new PointF(event.getX(), event.getY());

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					last.set(curr);
					start.set(last);
					mode = GestureState.DRAG;
					break;

				case MotionEvent.ACTION_MOVE:
					if (mode == GestureState.DRAG) {
						float deltaX = curr.x - last.x;
						float deltaY = curr.y - last.y;
						matrix.postTranslate(deltaX, deltaY);
						last.set(curr.x, curr.y);
					}
					break;

				case MotionEvent.ACTION_UP:
					mode = GestureState.NONE;
					int xDiff = (int) Math.abs(curr.x - start.x);
					int yDiff = (int) Math.abs(curr.y - start.y);
					if (xDiff < CLICK && yDiff < CLICK) {
						point[0] = (start.x + curr.x) / 2;
						point[1] = (start.y + curr.y) / 2;
						performClick();
					}
					break;

				case MotionEvent.ACTION_POINTER_UP:
					mode = GestureState.NONE;
					break;
				}

				setImageMatrix(matrix);
				invalidate();
				return true; // indicate event was handled
			}

		});
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = GestureState.ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}
			matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(),
					detector.getFocusY());
			return true;
		}
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans) {
			return -trans + minTrans;
		}
		if (trans > maxTrans) {
			return -trans + maxTrans;
		}
		return 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		//
		// Rescales image on rotation
		//
		if (oldMeasuredWidth == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0) {
			return;
		}
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1) {
			// Fit to screen.
			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0
					|| drawable.getIntrinsicHeight() == 0)
				return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();
			float scaleX = (float) viewWidth / (float) bmWidth;
			float scaleY = (float) viewHeight / (float) bmHeight;
			float scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);
			// Center the image
			float redundantYSpace = (float) viewHeight
					- (scale * (float) bmHeight);
			float redundantXSpace = (float) viewWidth
					- (scale * (float) bmWidth);
			redundantYSpace /= (float) 2;
			redundantXSpace /= (float) 2;
			matrix.postTranslate(redundantXSpace, redundantYSpace);
			setImageMatrix(matrix);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setMatrix(matrix);
		Paint paint = new Paint();
		paint.setColor(Constants.COLOR_POSITION);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(point[0], point[1], 10, paint);
	}

}
