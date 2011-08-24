package edu.sdsc.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;


public class Getter {
	//The IP for rtd.hprewn.ucsd.edu
	public static final String IP = "198.202.124.3";
	
	/* @param The port number for the tower
	 * @return 1 line of data
	 * Not efficient, try not to use.
	 */
	public static String get(int port){
		if(port == 00000){
			return "incorrect port";
		}
		try {
  	    	Socket appSoc = new Socket( IP , port);
  	        BufferedReader in = new BufferedReader(new
  	        InputStreamReader(appSoc.getInputStream()));
  	        String message = in.readLine();
  	        appSoc.close();
  	        return message;
  	    }catch (Exception e) {
  	    	return e.toString();
  	    }
	}
	
	
	/* @param 1 Line of Data from WXT520 sensors
	 * @return a parsed line of data
	 */
	public static String makeReadable(String msg){
		String message = "";
		String minSpeed = "",aveSpeed = "",maxSpeed = "";
		String minDegree = "",aveDegree = "",maxDegree = "";
		String airPressure = "",airTemperature ="";
		String relativeHumidity = "";
		String rainAccumulation = "",rainDuration = "",rainIntensity = "",rainPeakIntensity = "";
		
		
		if(msg.indexOf("Sn")!=-1){
			minSpeed = "Minimum Speed = " + 
							msg.substring(msg.indexOf("Sn")+3, msg.indexOf("Sn")+6)
							 + "m/s \n";
		}if(msg.indexOf("Sm")!=-1){
			aveSpeed = "Average Speed = " +
							msg.substring(msg.indexOf("Sm")+3, msg.indexOf("Sm")+6)
							 + "m/s \n";
		}if(msg.indexOf("Sx")!=-1){
			maxSpeed = "Max Speed = " +
							msg.substring(msg.indexOf("Sx")+3, msg.indexOf("Sx")+6)
							 + "m/s \n";
		}if(msg.indexOf("Dn")!=-1){
			minDegree = "Minimum Degree = " +
							msg.substring(msg.indexOf("Dn")+3, msg.indexOf("Dn")+6)
							 + "° \n";
		}if(msg.indexOf("Dm")!=-1){
			aveDegree = "Average Degree = " +
							msg.substring(msg.indexOf("Dm")+3, msg.indexOf("Dm")+6)
							 + "° \n";
		}if(msg.indexOf("Dx")!=-1){
			maxDegree = "Max Degree = " +
							msg.substring(msg.indexOf("Dx")+3, msg.indexOf("Dx")+6)
							 + "° \n";
		}if(msg.indexOf("Pa")!=-1){
			airPressure = "Air Pressure = " +
							msg.substring(msg.indexOf("Pa")+3, msg.indexOf("Pa")+6)
							 + "\n";
		}if(msg.indexOf("Ua")!=-1){
			relativeHumidity = "Relative Humidity = " +
							msg.substring(msg.indexOf("Ua")+3, msg.indexOf("Ua")+6)
							 + "\n";
		}if(msg.indexOf("Rc")!=-1){
			rainAccumulation = "Rain Accumulation = " +
							msg.substring(msg.indexOf("Rc")+3, msg.indexOf("Rc")+6)
							 + "\n";
		}if(msg.indexOf("Rc")!=-1){
			rainAccumulation = "Rain Accumulation = " +
							msg.substring(msg.indexOf("Rc")+3, msg.indexOf("Rc")+6)
							 + "\n";
		}if(msg.indexOf("Rd")!=-1){
			rainDuration = "Rain Duration = " +
							msg.substring(msg.indexOf("Rd")+3, msg.indexOf("Rd")+6)
							 + "s \n";
		}if(msg.indexOf("Rc")!=-1){
			rainIntensity = "Rain Intensity = " +
							msg.substring(msg.indexOf("Ri")+3, msg.indexOf("Ri")+6)
							 + "\n";
		}if(msg.indexOf("Rc")!=-1){
			rainPeakIntensity = "Rain Peak Intensity = " +
							msg.substring(msg.indexOf("Rp")+3, msg.indexOf("Rp")+6)
							 + "\n";
		}
		
		message += minSpeed + aveSpeed + maxSpeed 
					+ minDegree + aveDegree + maxDegree
					+ airPressure + airTemperature
					+ relativeHumidity
					+ rainAccumulation + rainDuration + rainIntensity + rainPeakIntensity;
		
		if(message.equals("")){
			message = "No Data was Found";	
		}
		return message;
	}
	
	/* @param 1 Line of Data from WXT520 sensors
	 * @return The Average Degrees, returns -1 if data cannot be found
	 */
	public static int getDegree(String msg){
		if(msg != null){
			if(msg.indexOf("Dm")!=-1){
				return Integer.parseInt(msg.substring(msg.indexOf("Dm")+3, msg.indexOf("Dm")+6));
			}
		}
		return -1;
	}
	
	/* @param 1 Line of Data from WXT520 sensors
	 * @return The Maximum Speed, returns -1.0 if data cannot be found
	 */
	public static double getSpeed(String msg){
		if(msg != null){
			if(msg.indexOf("Sx")!=-1){
					return Double.parseDouble(msg.substring(msg.indexOf("Sx")+3, msg.indexOf("Sx")+6));
			}	
		}
		return -1.0;
	}
	
}
