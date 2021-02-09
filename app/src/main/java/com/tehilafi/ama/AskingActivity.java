package com.tehilafi.ama;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AskingActivity extends Activity {

    private Button send;
    private TextView textViewTheLocation, edtTitle, edtContent;
    private String iduser, latLngString, kmInLongitudeDegree;
    private CheckBox checkBox;
    private String important_questions = "false";

    DatabaseReference reff;
    Question question;
    long counter = 0;

    private static final String KEY_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_question );
        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        send = findViewById( R.id.btnSaveID );
        textViewTheLocation = findViewById( R.id.textViewTheLocationID );
        textViewTheLocation.setText( getIntent().getStringExtra( "Extra locations" ) );
        edtTitle = findViewById( R.id.edtTitleID );
        edtContent = findViewById( R.id.edtContentID);
        checkBox = findViewById( R.id.checkBoxID);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPreferences.edit();

        iduser =  mPreferences.getString(getString(R.string.id), "");
        if(iduser == "")
            iduser = "null";


        latLngString = mPreferences.getString(getString(R.string.location), "");
        if(latLngString == "")
            latLngString = "null";

        question = new Question();
        reff = FirebaseDatabase.getInstance().getReference().child( "Questions" );
        reff.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    counter = (snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int num_question;
                boolean checkContent;
                //If one of the details is missing:
                if (edtContent.getText().toString().equals( "" )) {
                    Toast.makeText( AskingActivity.this, "Missing content", Toast.LENGTH_LONG ).show();
                    checkContent = false;
                } else
                    checkContent = true;

                if (checkContent) {
                    num_question= (int)counter;
                    question.setLocation(getIntent().getStringExtra("Extra locations"));
                    question.setIdAsking( Integer.parseInt( iduser ) );
                    question.setTitleQuestion("title");
                    question.setContentQuestion( edtContent.getText().toString().trim() );
                    question.setNumQuestion(num_question +1);
                    question.setImportant_questions(important_questions);

                    //question.setLocation(latLngString);

                    reff.child( String.valueOf( counter + 1 ) ).setValue( question );

                    Calculation_coordinates(latLngString);

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(AskingActivity.this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();
            }

        } );
    }

    //private ArrayList<String> Calculation_coordinates(String latLngString){
    private void Calculation_coordinates(String latLngString){

        String latString = null;
        String lngString = null;
        String[] location = latLngString.split("\\,");
        latString = location[0];
        lngString = location[1];

        String userlat = "31.200";
        String userlng = "35.04";

        float[] results = new float[1];
       // Location.distanceBetween(latString,Double.parseDouble(lngString) , Double.parseDouble(userlat), Double.parseDouble(userlng), results);
        Location.distanceBetween(31.9325, 35.0423, 31.9, 35.0, results);
        float distance = results[0];

        Toast.makeText( this, "#####" + distance+  "#####"  , Toast.LENGTH_SHORT ).show();
//        return latLngStrings;
    }

    private void sendNotification(String latLngString) {
    }


    public void itemClicked(View v) {
        important_questions = "true";
    }




    }
