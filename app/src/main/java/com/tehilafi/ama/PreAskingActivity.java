package com.tehilafi.ama;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;

public class PreAskingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_preasking );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }


    }
}
