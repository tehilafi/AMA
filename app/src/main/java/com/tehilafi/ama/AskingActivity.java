package com.tehilafi.ama;


import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.db.Users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.tehilafi.ama.not.NotificationSender.sendNotification;


public class AskingActivity extends Activity {

    private Button send;
    private TextView textViewTheLocation, edtTitle, edtContent;
    private String iduser, latLngString, kmInLongitudeDegree;
    private CheckBox checkBox;
    private String important_questions = "false";
    private int score;

    private DatabaseReference reff, reffUser;
    private FirebaseAuth mAuth;
    Users users;
    Question question;
    public static long counter = 0;

    private static final String KEY_ID = "id";

    public static final String TAG = "MyTag";

   ArrayList<String> tokens = new ArrayList<String>();


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

        // Init
        mAuth = FirebaseAuth.getInstance();
        users = new Users();
        reffUser = FirebaseDatabase.getInstance().getReference( "Users" );

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

                Query myQuery = reffUser;
                myQuery.addChildEventListener( new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String token = snapshot.getValue( Users.class ).getToken();
                        int idd = snapshot.getValue( Users.class ).getId();
                        Log.d(TAG, "the token = " + token);
                        Double lat = snapshot.getValue( Users.class ).getLatitude();
                        Double lng = snapshot.getValue( Users.class ).getLongitude();

                        if (iduser.equals( String.valueOf(idd)))
                            score =  snapshot.getValue( Users.class ).getScore();

                        // split latLngString to longitude and latitude
                        String[] s = latLngString.split("\\(");
                        String s1 = s[1];
                        s = s1.split("\\,");
                        String la = s[0];
                        String ln = s[1].substring(0, s[1].length() - 1);
                        // Check if the location is within range
                        float[] results = new float[1];
                        Location.distanceBetween(Double.parseDouble(la), Double.parseDouble(ln), lat, lng, results );
                        float distanceInMeters = results[0];
                        if(distanceInMeters < 700){
                            Log.d(TAG, "the token in = " + token);
                            tokens.add(token);
                        }

                        int num_question;
                        boolean checkContent;
                        //If one of the details is missing:
                        if (edtContent.getText().toString().equals( "" )) {
                            Toast.makeText( AskingActivity.this, "Missing content", Toast.LENGTH_LONG ).show();
                            checkContent = false;
                        } else
                            checkContent = true;

                        if (checkContent) {
                            // send notification to tokens
                            Log.d(TAG, "array tokens = " + tokens);
                            sendNotification( AskingActivity.this, tokens, "try", "massege", "question" );

                            num_question= (int)counter;
                            // save in DB question
                            question.setLocation(getIntent().getStringExtra("Extra locations"));
                            question.setIdAsking( Integer.parseInt( iduser ) );
                            question.setContentQuestion( edtContent.getText().toString().trim() );
                            question.setNumQuestion(num_question +1);
                            question.setImportant_questions(important_questions);
                            question.setLatLngString(latLngString);
                            question.setSend_to_tokens(tokens);
                            question.setDateTimeQuestion(currentDateTime());
                            question.setNumLikes(0);
                            question.setNumComments(0);
                            question.setUsernameAsk(mPreferences.getString(getString(R.string.name), ""));

                            reff.child( String.valueOf( counter + 1 ) ).setValue( question );

                            int score_now = score + 4;
                            reffUser.child(iduser).child("score").setValue(score_now);




                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(AskingActivity.this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );


           }

        } );
    }

    public void itemClicked(View v) {
        important_questions = "true";
    }

    //  The function returns the current date and time
    public String currentDateTime(){
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date currentTime = Calendar.getInstance().getTime();
        String todayAsString = df.format(currentTime);

        return todayAsString;
    }

}
