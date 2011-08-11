package edu.sdsc.test;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
	public final String LAT = "33.019821";
	public final String LONG = "-116.83857";
	private String[] latitudes, longitudes, locations, ports; 
    private MapView map=null;
    private MyLocationOverlay me=null;
    private SitesOverlay sites=null;
    private boolean tracking=false;
    private boolean locked=false;
    private Context mContext;
    private GeoPoint myLoc;
    private MapController mc;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mContext = getApplicationContext();
        	
		latitudes = getResources().getStringArray(R.array.lat_array);
        longitudes = getResources().getStringArray(R.array.lon_array);
        locations = getResources().getStringArray(R.array.location_array);
        ports = getResources().getStringArray(R.array.port_array);

        map=(MapView)findViewById(R.id.mapView);
        mc = map.getController();
        mc.setCenter(getPoint(Double.parseDouble(LAT), 
        		Double.parseDouble(LONG)));
        mc.setZoom(10);
        map.setBuiltInZoomControls(true);
        
        me=new MyLocationOverlay(this, map);
        map.getOverlays().add(me);

        sites=new SitesOverlay();
        map.getOverlays().add(sites);
        map.invalidate();           
    }
	
	@Override
    protected void onResume(){
    	super.onResume();
    	//Resume when activity resumes
    	if(tracking){
    		me.enableMyLocation();
    	}else{
    		me.disableMyLocation();
    	}
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	//Pause when activity pauses
    	me.disableMyLocation();
    }	
	
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();//
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myloc:    findLocation();
            					return true;
            case R.id.lock:     giveLocation();
            					return true;
            case R.id.reset: 	reset();
            					return true;
        }
        return true;
    }
    
    /* Re/* Resets the position of the Map */
    private void findLocation(){
    	myLoc = me.getMyLocation();
    	if(tracking){
    		me.disableMyLocation();
    		tracking = false;
    	}else{
    		me.enableMyLocation();
    		tracking = true;
    	}
    }
    
    /* Makes a Toast notification of the location
     * or "Turn on Tracking if overlay is off. */
    private void giveLocation(){
    	if(me.isMyLocationEnabled()){
    		Toast.makeText(mContext, myLoc.toString(), Toast.LENGTH_LONG);
    	}else{
    		Toast.makeText(mContext, "Turn on Tracking", Toast.LENGTH_SHORT);
    	}
    }
    
    /* Resets the position of the Map */
    private void reset(){
    	mc.animateTo(getPoint(Double.parseDouble(LAT), 
        		Double.parseDouble(LONG)));
        mc.setZoom(10); 
        map.invalidate();
    }

    
    @Override
    protected boolean isRouteDisplayed() {
        return(false);
    }
    
    /* @param Double Latitude and Double Longitude
     * @return A new GeoPoint with those coordinates
     * make sure those are the actual coordinates with the 
     * decimals
     * */
    private GeoPoint getPoint(double lat, double lon) {
        return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
    }

    private class SitesOverlay extends ItemizedOverlay<CustomItem> {
        private List<CustomItem> items=new ArrayList<CustomItem>();

        public SitesOverlay() {
            super(null);
			for(int k = 0;k < latitudes.length;k++){
	    		items.add(new CustomItem(getPoint(Double.parseDouble(latitudes[k]),Double.parseDouble(longitudes[k])),
					locations[k], ports[k], getMarker(R.drawable.arrowscalled)));
    		}			

            populate();
        }
        
        @Override
        protected boolean onTap(int index) {
	        OverlayItem item = items.get(index);
	        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	        dialog.setTitle(item.getTitle());
	        dialog.setMessage(Getter.get(Integer.parseInt(item.getSnippet())));
	        dialog.show();
	        
	      	try{
	      	}catch(NumberFormatException e){
	      		
	      	}
          return true;
        }
        
        @Override
        protected CustomItem createItem(int i) {
            return(items.get(i));
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            super.draw(canvas, mapView, false);
            handler.post(new Runnable() {
                @Override 
                public void run() {
                    for (CustomItem item : items) {
                    	
                    }
                    map.invalidate();
                }               
            });
        }

        @Override
        public int size() {
            return(items.size());
        }


        private Drawable getMarker(int resource) {
            Drawable marker=getResources().getDrawable(resource);

            marker.setBounds(0, 0, marker.getIntrinsicWidth(),
                                                marker.getIntrinsicHeight());
            boundCenter(marker);

            return(marker);
        }
    }

    private class RotateDrawable extends Drawable {
        private Drawable mDrawable;
        private float mPivotX = 0.5f;
        private float mPivotY = 0.5f;
        private float mCurrentDegrees = 0f;
        public RotateDrawable(Drawable drawable) {
            this.mDrawable = drawable;
        }
        
        public void rotate(int Degrees) {
        	mCurrentDegrees = Degrees;
        }
        public void draw(Canvas canvas) {
            int saveCount = canvas.save();
            Rect bounds = mDrawable.getBounds();
            int w = bounds.right - bounds.left;
            int h = bounds.bottom - bounds.top;
            float px = w * mPivotX;
            float py = h * mPivotY;
            canvas.rotate(mCurrentDegrees, 0, 0);
            mDrawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }

        @Override public int getOpacity() {
            return mDrawable.getOpacity();
        }

        @Override
        public void setAlpha(int arg0) {
            mDrawable.setAlpha(arg0);
        }

        @Override
        public void setColorFilter(ColorFilter arg0) {
            mDrawable.setColorFilter(arg0);
        }
    }

    class CustomItem extends OverlayItem {
        public RotateDrawable marker=null;
        CustomItem(GeoPoint pt, String name, String snippet, Drawable marker) {
            super(pt, name, snippet);
            this.marker=new RotateDrawable(marker);
        }


        @Override
        public Drawable getMarker(int stateBitset) {
            Drawable result=(marker);

            setState(result, stateBitset);

            return(result);
        }
    }

}