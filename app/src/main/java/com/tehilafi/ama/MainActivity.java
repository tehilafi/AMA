package com.tehilafi.ama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, LocationListener, OnMapReadyCallback {

    private TextView txv_location;
    private boolean isTrackLocation;
    private ImageView add_location;


    LocationManager locationManager;
    GoogleMap mMap;
    SearchView searchView;
    SupportMapFragment mapFragment;
    // for database
    DatabaseReference reff;
    DatabaseReference reff1;
    Users location;
    Users chec_users;
    // for location
    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

        searchView = findViewById( R.id.search_location );
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.google_map );
        mapFragment.getMapAsync( this );

        txv_location = findViewById( R.id.txvLocation );
        isTrackLocation = false;

        add_location = findViewById( R.id.add_locationID );
        add_location.setOnClickListener( this );


        //Request permission for location
        instance = this;
        Dexter.withActivity( this ).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {

            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                updateLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText( MainActivity.this, "You must accept this location", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        } ).check();


        // location

       /* if (isTrackLocation == false) {
            if (isPermissionToReadGPSLocationOK()) {
                // display Last Known Location
                if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                Toast.makeText(this, "****************showLocation****************", Toast.LENGTH_LONG).show();
                showLocation( locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER ) );
                Toast.makeText(this, "****************after ///////showLocation****************", Toast.LENGTH_LONG).show();

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

*/
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals( "" )) {
                    Geocoder geocoder = new Geocoder( MainActivity.this );
                    try {
                        addressList = geocoder.getFromLocationName( location, 1 );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get( 0 );
                    LatLng latLng = new LatLng( address.getLatitude(), address.getLongitude() );
                    mMap.addMarker( new MarkerOptions().position( latLng ).title( location ) );
                    mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 10 ) );
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

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates( locationRequest, getPendingIntent() );
    }

    // create broadcast receiver to update location in background and killed mode
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }



    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Requests highly accurate locations
        locationRequest.setInterval( 5000 ); // Set the desired interval for active location updates, in millisecond
        locationRequest.setFastestInterval( 3000 ); // Explicitly set the fastest interval for location updates
        locationRequest.setSmallestDisplacement( 10f ); // Set the minimum displacement between location updates in meters
    }

    // update location in texetView
    public void updateTextView(final String value)
    {
        MainActivity.this.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                txv_location.setText(value);
            }
        } );
    }

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

            String msg = "Address:" + fullAddress;
//            String msg = String.format("Latitude: %1$.4f \n Longitude: %2$.4f \n Timestamp: %3$tT",
//                    location.getLatitude(),
//                    location.getLongitude(),
//                    location.getTime()) + "\n\nAddress:\n" + fullAddress;

            txv_location.setText(msg);
        }
        else
            txv_location.setText("No Location Found!");
    }




    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, AskquestionActivity.class);
        startActivity(intent);

    }
}

