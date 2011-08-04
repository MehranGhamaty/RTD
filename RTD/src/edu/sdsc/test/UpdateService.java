package edu.sdsc.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		start();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		start();
	}
	
	private void start() {
		
	    try {
	            Socket appSoc = new Socket( "198.202.124.3" ,12020);
	            BufferedReader in = new BufferedReader(new
	            InputStreamReader(appSoc.getInputStream()));
	            String message = in.readLine();
	            add(message);

	    }catch (Exception e) {
	    };
	}
	
	private void add(String msg){
		
	}

}
