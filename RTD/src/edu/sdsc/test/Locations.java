package edu.sdsc.test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Locations extends ListActivity{
	private String[] locations;
	private Intent options;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  options = new Intent(this, GraphOptions.class);
	  
	  
	  //Sets the layout to be a list of the locations
	  locations = getResources().getStringArray(R.array.location_array);
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, locations));
	  
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  
	  //Gives the ListView an on click listener
	  lv.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
				options.putExtra("Location", position);
				startActivity(options);
		}
	  });
	}
	
	
}
