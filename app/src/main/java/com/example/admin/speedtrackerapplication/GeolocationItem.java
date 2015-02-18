package com.example.admin.speedtrackerapplication;

import android.location.Location;
import android.location.LocationManager;

public class GeolocationItem {
	private Location loc;
	private String currentColor;
	
	public GeolocationItem(Location l, String col){
		loc = l;
		currentColor = col;
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public String getColor(){
		return currentColor;
	}
	
	public String serialize(){
		String sep = "_::_";
		String lat = Double.toString(loc.getLatitude());
		String lon = Double.toString(loc.getLongitude());
		String speed = Float.toString(loc.getSpeed());
		String time = Long.toString(loc.getTime());
		return lat + sep + lon + sep + speed + sep + time + sep + currentColor;
	}
	
	public static GeolocationItem deserilize (String serializedItem){
		Location l = new Location(LocationManager.GPS_PROVIDER);
		String[] locHolder = serializedItem.split("_::_");
		l.setLatitude(Double.parseDouble(locHolder[0]));
		l.setLongitude(Double.parseDouble(locHolder[1]));
		l.setSpeed(Float.parseFloat(locHolder[2]));
		l.setTime(Long.parseLong(locHolder[3]));
		
		GeolocationItem gi = new GeolocationItem(l, locHolder[4]);
		return gi;
	}
}
