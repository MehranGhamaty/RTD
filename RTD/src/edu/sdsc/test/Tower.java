package edu.sdsc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Tower extends Activity {
	public static final String TAG = "Tower";
	public static final int CLASS1 = 1, CLASS2 = 2, CLASS3 = 3, CLASS4 = 4, CLASS5 = 5;
	private String[] ports, lat, lng, locs;
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
	private int currentAngle = 0;
	private int angle, speed;
	private boolean useCompass, makeNew = false;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tower);
	    
	    new InfoGrabber();
	    
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
    }
    
	public int getClosestTower(){
		int bestTower = 0;
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
        }
        return true;
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
		    	ctr = 0;
			}
			return null;
	     }

	     @Override
	     protected void onProgressUpdate(String... values) {
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
	         Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, 
	                           width, height, matrix, true); 
	     
	         // make a Drawable from Bitmap to allow to set the BitMap 
	         // to the ImageView, ImageButton or what ever
	         return new BitmapDrawable(resizedBitmap);
	     }


	 }
}
