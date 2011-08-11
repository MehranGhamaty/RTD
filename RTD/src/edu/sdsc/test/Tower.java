package edu.sdsc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Tower extends Activity {
	private static final int[] FROM_COLOR = new int[]{255, 255, 255};
	public static final int CLASS1 = 5, CLASS2 = 6, CLASS3 = 7, CLASS4 = 8, CLASS5 = 9;
	private static final int THRESHOLD = 3;
	private String[] ports, lat, lng;
	private Drawable arrow;
	private InfoGrabber ig;
	private int currentPort;
	private LocationManager locationManager;
	private String bestProvider;
	private Spinner spinner;
	private Location location;
	private TextView tv;
	private ImageView arrowView;
	private int ctr = 0;
	private Context context = this;
	private int currentColor = Color.WHITE;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tower);
	    
	    ig = new InfoGrabber();
	    
	    // Get the location manager
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(bestProvider);
		
		
	    ports = getResources().getStringArray(R.array.port_array);  
	    lat = getResources().getStringArray(R.array.lat_array);
        lng = getResources().getStringArray(R.array.lon_array);
	    
        
	    spinner = (Spinner)findViewById(R.id.spinner);
	    tv = (TextView)findViewById(R.id.textViewInfo);
	    arrowView = (ImageView)findViewById(R.id.arrowView);
	    arrow = getResources().getDrawable(R.drawable.arrow2);
	    
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.location_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	    spinner.setSelection(getClosestTower());
	    
	}

	public int getClosestTower(){
		int bestTower = 0;
		double bestDistance = Double.MAX_VALUE;
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		for(int k = 0;k < lat.length ; k++){
			double distanceFrom = distance(Double.parseDouble(lat[k]),Double.parseDouble(lng[k]),
									latitude,longitude);
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
		     tv.setText(Getter.makeReadable(values[0])+"\nLine Number: "+ctr);
		     arrowView.setImageDrawable(adjust(rotateDrawable(Getter.getDegree(values[0])),Getter.getSpeed(values[0])));
		     
	     }
	     

	     private Drawable adjust(Drawable d, Double severity){
	         int to = Color.WHITE;
	         if(severity >= CLASS1){
	        	 to = Color.YELLOW;
	         }if(severity >= CLASS2){
	        	 to = Color.rgb(255, 140, 0);
	         }if(severity >= CLASS3){
	        	 to = Color.MAGENTA;
	         }if(severity >= CLASS4){
	        	 to = Color.RED;
	         }if(severity >= CLASS5){
	        	 to = Color.BLACK;
	         }
	         if(currentColor != to){
		         Bitmap src = ((BitmapDrawable) d).getBitmap();
		         Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
		         for(int x = 0;x < bitmap.getWidth();x++)
		             for(int y = 0;y < bitmap.getHeight();y++)
		                 if(match(bitmap.getPixel(x, y))) 
		                     bitmap.setPixel(x, y, to);
	
		         return new BitmapDrawable(bitmap);
	         }else{
	        	 return d;
	         }
	     }

	     private boolean match(int pixel){
	         return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD &&
	             Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD &&
	             Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
	     }
	     
	     public Drawable rotateDrawable(int angle){
	         Bitmap bm = BitmapFactory.decodeResource(getResources(), 
	                R.drawable.arrow2);
	         
	         int width = bm.getWidth();
	         int height = bm.getHeight();
	         
	         Matrix matrix = new Matrix();

	         
	         // rotate the Bitmap
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
