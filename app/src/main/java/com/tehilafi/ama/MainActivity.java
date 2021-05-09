package com.tehilafi.ama;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.tehilafi.ama.db.Users;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    GoogleMap mMap;
    SearchView searchView;
    SupportMapFragment mapFragment;
    Location currentLocation, currentLoc;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    static MainActivity instance;
    LocationRequest locationRequest;
    private Boolean isWithin0_5km = false;
    Boolean isFirstRun;

    Users users;

    private static final String KEY_locatoin = "q_location";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private DatabaseReference reff, reffLocation;
    CurrentLocation locationDB;
    private FirebaseAuth mAuth;

    String locationToAddQuestion;
    String id_user;
    String idUser;

    ImageView requestLocation, removeLocation;
    ImageView add_location, my_question, mainbtn;
    private ImageView profile;

    public static final String TAG = "MyTag";


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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(final ListViewAdapte.SendLocationToActivity event) {
        if (event != null) {
            String data = new StringBuilder()
                    .append( event.getLocation().getLatitude() )
                    .append( "/" )
                    .append( event.getLocation().getLongitude() )
                    .toString();
            // Save locations in DB
            saveLocation( event.getLocation().getLatitude(), event.getLocation().getLongitude() );
        }
    }

    // Save current locations in users DB
    private void saveLocation(final double latitude, final double longitude) {

        Log.d( TAG, "in: saveLocation " );

        Toast.makeText( mService, latitude + " / " + longitude, Toast.LENGTH_SHORT ).show();
        users = new Users();
        reff = FirebaseDatabase.getInstance().getReference( "Users" );
        String id = mPreferences.getString( getString( R.string.id ), "" );
        reff.child( id ).child( "latitude" ).setValue( latitude );
        reff.child( id ).child( "longitude" ).setValue( longitude );

        // Check if the location is within range
//        float[] results = new float[1];
//        Location.distanceBetween( 31.925009, 35.04158, latitude, longitude, results );
//        float distanceInMeters = results[0];
//        boolean isWithin05km = distanceInMeters < 500;
////        if (isWithin05km) {
//            Log.d( TAG, "isWithin05km : YES" );
//            isWithin0_5km = true;
            // Creating the topic for the Device
//            FirebaseMessaging.getInstance().subscribeToTopic( "/topics/t-topic" ).addOnCompleteListener( new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    Toast.makeText( getApplicationContext(), "Received", Toast.LENGTH_LONG ).show();
//                    if (task.isSuccessful())
//                        Log.d( TAG, "isWithin0_5km : Topic Subscribed" );
//                    else
//                        Log.d( TAG, "isWithin0_5km : Subscribed failed" );
//                }
//            } );
//        } else {
//            Log.d( TAG, "isWithin05km : NO" );
//            isWithin0_5km = false;
//            // Disabling the device's topic
//            FirebaseMessaging.getInstance().unsubscribeFromTopic( "t-topic" );
//        }

    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        //**********************************************************************************
        FirebaseMessaging.getInstance().subscribeToTopic( "/topics/t-topic" ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText( getApplicationContext(), "Received", Toast.LENGTH_LONG ).show();
                    if (task.isSuccessful())
                        Log.d( TAG, "Topic Subscribed" );
                    else
                        Log.d( TAG, "Topic Subscribed failed" );
                }
            } );




//***************************************************************************************************************
        // to first time of login the app
        Log.d( TAG, "isFirstRun:" + isFirstRun);
        isFirstRun = getSharedPreferences( "PREFERENCE", MODE_PRIVATE ).getBoolean( "isFirstRun", true );
        if (isFirstRun) {
            //show login activity
            startActivity( new Intent( MainActivity.this, LoginActivity.class ) );

        }
//***************************************************************************************************************

        getSharedPreferences( "PREFERENCE", MODE_PRIVATE ).edit()
                .putBoolean( "isFirstRun", false ).commit();

        idUser = getIntent().getStringExtra( "Extra id" );
        Toast.makeText( MainActivity.this, idUser, Toast.LENGTH_LONG );

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.google_map );
        mapFragment.getMapAsync( this );
        // Initialize fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        // Check permission
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }

        instance = this;
        searchView = findViewById( R.id.search_location );

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        mEditor = mPreferences.edit();

        mAuth = FirebaseAuth.getInstance();
        reff = FirebaseDatabase.getInstance().getReference( "Users" );

        /////////////////////////////////////////////////////////////////////////////////////
        // Activity transitions
        // Moves to the activity of viewing my questions and uswers
        my_question = findViewById( R.id.my_questionID );
        my_question.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), MyAnswerActivity.class );
                startActivity( intent );
            }
        } );

        // Moves to the activity of watching the previous questions
        mainbtn = findViewById( R.id.mainID );
        mainbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), AskQuestionActivity.class );
                startActivity( intent );
            }
        } );

        // Moves to activity of profile
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), ChangProfilActivity.class );
                startActivity( intent );
            }
        } );

//********************************************************************************************************************************//
        // Disabling the device's topic
//        FirebaseMessaging.getInstance().unsubscribeFromTopic( "t-topic" );
//        Log.d( TAG, "unsubscribeFromTopic:" );

//********************************************************************************************************************************//

        // for the search location
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals( "" )) {

                    try {
                        Geocoder geocoder = new Geocoder( MainActivity.this );
                        addressList = geocoder.getFromLocationName( location, 3 );
                        Address address = addressList.get( 0 );
                        LatLng latLng = new LatLng( address.getLatitude(), address.getLongitude() );
                        String latLngString = String.valueOf( latLng );
                        Toast.makeText( getApplicationContext(), "****" + latLng + "latLngString", Toast.LENGTH_LONG ).show();
                        mEditor.putString( getString( R.string.location ), latLngString );
                        mEditor.commit();

                        mMap.addMarker( new MarkerOptions().position( latLng ).title( location ) );
                        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 10 ) );

                        locationToAddQuestion = location;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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

        // Moves to activity of asking questions by location
        add_location = findViewById( R.id.add_locationID );
        add_location.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationToAddQuestion != null || !locationToAddQuestion.equals( "" )) {
                    Intent intent = new Intent( getBaseContext(), AskingActivity.class );
                    if (locationToAddQuestion == null)
                        locationToAddQuestion = "qqqqq";
                    intent.putExtra( "Extra locations", locationToAddQuestion );
                    intent.putExtra( "Extra id", id_user );
                    startActivity( intent );
                } else
                    Toast.makeText( getApplicationContext(), "Enret locattion", Toast.LENGTH_LONG ).show();
            }
        } );


        Dexter.withActivity( this ).withPermissions( Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) )
                .withListener( new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {


                        requestLocation = findViewById( R.id.requesr_location_updates_button );
                        removeLocation = findViewById( R.id.remove_location_updates_button );

                        requestLocation.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mService.requestLocationUpdates();
                            }
                        } );
                        removeLocation.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mService.removeLocationUpdates();
                            }
                        } );

                        setButtonState( ListViewAdapte.Common.requestingLocationUpdates( MainActivity.this ) );
                        bindService( new Intent( MainActivity.this,
                                MyBackgroundService.class ), mServiceConnection, Context.BIND_AUTO_CREATE );
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                } ).check();

        //getUserinfo();

    }

    private void getUserinfo() {

        reff.child( mAuth.getCurrentUser().getUid() ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    //String name = snapshot.child("name").getValue().toString();
                    if (snapshot.hasChild( "image" )) {
                        String image = snapshot.child( "image" ).getValue().toString();
                        Picasso.with( MainActivity.this ).load( image ).into( profile );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }


    // To see the current location on the map
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkSelfPermission : No");
        }
        else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener( new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        mapFragment.getMapAsync( new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );
                                //Create marker options
                                MarkerOptions markerOptions = new MarkerOptions().position( latLng ).title( "I am here" );
                                // Zoom map
                                googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 10 ) );
                                // Add marker on map
                                googleMap.addMarker( markerOptions );
                            }
                        } );
                    }
                }
            } );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener( this );
        EventBus.getDefault().register( this );
    }

    @Override
    protected void onStop() {
        if(mBound){
            unbindService( mServiceConnection );
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences( this ).unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister( this );
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals( ListViewAdapte.Common.KEY_REQUESTING_LOCATION_UPDATES))
            setButtonState(sharedPreferences.getBoolean( ListViewAdapte.Common.KEY_REQUESTING_LOCATION_UPDATES,false));

    }

    private void setButtonState(boolean isRequestEnable){
        if(isRequestEnable)
        {
            requestLocation.setEnabled( false );
            removeLocation.setEnabled(  true);
        }
        else{
            requestLocation.setEnabled( true );
            removeLocation.setEnabled(  false);
        }
    }



//    // Check if the location is within range
//    private void locationWithinRange(final double latitude, final double longitude){
//
//        float[] results = new float[1];
//        Location.distanceBetween(31.925009, 35.04158, latitude, longitude, results);
//        float distanceInMeters = results[0];
//        boolean isWithin10km = distanceInMeters < 10000;
//        if(isWithin10km)
//            Log.d(TAG, "locationWithinRange : YES");
//        Log.d(TAG, "locationWithinRange : NO");
//    }



    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
//        LatLng latLng = new LatLng(-34, 151); //new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am Here");
//        mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    fetchLastLocation();
                }
                break;
        }
    }
}