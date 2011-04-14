package com.example.Geoscribe;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class UseGPS {

    private LocationManager locationManager;
    //private LocationManager celllm;
    private MyLocationListener GPSLocationListener;
    private MyLocationListener networkLocationListener;
    //private MyLocationListener celllocationListener;
    
    private String bestProvider = null;
    
    public double currentLatitude;
	public double currentLongitude;
    
    private float geoX;
    private float geoY;
    
    public Location location = null;
    public Location currentLocation = null;
    
    private Context myContext;
    
    public UseGPS (Context context) {
    	
    	this.myContext = context;
    	Log.d(getClass().getSimpleName(), "Head of UseGPS Constructor. Context: " + myContext);
    	

    	//BluetoothAdapter bluetoothDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
		//Log.d(getClass().getSimpleName(), "After get bluetooth adapter");
		
		//to get providers with criteria
		//List<String> matchingProviders = locationManager.getProviders(criteria,
		//		false);
		
		//alternatively to get all the providers
/*		List<String> providers = locationManager.getProviders(true);
		for (String provider : providers) {
		locationManager.requestLocationUpdates(provider, 1000, 0,
		new LocationListener() {
		public void onLocationChanged(Location location) {}
		public void onProviderDisabled(String provider){}
		public void onProviderEnabled(String provider){}
		public void onStatusChanged(String provider, int status,
		Bundle extras){}
		});*/
		
  //---use the LocationManager class to obtain GPS locations---
    locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);  
    //celllm = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE); 
    
	//to set the criterion	
	Criteria criteria = new Criteria();
	criteria.setAccuracy(Criteria.ACCURACY_FINE);
	criteria.setPowerRequirement(Criteria.POWER_LOW);
	criteria.setAltitudeRequired(false);
	criteria.setBearingRequired(false);
	criteria.setSpeedRequired(false);
	criteria.setCostAllowed(false);
	
	//to get the best provider by setting the criterion
	bestProvider = locationManager.getBestProvider(criteria, true);
	Log.d(getClass().getSimpleName(), "Best Provider: " + bestProvider);
    //Log.d(getClass().getSimpleName(), "Creating MyLocationListener.");
    GPSLocationListener = new MyLocationListener(this);
    networkLocationListener = new MyLocationListener(this);
    //celllocationListener = new MyLocationListener(this);
    //GPSlm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, GPSlocationListener);  
    //GPSlm.addGpsStatusListener(GPSlocationListener);  
    
 // Cell tower and wifi based
/*    if(locationListener != null)
    {
    	Log.d(getClass().getSimpleName(), "Attempting to remove locationListener.");
    	locationManager.removeUpdates(locationListener);
    }*/
    
    Log.d(getClass().getSimpleName(), "Requesting Locationupdates.");
	//locationManager.requestLocationUpdates(bestProvider, 2000, 2, locationListener);
	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, GPSLocationListener);
	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 50, networkLocationListener);
    
    }
    
    //to show location as toast message
/*    private void showLocation () {
    	
    	location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            //Toast.makeText(UseGPS.this, message,
            //        Toast.LENGTH_LONG).show();
            geoX = (float) location.getLongitude();
            geoY = (float) location.getLatitude();
        }
        
    }*/
    
    //return latitude as float
    protected float returnLatitude() {
    	Log.d(getClass().getSimpleName(), "returnLatitude before showLocation.");
    	//showLocation();
    	geoX = (float) currentLatitude;
    	Log.d(getClass().getSimpleName(), "returnLatitude.");
    	return geoX;
    }
    
    //return Longitude as float
    protected float returnLongitude() {
    	//showLocation();
    	geoY = (float) currentLongitude;
    	return geoY;
    }
    
    //return both coordinates as location
    public Location returnCoordinates() {
    	//showLocation();
    	return currentLocation;
    }
    
    public void displayLoc(Location location){
    	
    	Log.d("UseGPS", "In displayLoc");
    	//TextView tvLatitude = (TextView) findViewById(R.id.geo_lat_box);
		//TextView tvLongitude = (TextView) findViewById(R.id.geo_lon_box);
		currentLocation = location;
		currentLatitude = location.getLatitude();
		currentLongitude = location.getLongitude();
		if(currentLocation != null)
		Log.d("UseGPS", "DisplayLoc. CurrentLocation: " + currentLocation.toString());
		//tvLatitude.setText(String.valueOf(location.getLatitude()));
		//tvLongitude.setText(String.valueOf(location.getLongitude()));
    }

/*	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(getClass().getSimpleName(), "OnCreate.");
	}*/
}
