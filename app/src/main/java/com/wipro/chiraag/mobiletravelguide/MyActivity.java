package com.wipro.chiraag.mobiletravelguide;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.provider.Settings;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

public class MyActivity extends Activity
{

    Button btnShowLocation,btnShowAddress,startGuide;
    TextView tvAddress;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_my);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        appLocationService = new AppLocationService(MyActivity.this);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        startGuide =(Button) findViewById(R.id.btnStartGuide);

        startGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                Location networkLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);

                Location testLocation;

                if(gpsLocation == null)
                    testLocation=networkLocation;
                else
                    testLocation=gpsLocation;

                if (testLocation != null)
                {
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(testLocation.getLatitude(), testLocation.getLongitude(),
                            getApplicationContext(), new GeocoderHandler());
                }

                else
                {
                    showSettingsAlert();
                }

                Intent intent=new Intent(getBaseContext(),VideoShow.class);
                intent.putExtra("place","bangalore");
                startActivity(intent);

            }
        });

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LocationManager   lManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                Location networkLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);

                Location testLocation;
                if(gpsLocation==null)
                    testLocation=networkLocation;
                else
                    testLocation=gpsLocation;

                if (testLocation!= null ) {
                    String result = " Latitude: " + testLocation.getLatitude() +
                            " \n Longitude: " + testLocation.getLongitude();
                    tvAddress.setText(result);
                } else {
                    showSettingsAlert();
                }
            }
        });

        btnShowAddress = (Button) findViewById(R.id.btnShowAddress);

        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                Location networkLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);

                Location testLocation;

                if(gpsLocation==null)
                    testLocation=networkLocation;
                else
                    testLocation=gpsLocation;

                if (testLocation != null) {

                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(testLocation.getLatitude(), testLocation.getLongitude(),
                            getApplicationContext(), new GeocoderHandler());

                } else {
                    showSettingsAlert();
                }

            }
        });

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MyActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvAddress.setText(locationAddress);
        }
    }
}

    class AppLocationService extends Activity implements LocationListener
    {

    protected LocationManager locationManager;
    Location location;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public AppLocationService(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation(String provider)
    {
        if (locationManager.isProviderEnabled(provider))
        {
            locationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
            if (locationManager != null)
            {
                location = locationManager.getLastKnownLocation(provider);
                return location;
            }
        }
        return null;
    }
    @Override
    public void onLocationChanged(Location location) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}