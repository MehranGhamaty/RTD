package edu.sdsc.test;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;


public final class GraphView extends View{

	private static final String TAG = GraphView.class.getSimpleName();
	
	private Handler handler; 
	
	//Drawing Tools
	private Paint bgGrid, axis, connectingLine, dots, alertPaint;
	
	private RectF[] point, yGrid, xGrid;
	private RectF alertLine, xAxis, yAxis;
	//End Drawing Tools
	
	private Bitmap background; // holds the cached static part
	
	// scale configuration
	private static final int MAX_DATA = 10;
	
	// hand dynamics -- all are angular expressed in F degrees
	private boolean gettingInfo = false;
	private int numberOfData = 0;
	private int[] speeds = new int[10];

	
	public GraphView(Context context) {
		super(context);
		init();
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);
		
		gettingInfo = bundle.getBoolean("gettingInfo");
		speeds = bundle.getIntArray("speeds");
		numberOfData = bundle.getInt("numberOfData");
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		
		Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		state.putBoolean("gettingInfo", gettingInfo);
		state.putIntArray("speeds", speeds);
		state.putInt("numberOfData", numberOfData);
		return state;
	}

	private void init() {
		handler = new Handler();
		
		initDrawingTools();
	}

	private void initDrawingTools() {
		point = new RectF[10];
		yGrid = new RectF[3];
		xGrid = new RectF[5];
		
		alertLine = new RectF();
		xAxis = new RectF(0.29f, 0.24f, 0.55f, 0.76f);
		yAxis = new RectF(0.09f, 0.00f, 0.095f, 1.00f);
		
		bgGrid = new Paint();
		bgGrid.setColor(Color.GRAY);
		
		
		axis = new Paint();
		axis.setColor(Color.WHITE);
		
		connectingLine = new Paint();
		connectingLine.setColor(Color.GREEN);
		
		dots = new Paint();
		dots.setColor(Color.BLUE);
		
		
		alertPaint = new Paint();
		alertPaint.setColor(Color.RED);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
		
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}


	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.w(TAG, "Background not created");
		} else {
			//canvas.drawBitmap(background, 0, 0, bgPaint);
		}
	}
	
	private void drawLines(Canvas canvas) {
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//drawBackground(canvas);

		float scale = (float) getWidth();		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);

		drawLines(canvas);
		
		Log.d(TAG, "Drawing yAxis");
		canvas.drawRect(yAxis, alertPaint);
		Log.d(TAG, "Drawing xAxis");
		canvas.drawRect(xAxis, alertPaint);
		canvas.restore();
	
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "Size changed to " + w + "x" + h);
		Log.d(TAG, "Size was " + oldw + "x" + oldh);
		
		regenerateBackground();
	}
	
	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = (float) getWidth();		
		backgroundCanvas.scale(scale, scale);
		
		drawLines(backgroundCanvas);		
	}
}