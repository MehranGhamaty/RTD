package edu.sdsc.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
	
	private Button mapButton, locationButton, towerButton, settingsButton, graphButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    // Link the Buttons
        mapButton = (Button)findViewById(R.id.buttonMap);
        locationButton = (Button)findViewById(R.id.buttonLocation);
        towerButton = (Button)findViewById(R.id.buttonTower);
        settingsButton = (Button)findViewById(R.id.buttonSetting);
	    graphButton = (Button)findViewById(R.id.buttonGraph); 
        
        // Give the button their jobs
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToMaps();
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToLocations();
            }
        });
        towerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToTowers();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToSettings();
            }
        });
        graphButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToGraphs();
            }
        });
	}
	
	/* Moves activity to maps */
	private void goToMaps(){
		Intent map = new Intent(this, Map.class);
		startActivity(map);
	}
	
	/* Moves activity to locations */
	private void goToLocations(){
		Intent loc = new Intent(this, Locations.class);
		startActivity(loc);
	}
	
	/* Moves activity to settings */
	private void goToSettings(){
		Intent set = new Intent(this, Settings.class);
		startActivity(set);
	}
    
	/* Moves activity to towers */
    private void goToTowers(){
    	Intent tow = new Intent(this, Tower.class);
    	startActivity(tow);
    }
    
    /* Moves activity to graphs */
    private void goToGraphs(){
    	Intent gra = new Intent(this, Graph.class);
    	startActivity(gra);
    }
}