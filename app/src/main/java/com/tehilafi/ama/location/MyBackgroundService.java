package com.tehilafi.ama.location;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tehilafi.ama.MainActivity;
import com.tehilafi.ama.R;
import com.tehilafi.ama.db.Users;

import org.greenrobot.eventbus.EventBus;

public class MyBackgroundService extends Service {


    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATIN = "com.tehilafi.myapplication"+ ".started_from_notification";
    private final IBinder mBinder = new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL = 1000*300; // Updated at location every 5 minutes
    private static final long FASTEST_UPDATE_INTERVAL_IN_MUL = UPDATE_INTERVAL_IN_MIL / 2;
    private static final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    Users users;
    private DatabaseReference reff;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public MyBackgroundService() {

    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult( locationResult );
                onNewLocation( locationResult.getLastLocation() );
            }
        };
        createLocationRequest();
        getLastLocation();

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        mEditor = mPreferences.edit();

        HandlerThread handlerThread = new HandlerThread( "EDMTDev" );
        handlerThread.start();
        mServiceHandler = new Handler( handlerThread.getLooper() );
//        mNotificationManager = (NotificationManager)getSystemService( NOTIFICATION_SERVICE );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel( CHANNEL_ID , getString( R.string.app_name ), NotificationManager.IMPORTANCE_DEFAULT);
//            mNotificationManager.createNotificationChannel( mChannel );
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification= intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATIN,false);
        if(startedFromNotification)
        {
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration=true;
    }

    public void removeLocationUpdates() {
        try{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Common.setLocationRequestingLocationUpdates(this,false);
            stopSelf();
        }catch(SecurityException ex){
            Common.setLocationRequestingLocationUpdates(this,true);
            Log.e("EDMT_DEV","Lost location permission. Could not remove Updates. "+ex);

        }
    }


    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful()&&task.getResult()!=null)
                                mLocation=task.getResult();
                            else
                                Log.e("EDMT_DEV","Failed to get location");
                        }
                    });
        }catch(SecurityException ex)
        {
            Log.e("EDMT_DEV","Lost location premission. "+ex);
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval( UPDATE_INTERVAL_IN_MIL ); // Set the time interval at which you want to update the location (60 sec)
        locationRequest.setFastestInterval( FASTEST_UPDATE_INTERVAL_IN_MUL ); // If another app uses the location we will get the location away in less time (30 sec)
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY ); // Appropriate for mapping applications that are showing your location in real-time.
    }

    private void onNewLocation(Location lastLocation) {
        mLocation = lastLocation;
        EventBus.getDefault().postSticky( new SendLocationToActivity(mLocation) );

        // Update notification content if running as a foreground service
        if(serviceIsRunningInForeGround(this)) {
//            mNotificationManager.notify( NOTI_ID, getNotification() );

            // *******************************  Save current locations in users DB  *******************************
            users = new Users();
            reff = FirebaseDatabase.getInstance().getReference( "Users" );
            String id = mPreferences.getString( getString( R.string.id ), "" );
            reff.child( id ).child( "latitude" ).setValue(Double.parseDouble(Common.getLatitudeText(mLocation)));
            reff.child( id ).child( "longitude" ).setValue(Double.parseDouble(Common.getLongitudeText(mLocation)));


        }


    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MyBackgroundService.class);
        String text = Common.getLocationText(mLocation);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATIN, true);
        PendingIntent servicePendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity( this, 0, new Intent(this, MainActivity.class),0 );

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this).addAction(R.drawable.logo, "Launch", activityPendingIntent).addAction(R.drawable.logo, "Remove",servicePendingIntent).setContentText( text ).setContentTitle( Common.getLocationTitle(this)).setOngoing( true ).setPriority( Notification.PRIORITY_HIGH ).setSmallIcon( R.mipmap.ic_launcher).setTicker( text).setWhen( System.currentTimeMillis() );

        // set the channel id for Android o
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId( CHANNEL_ID );
        }
        return builder.build();

    }

    private boolean serviceIsRunningInForeGround(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices( Integer.MAX_VALUE ))
            if(getClass().getName().equals( service.service.getClassName() ))
                if(service.foreground)
                    return true;
        return false;
    }


    public void requestLocationUpdates() {
        Common.setLocationRequestingLocationUpdates(this,true);
        startService(new Intent(getApplicationContext(),MyBackgroundService.class));
        try{
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

        }
        catch(SecurityException ex)
        {
            Log.e("EDMT_DEV","Lost location permission. Could not request it"+ex);

        }
    }


    public class LocalBinder extends Binder {
        public MyBackgroundService getService(){
            return MyBackgroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground( true );
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent){
        stopForeground( true );
        mChangingConfiguration = false;
        super.onRebind( intent );

    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!mChangingConfiguration&& Common.requestingLocationUpdates(this))
            startForeground(NOTI_ID,getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}