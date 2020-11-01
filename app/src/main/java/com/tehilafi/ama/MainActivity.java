package com.tehilafi.ama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener{


    GoogleMap mMap;
    SearchView searchView;
    SupportMapFragment mapFragment;

    Button requestLocation, removeLocation;
    ImageView add_location, my_question, questionSentToMe;
    CircleImageView profile;
    private static final String TAG = "AskquestionActivity";


    MyBackgroundService mService = null;
    boolean mBound = false;
    private  final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };



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

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        instance = this;
        searchView = findViewById( R.id.search_location );

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.google_map );
        mapFragment.getMapAsync( this );
        /////////////////////////////////////////////////////////////////////////////////////
        // Activity transitions

        // Moves to the activity of viewing my previous questions
        my_question = findViewById( R.id.my_questionID );
        my_question.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOfmy();
            }
        } );

        //Moves to the window of watching the questions I was asked
        questionSentToMe = findViewById( R.id.questionSentToMeID );
        questionSentToMe.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), QuestionSentToMeActivity.class);
                startActivity(intent);
            }
        } );

        // Moves to activity of asking questions by location
        add_location = findViewById( R.id.add_locationID );
        add_location.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOfAsk();
            }
        } );

        // Moves to activity of profile
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), ProfilActivity.class);
                startActivity(intent);
            }
        } );

        ///////////////////////////////////////////////////////////////////////////////////////
        // for the search location
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals( "" )) {
                    ////////
                    String fullAddress = "";
                    ///////
                    Geocoder geocoder = new Geocoder( MainActivity.this );
                    try {
                       // List<Address> addressesList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        addressList = geocoder.getFromLocationName( location, 1 );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);

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


        Dexter.withActivity(this).withPermissions( Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ))
                .withListener(new MultiplePermissionsListener()
                {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        requestLocation = (Button)findViewById( R.id.requesr_location_updates_button );
                        removeLocation = (Button)findViewById( R.id.remove_location_updates_button );

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

                        setButtonState(Common.requestingLocationUpdates(MainActivity.this));
                        bindService(new Intent(MainActivity.this,
                                        MyBackgroundService.class),
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE);
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();

    }
    ////////////////////////////////////////////////////////////////////////////////////////
    // for the dialog of ask question
    void showDialogOfAsk(){
        LayoutInflater inflater = LayoutInflater.from( this );
        View view = inflater.inflate(R.layout.activity_askquestion, null );

        Button ask = view.findViewById( R.id.askID);
        Button viewQ = view.findViewById( R.id.viewID);

        ask.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), AskingActivity.class);
                startActivity(intent);
            }
        } );

        viewQ.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), PreAskingActivity.class);
                startActivity(intent);
            }
        } );

        AlertDialog alertDialog = new AlertDialog.Builder( this ).setView(view).create();
        alertDialog.show();
    }

    // for the dialog of my questions and answers
    void showDialogOfmy(){
        LayoutInflater inflater = LayoutInflater.from( this );
        View view = inflater.inflate(R.layout.activity_my, null );

        Button myQuestions = view.findViewById( R.id.my_questionID);
        Button myAnswers = view.findViewById( R.id.my_answerID);

        myQuestions.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), MyQuestionActivity.class);
                startActivity(intent);
            }
        } );

        myAnswers.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), MyAnswerActivity.class);
                startActivity(intent);
            }
        } );

        AlertDialog alertDialog = new AlertDialog.Builder( this ).setView(view).create();
        alertDialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////

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
        if(s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
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

    @Subscribe(sticky = true, threadMode =  ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event){
        if(event != null){
            String data = new StringBuilder()
                    .append( event.getLocation().getLatitude() )
                    .append( "/" )
                    .append( event.getLocation().getLongitude() )
                    .toString();
            Toast.makeText( mService, data, Toast.LENGTH_SHORT ).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }


}