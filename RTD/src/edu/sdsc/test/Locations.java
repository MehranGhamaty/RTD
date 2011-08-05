package edu.sdsc.test;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Locations extends ListActivity{
	private String[] locations, ports;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  //Sets the layout to be a list of the locations
	  locations = getResources().getStringArray(R.array.location_array);
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, locations));
	  
	  ports = getResources().getStringArray(R.array.port_array);
	  
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  
	  //Gives the ListView an on click listener
	  lv.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
				int socket = Integer.parseInt(ports[position]);
				
				//Display a Toast notification with the data
				Toast.makeText(getApplicationContext(), ((TextView)view).getText() 
						+ ": \n" + Getter.get(socket),Toast.LENGTH_SHORT).show();		      
		}
	  });
	}
	
	
}
