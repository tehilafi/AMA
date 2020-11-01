package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class AnsweringActivity extends AppCompatActivity {

    Button send;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_answering );
            // Hide the Activity Status Bar
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
            // Hide the Activity  Bar
            try {
                this.getSupportActionBar().hide();
            } catch (NullPointerException e) {
            }

            send = findViewById(R.id.sendID);
            send.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);

                }
            } );

        }
    }



