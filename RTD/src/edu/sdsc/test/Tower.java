package edu.sdsc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Tower extends Activity {
	public static final String TAG = "Tower";
	public static final int CLASS1 = 5, CLASS2 = 6, CLASS3 = 7, CLASS4 = 8, CLASS5 = 11;
	private String[] ports, lat, lng, locs;
	private Button graphButton;
	private Drawable arrow, arrow1, arrow2, arrow3, arrow4;
	private int currentPort;
	private int bestTower;
	private LocationManager locationManager;
	private String bestProvider;
	private Spinner spinner;
	private Location location;
	private TextView tv;
	private ImageView arrowView;
	private int ctr = 0;
	private Drawable currentClassDrawable;
	private Context context = this;
	private int angle, speed;
	private boolean useCompass, makeNew = false;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;
	private Intent graph;
	
	// hand dynamics -- all are angular expressed in F degrees
	private float currentAngle = 0;
	private float targetAngle = 0;
	private float arrowVelocity = 0.0f;
	private float arrowAcceleration = 0.0f;
	private long lastMoveTime = -1L;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tower);
	    
	    
	    // Sensor stuff
	    mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    
	    // Get the location manager
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(bestProvider);
		
		
	    ports = getResources().getStringArray(R.array.port_array);  
	    lat = getResources().getStringArray(R.array.lat_array);
        lng = getResources().getStringArray(R.array.lon_array);
	    locs = getResources().getStringArray(R.array.location_array);
        
	    spinner = (Spinner)findViewById(R.id.spinner);
	    tv = (TextView)findViewById(R.id.textViewInfo);
	    arrowView = (ImageView)findViewById(R.id.arrowView);
	    
	    arrow = getResources().getDrawable(R.drawable.arrow);
	    arrow1 = getResources().getDrawable(R.drawable.arrow1);
	    arrow2 = getResources().getDrawable(R.drawable.arrow2);
	    arrow3 = getResources().getDrawable(R.drawable.arrow3);
	    arrow4 = getResources().getDrawable(R.drawable.arrow4);

	    
	    
	    bestTower = getClosestTower();
	    
	    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
	    		this, android.R.layout.simple_spinner_item, locs);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	    spinner.setSelection(bestTower);
	    
	    useCompass = true;
	    
	    graph = new Intent(this, Graph.class);
	    
	    graphButton = (Button)findViewById(R.id.buttonGraph);
	    graphButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	graph.putExtra("Location", bestTower);
                startActivity(graph);
            }
        });
	}
	
	@Override
    protected void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();

        mSensorManager.registerListener(mListener, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mListener);
        finish();
        super.onStop();
    }
    
    @Override
    protected void onPause() {
       finish();
       super.onPause();
    }
    
	public int getClosestTower(){
		int bestTower = 0;
		if(location == null){
			return bestTower;
		}
		double bestDistance = Double.MAX_VALUE;
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		for(int k = 0;k < lat.length ; k++){
			double distanceFrom = distance(Double.parseDouble(lat[k]),Double.parseDouble(lng[k]),
									latitude,longitude);
			locs[k] = locs[k] + " (" + (int)(distanceFrom + 0.5) + " miles)";
			if(distanceFrom <= bestDistance){
				bestTower = k;
				bestDistance = distanceFrom;
			}
		}
		return bestTower;
	}
	
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		  
		return (dist);
	}
	
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();//
        inflater.inflate(R.layout.menu_compass_quit, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:    	finish();
            					return true;
            case R.id.usecompass:	compass();
            					return true;
            case R.id.refresh:	reload();
								return true;
        }
        return true;
    }
    
    public void reload() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		
	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	tv.setText("Working...");
	    	currentPort = Integer.parseInt(ports[pos]);
		    new InfoGrabber().execute("");

	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	      // Do nothing.
	    }
	}
	
	public void compass(){
		//Toast notifications don't work. WHY???
		if(useCompass){
			useCompass = false;
			Toast.makeText(context, "NOT using Compass", Toast.LENGTH_LONG);
		}else{
			useCompass = true;
			Toast.makeText(context, "using Compass", Toast.LENGTH_LONG);
		}
		makeNew = true;
	}
	
	private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            //Log.d(TAG,"sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
            mValues = event.values;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
	
	private class InfoGrabber extends AsyncTask<String, String, String> {
		Socket socket;
		int startPort = currentPort;
        
	    @Override
	        protected String doInBackground(String... params) {
	    	if(currentPort == 00000){
				tv.setText("incorrect port");
			}else{
				try {
					socket = new Socket (Getter.IP,currentPort);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(isOnline()){
			    	while(currentPort == startPort){
			   	        try {
			   	        	BufferedReader in = new BufferedReader(new
			   	 	   	    InputStreamReader(socket.getInputStream()));
							String message = in.readLine();
							publishProgress(message);
							ctr++;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
				}else{
					publishProgress("No Internet Stopping");
				}
		    	ctr = 0;
			}
			return null;
	     }

	     @Override
	     protected void onProgressUpdate(String... values) {
	    	 if(values[0] != null){
	    		 if(values[0] == "No Internet Stopping"){
	    			 tv.setText(values[0]);
	    		 }else{
				     tv.setText(Getter.makeReadable(values[0])+"\nHeading: "+ mValues[0] + "\nLine Number: "+ctr);
				     speed = (int) Getter.getSpeed(values[0]);
				     angle = Getter.getDegree(values[0]);
				     
				     if(speed >= CLASS4){
			        	 currentClassDrawable = arrow4;
			         }else if(speed >= CLASS3){
			        	 currentClassDrawable = arrow3;
			         }else if(speed >= CLASS2){
			        	 currentClassDrawable = arrow2;
			         }else if(speed >= CLASS1){
			        	 currentClassDrawable = arrow1;
			         }else{
			        	 currentClassDrawable = arrow;
			         }
			         
				     
				     //Don't change the image unless it changed
			         if((currentAngle != angle && angle  != -1) || makeNew){
			        	 arrowView.setImageDrawable(rotateDrawable(findRelativeAngle(angle)));
			        	 makeNew = false;
			         }
	    		 }
	    	 }
	     }
	     
	     public boolean isOnline() {
	    	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    	    try{ 
	    	    	NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    	    	if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		    	        return true;
		    	    }
		    	    return false;
	    	    }catch(NullPointerException e){
	    	    	return false;
	    	    }
	    	    
	     }
	     
	     public int findRelativeAngle(int angle) {
	    	int newAngle;
	    	if(useCompass) {
	    		Log.d(TAG, "Returning a new angle");
	    		newAngle = (int) (angle - mValues[0]);
	    	}else{
	    		Log.d(TAG, "Returning unedited angle");
	    		newAngle = angle;
	    	}

		    currentAngle = newAngle;
	    	return newAngle;
	     }

	     private Drawable rotateDrawable(int angle){
	         Bitmap bm = ((BitmapDrawable) currentClassDrawable).getBitmap();
	         
	         int width = bm.getWidth();
	         int height = bm.getHeight();
	         
	         Matrix matrix = new Matrix();

		     matrix.postRotate(angle);
	         
	         // recreate the new Bitmap
	         Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, 
	                           width, height, matrix, true); 
	     
	         // make a Drawable from Bitmap to allow to set the BitMap 
	         // to the ImageView, ImageButton or what ever
	         return new BitmapDrawable(rotatedBitmap);
	     }
	     
	     @SuppressWarnings("unused")
	     private void moveHand() {
	 		if (lastMoveTime != -1L) {
	 			long currentTime = System.currentTimeMillis();
	 			float delta = (currentTime - lastMoveTime) / 1000.0f;

	 			float direction = Math.signum(arrowVelocity);
	 			if (Math.abs(arrowVelocity) < 90.0f) {
	 				arrowAcceleration = 5.0f * (targetAngle - currentAngle);
	 			} else {
	 				arrowAcceleration = 0.0f;
	 			}
	 			currentAngle += arrowVelocity * delta;
	 			arrowVelocity += arrowAcceleration * delta;
	 			if ((targetAngle - currentAngle) * direction < 0.01f * direction) {
	 				currentAngle = targetAngle;
	 				arrowVelocity = 0.0f;
	 				arrowAcceleration = 0.0f;
	 				lastMoveTime = -1L;
	 			} else {
	 				lastMoveTime = System.currentTimeMillis();				
	 			}
	 		} else {
	 			lastMoveTime = System.currentTimeMillis();
	 		
	 		}
	 	}

	 }
}
