package com.example.admin.speedtrackerapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.example.admin.speedtrackerapplication.SpeedShowingActivity;
import com.example.admin.speedtrackerapplication.SpeedShowingActivity.CustomColor;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;


public class MapActivity extends FragmentActivity {

    private float speedLimit;
    private GoogleMap map;
    private ArrayList<GeolocationItem> locationsHolder;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        Bundle intentBundle = getIntent().getExtras();

        String locs = "";
        locationsHolder = new ArrayList<GeolocationItem>();

        if (intentBundle != null && intentBundle.containsKey("serializedLocations")){
            locs = intentBundle.getString("serializedLocations");
            locationsHolder = SpeedShowingActivity.deserializeArray(locs);
        }
        if (intentBundle != null && intentBundle.containsKey("speedLimit")){
            speedLimit = Float.parseFloat(intentBundle.getString("speedLimit"));
        }
        if (locationsHolder.size() > 0){
            // center the map at the first known location
            Location fst = locationsHolder.get(0).getLocation();
            LatLng fstLatLng = new LatLng(fst.getLatitude(), fst.getLongitude());
            Marker start = map.addMarker(new MarkerOptions().position(fstLatLng).title("Start").
                    icon(BitmapDescriptorFactory.defaultMarker(getHue(locationsHolder.get(0)))));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(fstLatLng, 15));

            LatLng previousLatLng = fstLatLng;
            Location previous = fst;

            // add segmented line - each segment is colored depending on its average speed / speed limit ratio
            // also add markers for each location point, each marked is colored depending on its
            // momentary speed / speed limit ratio
            for(GeolocationItem item: locationsHolder){
                Location current = item.getLocation();
                LatLng currentLatLng = new LatLng(current.getLatitude(), current.getLongitude());
                // add marker for the current item
//				Marker currentMarker = map.addMarker(new MarkerOptions().position(currentLatLng).
//						icon(BitmapDescriptorFactory.defaultMarker(getHue(item))));
                // add segment to the line
                PolylineOptions rectOptions = new PolylineOptions().add(previousLatLng).add(currentLatLng);
//				// compute the average speed between the two  locations -> distance / time
//				// distance in meters
//				float distance = current.distanceTo(previous);
//				// time difference in milliseconds
//				long time = current.getTime() - previous.getTime();
//				float timeSec = (float) time / 1000.0f;
//				float avgSpeed = (distance/timeSec)*3.6f;
//
                Polyline polyline = map.addPolyline(rectOptions);

//		        String segmentColor = getColor(avgSpeed, speedLimit);
                //polyline.setColor(Color.parseColor(segmentColor));
                polyline.setColor(Color.parseColor(item.getColor()));
                previousLatLng = currentLatLng;
                previous = current;
            }
            // add markers for each location point, each marked is colored depending on its momentary speed /
            // speed limit ratio
//			for(GeolocationItem item: locationsHolder){
//				LatLng itemLatLng = new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude());
//				// set a colored marker on the map
//				Marker current = map.addMarker(new MarkerOptions().position(itemLatLng).
//						icon(BitmapDescriptorFactory.defaultMarker(getHue(item))));
//			}
        }
    }

    public static float getHue(GeolocationItem item){
        int color = Color.parseColor(item.getColor());
        // obtain hue from RGB color
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[0];
    }

    public static String getColor(float currentAvgSpeed, float speedLimit){
        String currentColor = "";
        if (speedLimit > currentAvgSpeed){
             SpeedShowingActivity.CustomColor yellow = new CustomColor(255,255,0);
            CustomColor green = new CustomColor(0,255,0);
            float percent = (currentAvgSpeed - speedLimit) / 10.0f;
            currentColor = SpeedShowingActivity.gradientBetween(green, yellow, percent);
        } else if (currentAvgSpeed - speedLimit <= 10 ){
            CustomColor yellow = new CustomColor(255,255,0);
            CustomColor green = new CustomColor(0,255,0);
            float percent = (currentAvgSpeed - speedLimit) / 10.0f;
            currentColor = SpeedShowingActivity.gradientBetween(green, yellow, percent);
        } else if (currentAvgSpeed - speedLimit <= 20) {
            CustomColor yellow = new CustomColor(255,255,0);
            CustomColor red = new CustomColor(255,0,0);
            float percent = (currentAvgSpeed - (speedLimit + 10.0f)) / 10.0f;
            currentColor = SpeedShowingActivity.gradientBetween(yellow, red, percent);
        } else {
            currentColor = "#FF0000";
        }
        return currentColor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
