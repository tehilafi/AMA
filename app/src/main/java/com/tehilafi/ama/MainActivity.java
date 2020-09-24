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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView txv_location;

    GoogleMap mMap;
    SearchView searchView;
    SupportMapFragment mapFragment;

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

        instance = this;
        Dexter.withActivity( this ).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                updateLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText( MainActivity.this, "You must accept this location", Toast.LENGTH_SHORT ).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        } ).check();

        searchView = findViewById( R.id.search_location );
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.google_map );
        mapFragment.getMapAsync( this );

        txv_location = findViewById( R.id.txvLocation );

        ///////////////////////////////////////////////////////////////////////////////////////
        // for teh search location
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
        ///////////////////////////////////////////////////////////////////////////////////////
    }

    private void updateLocation() {
        buildLocationReguest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates( locationRequest, getPendingIntent() );
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationReguest() {
       locationRequest = new LocationRequest();
       locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       locationRequest.setInterval(5000);
       locationRequest.setFastestInterval(3000);
       locationRequest.setSmallestDisplacement(10f);
   }

   public void updateTextView(final String value){
        MainActivity.this.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                txv_location.setText(value);
            }
        } );
   }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }
}












