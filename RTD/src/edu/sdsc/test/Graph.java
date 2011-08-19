package edu.sdsc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class Graph extends Activity {

	public Context context = this;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GraphView(this));
	}
	
	public class GraphView extends View {
			
			public final static int PORT = 12020;
		
			public final static String TAG = "GraphView";
			public final static float STEPX = 0.225f, STEPY = 0.18f;
			private final static int RADIUS = 15;
			//Drawing Tools
			private Paint bgGridPaint, axisPaint, connectingLinePaint, dotsPaint, alertPaint, numberPaint;
			
			private RectF[] yGrid, xGrid;
			private RectF alertLine, xAxis, yAxis;
			//End Drawing Tools
			

			// scale configuration
			private static final int MAX_DATA = 10, Y_LINES = 5, X_LINES = 3;
			private static final int X_AXIS_STEP = 2, Y_AXIS_STEP = 3;
			private int width, height;
			
			private int numberOfData = 0;
			private float[] speeds = new float[MAX_DATA];
			private int currentData = 0;
			/**
			 * Constructor
			 */
			public GraphView(Context context) {
				super(context);
				new InfoGrabber().execute("");
			}
			
			protected void onSizeChanged (int w, int h, int oldw, int oldh){
				width = w;
				height = h;
				initTools();
			}
	
			private void initTools(){
				alertPaint = new Paint();
				alertPaint.setColor(Color.RED);
				alertPaint.setAntiAlias(true);
				
				axisPaint = new Paint();
				axisPaint.setColor(Color.WHITE);
				axisPaint.setAntiAlias(true);
				
				bgGridPaint = new Paint();
				bgGridPaint.setColor(Color.GRAY);
				bgGridPaint.setAntiAlias(true);
				
				dotsPaint = new Paint();
				dotsPaint.setColor(Color.BLUE);
				dotsPaint.setAntiAlias(true);
				
				alertPaint = new Paint();
				alertPaint.setColor(Color.RED);
				alertPaint.setAntiAlias(true);
				
				numberPaint = new Paint();
				numberPaint.setColor(Color.BLUE);
				numberPaint.setAntiAlias(true);
				numberPaint.setTextSize(36);

	            xAxis = new RectF(percentagesToPointsWidth(0.0f), percentagesToPointsHeight(0.89f),
	            		percentagesToPointsWidth(1.0f), percentagesToPointsHeight(0.90f));
	            
	            xGrid = new RectF[X_LINES];
	            for(int k = 0;k<X_LINES;k++){
	            	xGrid[k] = new RectF(percentagesToPointsWidth(0.0f), percentagesToPointsHeight(0.89f - (k+1)*STEPX),
		            		percentagesToPointsWidth(1.0f), percentagesToPointsHeight(0.90f - (k+1)*STEPX));
	            	Log.d(TAG, xGrid[k].toString());
	            }
	            
	            yAxis = new RectF(percentagesToPointsWidth(0.10f), percentagesToPointsHeight(0.0f),
	            		percentagesToPointsWidth(0.11f), percentagesToPointsHeight(1.0f));
	            
	            
	            yGrid = new RectF[Y_LINES];
	            for(int k = 0;k<Y_LINES;k++){
	            	yGrid[k] = new RectF(percentagesToPointsWidth(0.10f + ((k+1)*STEPY)), percentagesToPointsHeight(0.0f),
		            		percentagesToPointsWidth(0.11f + ((k+1)*STEPY)), percentagesToPointsHeight(1.0f));
	            	Log.d(TAG, yGrid[k].toString());
	            }
	            
	            alertLine = new RectF(percentagesToPointsWidth(0.0f), percentagesToPointsHeight(0.10f),
	            		percentagesToPointsWidth(1.0f), percentagesToPointsHeight(0.11f));
	            
			}
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				
				for(int y = 0;y<Y_LINES;y++){
					canvas.drawRect(yGrid[y], bgGridPaint);
				}for(int x = 0;x<X_LINES;x++){
					canvas.drawRect(xGrid[x], bgGridPaint);
				}
				
				drawLineNumbers(canvas);
				
				canvas.drawRect(xAxis, axisPaint);
				canvas.drawRect(yAxis, axisPaint);
				
				drawPoints(canvas);
				
				canvas.drawRect(alertLine, alertPaint);
				invalidate();
			}
			
			private void drawPoints(Canvas canvas){
				for(int k = 0;k<speeds.length;k++){
					canvas.drawCircle( (percentagesToPointsWidth(0.10f + ((k+1)*STEPY)))/2, percentagesToPointsWidth(0.50f), RADIUS, dotsPaint);
				}
			}
			
			private void drawLineNumbers(Canvas canvas){
				for(int k = 1; k<X_LINES+3;k++){
					canvas.drawText(k*X_AXIS_STEP + "", percentagesToPointsWidth(0.05f + (k*STEPY))
							, percentagesToPointsHeight(0.99f), numberPaint);
				}for(int k = 0; k<Y_LINES;k++){
					canvas.drawText(k*Y_AXIS_STEP + "", percentagesToPointsWidth(0.03f)
							, percentagesToPointsHeight(0.99f - ((k)*(STEPY+0.05f))), numberPaint);
				}
				
			}
			
			
			public float percentagesToPointsWidth(float point){
				return point*width;
			}
			public float percentagesToPointsHeight(float point){
				return point*height;
			}
			
			private class InfoGrabber extends AsyncTask<String, String, String> {
				Socket socket;
				int startPort = PORT;
			    @Override
			        protected String doInBackground(String... params) {
			    	if(PORT == 00000){
						Toast.makeText(context, "SORRY TRY AGAIN", Toast.LENGTH_LONG);
					}else{
						try {
							socket = new Socket (Getter.IP,PORT);
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					
				    	while(PORT == startPort){
				   	        try {
				   	        	BufferedReader in = new BufferedReader(new
				   	 	   	    InputStreamReader(socket.getInputStream()));
								String message = in.readLine();
								publishProgress(message);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    	}
					}
					return null;
			     }

			     @Override
			     protected void onProgressUpdate(String... values) {
				     speeds[currentData] = (float)Getter.getSpeed(values[0]);
				     currentData++;
				     if(currentData >= 10){
				    	 currentData = 0;
				     }
				     numberOfData++;
				     
			     }
			     
			     
			     


			 }
		}
}