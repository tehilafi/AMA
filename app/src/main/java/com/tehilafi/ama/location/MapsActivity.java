package com.tehilafi.ama.location;

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
import androidx.core.content.ContextCompat;
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
import com.tehilafi.ama.PermissionUtilities;
import com.tehilafi.ama.R;

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
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ) {
            doMapThings();

        }else{
            ActivityCompat.requestPermissions(this, PermissionUtilities.requestStringPermissions(), 100);
        }
//        ActivityCompat.requestPermissions(this, PermissionUtilities.requestStringPermissions(), 100);
        /*Dexter.withContext( this ).withPermissions( Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)).withListener( new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.

                doMapThings();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
            }
        } ).check();*/
        //*******************
    }

    private void doMapThings() {
        mService.requestLocationUpdates();
        bindService(new Intent(MapsActivity.this,
                MyBackgroundService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

        buildLocationRequest();
        buildLocationCallback();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        settingGeoFire();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    doMapThings();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }



    private void settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
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
    }



    @Subscribe(sticky = true, threadMode =  ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event){
        if(event != null){
            String data = new StringBuilder()
                    .append( event.getLocation().getLatitude() )
                    .append( "/" )
                    .append( event.getLocation().getLongitude() )
                    .toString();
            initArea(data);

        }
    }

    private void initArea(String data){
        myLocation = new ArrayList<>();
        myLocation.add(data);

        FirebaseDatabase.getInstance().getReference("MyLocation").child("MyCity").setValue(myLocation).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

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
        if(mBound){
            unbindService( mServiceConnection );
            mBound = false;
        }
        super.onStop();
    }


}