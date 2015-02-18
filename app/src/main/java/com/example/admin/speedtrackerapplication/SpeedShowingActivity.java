package com.example.admin.speedtrackerapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;


public class SpeedShowingActivity extends Activity implements SensorEventListener, LocationListener {

    private String fSpeedLimit;
    private Sensor fAccelerometer;
    private SensorManager sm;
    private LocationManager lm;
    private View vMain;
    private TextView vAcceleration;
    private TextView vSpeed;
    private TextView vLoc;
    private TextView vWarning;
    private Button vButton;
    private float fCurrentSpeed;
    private long fLastRecordedTime;
    private ArrayList<GeolocationItem> fLocationsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceleration_view);

        Bundle intentBundle = getIntent().getExtras();

        if(intentBundle != null && intentBundle.containsKey("speed"))
            fSpeedLimit = intentBundle.getString("speed");

        // managing the acceleration sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        fAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, fAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        vAcceleration = (TextView) findViewById(R.id.acceleration);

        // managing the geolocation sensor
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // get location services from GPS provider, mintime, mindistance = 0 -> as soon as available
        // battery unfriendly
        fLocationsHolder = new ArrayList<GeolocationItem>();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        // init location to null
        // this.onLocationChanged(null);

        vSpeed = (TextView) findViewById(R.id.speed);
        vWarning = (TextView) findViewById(R.id.warning);
        vButton = (Button) findViewById(R.id.map_button);
        vLoc = (TextView) findViewById(R.id.geo_loc);

        vButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(SpeedShowingActivity.this, MapActivity.class);
                Bundle bundle = new Bundle();
                String locs = serializeArray(fLocationsHolder);
                bundle.putString("serializedLocations", locs);
                String speedLimit = fSpeedLimit.replace(" km/h", "");
                bundle.putString("speedLimit", speedLimit);
                i.putExtras(bundle);
                startActivity(i);
            }
        });


        vMain = this.getWindow().getDecorView();
        vMain.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        vAcceleration.setText("Acceleration:\n" +
                "X: " + round(event.values[0], 3) + " m/s\nY: " +
                round(event.values[1], 3) + " m/s\nZ: " +
                round(event.values[2], 3) + " m/s");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            vSpeed.setText( "Speed: -- km/h " );
        else{
            if(vButton.getVisibility() == View.GONE)
                vButton.setVisibility(View.VISIBLE);
            fCurrentSpeed = location.getSpeed()*3.6f;
            //fCurrentSpeed = 9.8f;
            float accuracy = location.getAccuracy();
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            long currentTime = location.getTime();
            vSpeed.setText( round(fCurrentSpeed, 2) + " km/h");
            vLoc.setText("Latitude: " + round((float) lat, 5) +
                    "\nLongitude: " + round((float)lon, 5) +
                    "\nNum of recorded points : " + fLocationsHolder.size());
            String speedLimit = fSpeedLimit.replace(" km/h", "");
            String currentColor = setBackground(fCurrentSpeed, Float.parseFloat(speedLimit), vMain);
            // if the location is changed after 10 seconds of last logging save the new location
            if (currentTime - fLastRecordedTime > 3000){
                fLastRecordedTime = currentTime;
                GeolocationItem x = new GeolocationItem(location, currentColor);
                fLocationsHolder.add(x);
            }
            // don't allow fLocationsHolder to become too big -> save it when len > 150 and clear it afterwards
        }
    }

    // method used to recalculate the gradient of the background, returns the color
    public String setBackground(float currentSpeed, float speedLimit, View activityView){
        String currentColor = "";
        if (currentSpeed < speedLimit){
            // hide the warning view
            toggleWarning(round(currentSpeed - speedLimit, 2));
            // going from blue to green on the RGB cube
            float percent = currentSpeed/speedLimit;
            CustomColor blue = new CustomColor(0,0,255);
            CustomColor green = new CustomColor(0,255,0);
            currentColor = gradientBetween(blue, green, percent);
            activityView.setBackgroundColor(Color.parseColor(currentColor));
            activityView.invalidate();
        } else {
            // needs heavy refactoring
            if (currentSpeed - speedLimit <= 4){
                CustomColor yellow = new CustomColor(255,255,0);
                CustomColor green = new CustomColor(0,255,0);
                float percent = (currentSpeed - speedLimit) / 10.0f;
                currentColor = gradientBetween(green, yellow, percent);
                activityView.setBackgroundColor(Color.parseColor(currentColor));
                toggleWarning(round(currentSpeed - speedLimit, 2));
                activityView.invalidate();
            } else if (currentSpeed - speedLimit <= 8){
                CustomColor yellow = new CustomColor(255,255,0);
                CustomColor red = new CustomColor(255,0,0);
                float percent = (currentSpeed - (speedLimit + 4.0f)) / 4.0f;
                currentColor = gradientBetween(yellow, red, percent);
                activityView.setBackgroundColor(Color.parseColor(currentColor));
                toggleWarning(round(currentSpeed - speedLimit, 2));
                activityView.invalidate();
            } else {
                CustomColor red = new CustomColor(255,0,0);
                activityView.setBackgroundColor(Color.parseColor(red.toHex()));
                toggleWarning(round(currentSpeed - speedLimit, 2));
                vWarning.setTextColor(getResources().getColor(android.R.color.black));
                activityView.invalidate();
                currentColor = "#FF0000";
            }
        }
        return currentColor;
    }

    public void toggleWarning( BigDecimal speedDifference ){
        if (speedDifference.compareTo(BigDecimal.ZERO) > 0){
            vWarning.setVisibility(View.VISIBLE);
            vWarning.setText("You are " + speedDifference + "km/h over the limit!");
        } else {
            vWarning.setVisibility(View.GONE);
        }
    }

    // linear transform
    public static String gradientBetween(CustomColor first, CustomColor second, float percent){
        CustomColor result = new CustomColor((int)(first.r*(1-percent) + second.r*percent),
                (int)(first.g*(1-percent) + second.g*percent),
                (int)(first.b*(1-percent) + second.b*percent));
        return result.toHex();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}


    // representing rgb color
    static class CustomColor{
        public int r, g, b;

        public CustomColor(int r, int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public String toHex(){
            String s = "#" +  hexFormatter(Integer.toHexString(this.r)) +
                    hexFormatter(Integer.toHexString(this.g)) +
                    hexFormatter(Integer.toHexString(this.b));
            return s;
        }

        public int toHexInt(){
            String s = this.toHex();
            int col = Integer.parseInt(s, 16);
            return col;
        }

    }

    public static String hexFormatter(String hexString){
        if (hexString.length() == 1)
            return "0" + hexString;
        else
            return hexString;
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static String serializeArray(ArrayList<GeolocationItem> arr){
        String sep = ";,;,;";
        String result = "";
        if	(arr.size() > 0){
            for(GeolocationItem item: arr){
                result += item.serialize() + sep;
            }
            return result.substring(0, result.length() - 5);
        }
        else
            return "";
    }

    public static ArrayList<GeolocationItem> deserializeArray(String serializedArray){
        if (serializedArray.length() > 0){
            String[] serializedItems = serializedArray.split(";,;,;");
            ArrayList<GeolocationItem> result = new ArrayList<GeolocationItem>();
            for(String item: serializedItems){
                result.add(GeolocationItem.deserilize(item));
            }
            return result;
        } else {
            return null;
        }
    }
}
