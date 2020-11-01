package com.tehilafi.ama;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AskingActivity extends AppCompatActivity {

    private Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_asking );
        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide the Activity  Bar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        send = findViewById(R.id.sendID);
        send.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Toast.makeText( AskingActivity.this, "The message sent ", Toast.LENGTH_LONG ).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);

            }
        } );


    }
}
