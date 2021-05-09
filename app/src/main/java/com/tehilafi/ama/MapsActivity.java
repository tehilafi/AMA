package com.tehilafi.ama;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GeoQueryEventListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<String> myLocation;

    //MyBackgroundService mService
    MyBackgroundService mService = null;
    boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        //*******************
        Dexter.withContext( this ).withPermissions( Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)).withListener( new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.

                mService.requestLocationUpdates();
                bindService( new Intent( MapsActivity.this,
                        MyBackgroundService.class ), mServiceConnection, Context.BIND_AUTO_CREATE );

                buildLocationRequest();
                buildLocationCallback();

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( MapsActivity.this );
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById( R.id.map );
                mapFragment.getMapAsync( MapsActivity.this );

                settingGeoFire();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        } ).check();
        //*******************
    }



    private void settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        Toast.makeText( MapsActivity.this, "******" + myLocationRef, Toast.LENGTH_LONG ).show();
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if (mMap != null) {

                    geoFire.setLocation( "You", new GeoLocation( locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude() ), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (currentUser != null) currentUser.remove();
                            currentUser = mMap.addMarker( new MarkerOptions().position( new LatLng( locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude() ) ).title( "You" ) );
                            // After sadd marker move camera
                            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( currentUser.getPosition(), 12.0f ) );

                        }
                    } );

                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 5000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setSmallestDisplacement( 10f );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled( true );

        if (fusedLocationProviderClient != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
            // Add circle for dangerous area
//        for(LatLng latLng : myLocation){
//            mMap.addCircle( new CircleOptions().center( latLng).radius( 500 ).strokeColor( Color.BLUE ).fillColor( 0x220000FF ).strokeWidth( 5.0f ));
//
//            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation( latLng.latitude, latLng.longitude ), 0.5f );
//            geoQuery.addGeoQueryEventListener( MapsActivity.this );
       // }

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng( -34, 151 );
//        mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) );
//        mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );
    }



    @Subscribe(sticky = true, threadMode =  ThreadMode.MAIN)
    public void onListenLocation(ListViewAdapte.SendLocationToActivity event){
        if(event != null){
            String data = new StringBuilder()
                    .append( event.getLocation().getLatitude() )
                    .append( "/" )
                    .append( event.getLocation().getLongitude() )
                    .toString();
            Toast.makeText( mService, data, Toast.LENGTH_SHORT ).show();
            initArea(data);

        }
    }

    private void initArea(String data){
        myLocation = new ArrayList<>();
        myLocation.add(data);

        FirebaseDatabase.getInstance().getReference("MyLocation").child("MyCity").setValue(myLocation).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText( MapsActivity.this, "Update!!!", Toast.LENGTH_LONG ).show();

            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( MapsActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG ).show();
            }
        } );
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Toast.makeText( MapsActivity.this, "in dengerous area ", Toast.LENGTH_LONG ).show();

    }

    @Override
    public void onKeyExited(String key) {
        Toast.makeText( MapsActivity.this, "out dengerous area ", Toast.LENGTH_LONG ).show();

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Toast.makeText( MapsActivity.this, "within dengerous area ", Toast.LENGTH_LONG ).show();

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Toast.makeText( MapsActivity.this, "error!!!", Toast.LENGTH_LONG ).show();

    }

    @Override
    protected void onStop() {
        // fusedLocationProviderClient.removeLocationUpdates( locationCallback );

        if(mBound){
            unbindService( mServiceConnection );
            mBound = false;
        }
        // PreferenceManager.getDefaultSharedPreferences( this ).unregisterOnSharedPreferenceChangeListener(this);
        // EventBus.getDefault().unregister( this );
        super.onStop();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener();
//        EventBus.getDefault().register( this );
//    }
}