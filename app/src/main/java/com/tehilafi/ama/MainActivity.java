package com.tehilafi.ama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback,SharedPreferences.OnSharedPreferenceChangeListener{

    private Button btn_location;
    private Button requestLocation;
    private Button removeLocation;
    MyBackgroundService mService=null;
    private boolean mBound=false;

    private TextView txv_location;
    private boolean isTrackLocation;
    Context context = this;
    LocationManager locationManager;
    GoogleMap mMap;
    SearchView searchView;
    SupportMapFragment mapFragment;

    private  final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder=(MyBackgroundService.LocalBinder)iBinder;
            mService=binder.getService();
            mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService=null;
            mBound=false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
//         SharedPreferences.Editor editor = sharedPref.edit();
//         editor.putString("USERNAME",null);
//         editor.putString("PASSWORD",null);
//         editor.commit();
        String username = sharedPref.getString("USERNAME", null);
        String password = sharedPref.getString("PASSWORD", null);
        //        String username = getDefaults("USERNAME", context);
       //        String password = myPrefs.getString("PASSWORD",null);
        if (username == null && password == null )
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        setContentView( R.layout.activity_main );
        Dexter.withActivity(this).withPermissions(Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
                ))
                .withListener(new MultiplePermissionsListener()
        {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                removeLocation=(Button)findViewById(R.id.remove_location_updates_butten);
                requestLocation=(Button)findViewById(R.id.request_location_updates_butten);
                requestLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mService.requestLocationUpdates();
                    }
                });
                removeLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mService.removeLocationUpdates();
                    }
                });
                setButtonState(Common.requestingLocationUpdates(MainActivity.this));
                bindService(new Intent(MainActivity.this,
                        MyBackgroundService.class),
                        mServiceConnection,
                        Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();


        searchView = findViewById( R.id.search_location );
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.google_map );
        mapFragment.getMapAsync( this );

        btn_location = findViewById( R.id.btnLocation );
        txv_location = findViewById( R.id.txvLocation );
        isTrackLocation = false;

        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

        if (isTrackLocation == false) {
            if (isPermissionToReadGPSLocationOK()) {
                // display Last Known Location
                if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                showLocation( locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER ) );

                // start track GPS location as soon as possible or location changed
                long minTime = 3000;     // minimum time interval between location updates, in milliseconds
                float minDistance = 50;  // minimum distance between location updates, in meters
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, (LocationListener) this );

                isTrackLocation = true;
            }
            else
            {
                Toast.makeText(this, "NO GPS or Location Permission!", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            // stop track GPS location
//            locationManager.removeUpdates( (LocationListener) view );
//            txv_location.setText("No GPS Location Tracking!");
//            btn_location.setText("Start Track GPS Location");
//            isTrackLocation = false;
        }




        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try{
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        } );
        mapFragment.getMapAsync( this );

    }




//    @SuppressLint("MissingPermission")
//    public void trackLocation(View view)
//    {
//        if (isTrackLocation == false)
//        {
//            if (isPermissionToReadGPSLocationOK())
//            {
//                // display Last Known Location
//                showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//
//                // start track GPS location as soon as possible or location changed
//                long minTime = 3000;     // minimum time interval between location updates, in milliseconds
//                float minDistance = 50;  // minimum distance between location updates, in meters
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, (LocationListener) this );
//
//                isTrackLocation = true;
//                btn_location.setText("Stop Track GPS Location");
//            }
//            else
//            {
//                Toast.makeText(this, "NO GPS or Location Permission!", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//        else
//        {
//            // stop track GPS location
//            locationManager.removeUpdates( (LocationListener) view );
//            txv_location.setText("No GPS Location Tracking!");
//            btn_location.setText("Start Track GPS Location");
//            isTrackLocation = false;
//        }
//    }

    private boolean isPermissionToReadGPSLocationOK()
    {
        // first, check if GPS Provider (Location) is Enabled ?
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // second, check if permission to ACCESS_FINE_LOCATION is granted ?
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                return true;
            else
            {
                txv_location.setText("NO Permission To Access Location!");
                return false;
            }
        }
        else
        {
            txv_location.setText("GPS is NOT Enabled!");
            return false;
        }
    }

    public void onLocationChanged(Location location)
    {
        // Called when a new location is found by the location provider.
        showLocation(location);

        // Add a marker in Sydney and move the camera
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(loc).title("HELLO"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    public void onStatusChanged(String provider, int i, Bundle bundle)
    {
    }


    public void onProviderEnabled(String provider)
    {
        Log.d("debug", ">>>> " + provider + " is Enabled!");
    }


    public void onProviderDisabled(String provider)
    {
        Log.d("debug", ">>>> " + provider + " is Disabled!");
    }

    private void showLocation(Location location)
    {
        if (location != null)
        {
            String fullAddress = "";
            Geocoder geocoder = new Geocoder(this);
            try
            {
                List<Address> addressesList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Log.d("debug", ">>>> " + addressesList.toString());
                fullAddress += addressesList.get(0).getAddressLine(0) + "\n";
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            String msg = String.format("Latitude: %1$.4f \n Longitude: %2$.4f \n Timestamp: %3$tT",
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getTime()) + "\n\nAddress:\n" + fullAddress;

            txv_location.setText(msg);
        }
        else
            txv_location.setText("No Location Found!");
    }

//              public void setDefaults(String key, String value, Context context) {
//                new Thread(new Runnable(){
//                    public void run()
//                    {
//                        runOnUiThread(new Runnable()
//                        {
//                            public void run() {
//                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putString(key, value);
//                                editor.commit();
//                            }
//                        });
//
//                    }
//
//        }).start();

//    }


//    public static void setDefaults(String key, String value, Context context) {
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(key, value);
//        editor.commit();
//
//
//    }
//    public static String getDefaults(String key, Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getString(key, null);
//    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop()
    {
        if(mBound)
        {
            unbindService(mServiceConnection);
            mBound=false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
          if(s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
              setButtonState(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES,false));

    }

    private void setButtonState(boolean isRequestEnable) {
        if(isRequestEnable)
        {
            removeLocation.setEnabled(true);
            requestLocation.setEnabled(false);
        }
        else
        {
            removeLocation.setEnabled(false);
            requestLocation.setEnabled(true);
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onListenLocation(sendLocationToActivity event)
    {
        if(event!=null)
        {
            String data=new StringBuilder()
                    .append(event.getLocation().getAltitude())
                    .append("/")
                    .append(event.getLocation().getAltitude())
                    .toString();
            Toast.makeText(mService,data,Toast.LENGTH_SHORT).show();

        }
    }
}

