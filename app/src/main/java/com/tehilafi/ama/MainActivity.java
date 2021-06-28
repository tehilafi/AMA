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
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tehilafi.ama.db.Users;
import com.tehilafi.ama.location.Common;
import com.tehilafi.ama.location.MyBackgroundService;
import com.tehilafi.ama.location.SendLocationToActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener{
    GoogleMap mMap;
    SearchView searchView;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    static MainActivity instance;
    Boolean isFirstRun;
    Users users;
    String id;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DatabaseReference reff, reffUser;
    String locationToAddQuestion;
    String id_user;
    String idUser;
    ImageView requestLocation, removeLocation;
    private CircleImageView profile;
    public static final String TAG = "MyTag";
    private StorageReference storageReff;
    FirebaseStorage storage;
    private String location;


    // *******************************  Background Service  *******************************
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

// *******************************  Save current locations in users DB  *******************************
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(final SendLocationToActivity event) {
        if (event != null) {
            users = new Users();
            reff.child( id ).child( "latitude" ).setValue( event.getLocation().getLatitude() );
            reff.child( id ).child( "longitude" ).setValue( event.getLocation().getLongitude() );
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        // to first time of login the app
        Log.d( TAG, "isFirstRun:" + isFirstRun);
        isFirstRun = getSharedPreferences( "PREFERENCE", MODE_PRIVATE ).getBoolean( "isFirstRun", true );
        if (isFirstRun) {
            //show login activity
            startActivity( new Intent( MainActivity.this, LoginActivity.class ) );
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        mEditor = mPreferences.edit();

// ******************  A function that is run only once a week and keeps the number of answers the user answered  ******************
        reff = FirebaseDatabase.getInstance().getReference( "Users" );
        id = mPreferences.getString( getString( R.string.id ), "" );

        Timer timer = new Timer ();
        TimerTask t = new TimerTask () {
            @Override
            public void run () {
                reff.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int numAnswer = dataSnapshot.getValue( Users.class).getNumAnswer();
                        mEditor.putString( getString( R.string.numAnswer ), String.valueOf( numAnswer ) );
                        mEditor.commit();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };
        timer.schedule (t, 0l, 1000*60*60*168);

//******************* If the user answered more than 8 answers per week - the importantQuestions up. *******************
        reff.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numAnswerNow = dataSnapshot.getValue( Users.class).getNumAnswer();
                int importantQuestions = dataSnapshot.getValue( Users.class).getImportantQuestions();
                if(mPreferences.getString( getString( R.string.numAnswer ), "" ) == null || mPreferences.getString( getString( R.string.numAnswer ), "" ) == "") {
                    mEditor.putString( getString( R.string.numAnswer ), "0" );
                    mEditor.commit();
                }
                if(numAnswerNow -  Integer.parseInt(mPreferences.getString( getString( R.string.numAnswer ), "" )) >= 8)
                    reff.child( "importantQuestions" ).setValue(importantQuestions + 1);
                mEditor.putString( getString( R.string.numAnswer ), String.valueOf(numAnswerNow) );
                mEditor.commit();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
// *****************************************  End  *****************************************

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        // Show the profile image in profileID
        storageReff.child("profile picture/").child(mPreferences.getString( getString( R.string.id ), "" )).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Glide.with( getApplicationContext()).load(downloadUrl).into(profile);
            }
        });

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

//      *******************************  Search location in map  *******************************
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals( "" )) {
                    try {
                        Geocoder geocoder = new Geocoder( MainActivity.this );
                        addressList = geocoder.getFromLocationName( location, 3 );
                        if(addressList.size() > 0) {
                            Address address = addressList.get( 0 );
                            LatLng latLng = new LatLng( address.getLatitude(), address.getLongitude() );
                            String latLngString = String.valueOf( latLng );
                            Toast.makeText( getApplicationContext(), "* " + latLng + " *", Toast.LENGTH_LONG ).show();
                            mEditor.putString( getString( R.string.location ), latLngString );
                            mEditor.putString( getString( R.string.searchLocation ), location );

                            mEditor.commit();

                            mMap.addMarker( new MarkerOptions().position( latLng ).title( location ) );
                            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 10 ) );

                            locationToAddQuestion = location;
                        }
                        else
                            Toast.makeText( MainActivity.this, "כתובת לא תקינה", Toast.LENGTH_SHORT ).show();
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
//  *******************************  End search location in map  *******************************

//  *******************************  Activity transitions to profile  *******************************
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), ChangProfilActivity.class );
                startActivity( intent );
            }
        } );
//  *******************************  End activity transitions to profile *******************************


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

                        setButtonState( Common.requestingLocationUpdates( MainActivity.this ) );
                        bindService( new Intent( MainActivity.this,
                                MyBackgroundService.class ), mServiceConnection, Context.BIND_AUTO_CREATE );
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                } ).check();


    }

// *******************************  For NavBar  *******************************
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.preID:
                            if(location == null || locationToAddQuestion.equals( "" )) {
                                Toast.makeText( MainActivity.this, "הכנס מיקום לחיפוש", Toast.LENGTH_SHORT ).show();
                            }
                            else{
                                intent = new Intent( getBaseContext(), AskQuestionActivity.class );
                                startActivity( intent );
                                break;
                            }

                        case R.id.mainID:
                            break;
                            default:
                            throw new IllegalStateException( "Unexpected value: " + item.getItemId() );

                        case R.id.my_questionID:
                            intent = new Intent( getBaseContext(), MyQuestionActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.add_locationID:
                            if(location == null || locationToAddQuestion.equals( "" )) {
                                Toast.makeText( MainActivity.this, "הכנס מיקום לחיפוש", Toast.LENGTH_SHORT ).show();
                            }
                            else{
                                intent = new Intent( getBaseContext(), AskingActivity.class );
                                startActivity( intent );
                                break;
                            }
                    }
                    return true;
                }
            };
// *******************************  End NavBar  *******************************


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
        if(s.equals( Common.KEY_REQUESTING_LOCATION_UPDATES))
            setButtonState(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES,false));

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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}