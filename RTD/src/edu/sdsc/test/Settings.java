package edu.sdsc.test;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.layout.settings);


	}
	
	public static String getDefaultLang(Context context) {
	    return PreferenceManager.getDefaultSharedPreferences(context)
	           .getString("defaultLang", "english");
	}
}
