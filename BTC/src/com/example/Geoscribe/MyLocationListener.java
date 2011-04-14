package com.example.Geoscribe;

import android.app.Activity;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener implements LocationListener, GpsStatus.Listener
{
	private static UseGPS getlocmain;
	//private Start_GPS startGps;
	//private MainScreen mainScreen;
	
	public MyLocationListener(UseGPS start_gps)
	{
		Log.d(getClass().getSimpleName(), "Constructor MyLocationListener.");
		getlocmain = start_gps;
	}
	
	
    @Override
    public void onLocationChanged(Location loc) {
    	try
		{
    		if (loc != null)
			{

				getlocmain.currentLatitude = loc.getLatitude();
				getlocmain.currentLongitude = loc.getLongitude();
				getlocmain.currentLocation = loc;
				
				//***update the location here
				//getlocmain.DisplayLocationInfo(loc);
				//getlocmain.displayLoc(loc);
				Log.d("MyLocationListener", "UseGPS. OnLocationChanged. UseGPS");
				
				//to update start_gps activity location
				//startGps.displayLoc(loc);
				//debug to show onLocationChanged
				//mainScreen = new MainScreen();
				//mainScreen.locationChanged(loc);
				//Log.d("MyLocationListener", "UseGPS. OnLocationChanged. mainScreen");
//				Toast.makeText(getlocmain, 
//	                    "Location changed : Lat: " + loc.getLatitude() + 
//	                    " Lng: " + loc.getLongitude(), 
//	                    Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception ex)
		{
//			String message = String.format("loc unvailable");
//			Toast.makeText(MyLocationListener.this, message, Toast.LENGTH_LONG).show();
		}
    }

    public void onStatusChanged(String s, int i, Bundle b) {
        //Toast.makeText(MyLocationListener.this, "Provider status changed",
        //        Toast.LENGTH_LONG).show();
    }

    public void onProviderDisabled(String s) {
        //Toast.makeText(MyLocationListener.this,
        //        "Provider disabled by the user. GPS turned off",
        //        Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String s) {
        //Toast.makeText(MyLocationListener.this,
        //        "Provider enabled by the user. GPS turned on",
        //        Toast.LENGTH_LONG).show();
    }

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		
	}
}