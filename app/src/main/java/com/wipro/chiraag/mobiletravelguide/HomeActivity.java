package com.wipro.chiraag.mobiletravelguide;

        import android.content.Context;
        import android.content.Intent;
        import android.location.Address;
        import android.location.Geocoder;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;

        import java.io.IOException;
        import java.util.List;
        import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
    }

    public void onLocationClicked(View view){
        Intent intent = new Intent(this,MyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ClickMe){
            onLocationClicked(v);
        }
    }
}
class LocationAddress {
    private static final String TAG = "LocationAddress";
    public static String ADDRESS = "";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler)
    {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0)
            {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                ADDRESS = address.getLocality();
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Unable connect to Geocoder", e);
        }
        finally
        {
            Message message = Message.obtain();
            message.setTarget(handler);
            if (result != null) {
                message.what = 1;
                Bundle bundle = new Bundle();
                result = "Address:\n" + result;
                bundle.putString("address", result);
                message.setData(bundle);
            }
            else {
                message.what = 1;
                Bundle bundle = new Bundle();
                result = "Latitude: " + latitude +
                        "\n Longitude: " + longitude +
                        "\n Unable to get address for this lat-long.";
                bundle.putString("address", result);
                message.setData(bundle);
            }
            message.sendToTarget();
        }
    }
}


