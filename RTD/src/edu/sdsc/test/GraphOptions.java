package edu.sdsc.test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class GraphOptions extends ListActivity{
	private String[] options, locations;
	private Intent graph; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  graph = new Intent(this, Graph.class);
	  
	  locations = getResources().getStringArray(R.array.location_array);
	  setTitle(locations[getIntent().getIntExtra("Location", 0)]);
	  //Sets the layout to be a list of the locations
	  options = getResources().getStringArray(R.array.options_array);
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, options));
	  
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  
	  //Gives the ListView an on click listener
	  lv.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
				graph.putExtra("Type", position);
				graph.putExtra("Location",  getIntent().getIntExtra("Location", 0));
				startActivity(graph);      
		}
	  });
	}
	
	
}
