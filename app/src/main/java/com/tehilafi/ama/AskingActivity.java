package com.tehilafi.ama;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.db.Users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tehilafi.ama.not.NotificationSender.sendNotification;


public class AskingActivity extends Activity {

    private Button send;
    private TextView textViewTheLocation, edtContent, impoetant;
    private String iduser, latLngString,  location_question;
    private Boolean important_questions = false;
    private CheckBox checkBox;
    private int score, numImportantQuestions;
    public static long counter = 0;
    private CircleImageView profile;
    private StorageReference storageReff;
    FirebaseStorage storage;
    private SharedPreferences mPreferences;
    private int num_question, counter_checkBox = 0;


    private DatabaseReference reff, reffUser;
    Users users;
    Question question;

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

        // For navBar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Init
        users = new Users();
        reffUser = FirebaseDatabase.getInstance().getReference( "Users" );
        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        send = findViewById( R.id.btnSaveID );
        textViewTheLocation = findViewById( R.id.textViewTheLocationID );
        edtContent = findViewById( R.id.edtContentID);
        checkBox = findViewById( R.id.checkBoxID);
        impoetant = findViewById( R.id.importantID);
        profile = findViewById(R.id.profileID);

//  *******************************  Activity transitions to profile  *******************************
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), ChangProfilActivity.class );
                startActivity( intent );
            }
        } );

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        // Show the profile image in profileID
        storageReff.child("profile picture/").child(mPreferences.getString( getString( R.string.id ), "" )).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Glide.with( AskingActivity.this).load(downloadUrl).into(profile);
            }
        });

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        iduser =  mPreferences.getString(getString(R.string.id), "");
        if(iduser == "")
            iduser = "null";

        latLngString = mPreferences.getString(getString(R.string.location), "");
        if(latLngString == "")
            latLngString = "null";

        location_question = mPreferences.getString(getString(R.string.searchLocation), "");
        if(location_question == "")
            location_question = "null";

        textViewTheLocation.setText(location_question);

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

//************************************  Get  data from Users DB  *************************************
        Query myQuery = reffUser;
        myQuery.addChildEventListener( new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               String token = snapshot.getValue( Users.class ).getToken();
               String myToken = "0";
               int idd = snapshot.getValue( Users.class ).getId();
               Double lat = snapshot.getValue( Users.class ).getLatitude();
               Double lng = snapshot.getValue( Users.class ).getLongitude();

               if (iduser.equals( String.valueOf( idd ) )) {
                   score = snapshot.getValue( Users.class ).getScore();
                   numImportantQuestions = snapshot.getValue( Users.class ).getImportantQuestions();
                   impoetant.setText(" נשארו לך " + numImportantQuestions + "  שאלות חשובות ");
                   if(numImportantQuestions <= 0)
                       checkBox.setEnabled(false);
                   myToken = snapshot.getValue( Users.class ).getToken();
               }

               // split latLngString to longitude and latitude
               String[] s = latLngString.split( "\\(" );
               String s1 = s[1];
               s = s1.split( "\\," );
               String la = s[0];
               String ln = s[1].substring( 0, s[1].length() - 1 );
               // Check if the location is within range
               float[] results = new float[1];
               Location.distanceBetween( Double.parseDouble( la ), Double.parseDouble( ln ), lat, lng, results );
               float distanceInMeters = results[0];
               if (distanceInMeters < 800) {
                   if(token != myToken)
                       tokens.add( token );
               }
           }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//************************************  Save the data in Questions DB  *************************************

        send.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtContent.getText().toString().equals( "" )) {

                    // send notification to tokens
                    Log.d(TAG, "tokens  send = " + tokens);
                    sendNotification( AskingActivity.this, tokens, "שאלה חדשה",  edtContent.getText().toString().trim(), "question", "MyAnswerActivity" );

                    num_question = (int) counter;
                    question.setLocation( mPreferences.getString( getString( R.string.searchLocation ), "" )  );
                    question.setIdAsking( Integer.parseInt( iduser ) );
                    question.setContentQuestion( edtContent.getText().toString().trim() );
                    question.setNumQuestion( num_question + 1 );
                    question.setImportant_questions(important_questions);
                    question.setLatLng( latLngString );
                    question.setSend_to_tokens( tokens );
                    question.setDateTimeQuestion( currentDateTime() );
                    question.setNumLikes( 0 );
                    question.setNumComments( 0 );
                    question.setUsernameAsk( mPreferences.getString( getString( R.string.name ), "" ) );
                    question.setNewQuestion(null);
                    reff.child( String.valueOf( counter + 1 ) ).setValue(question);

                    // Update score
                    int score_now = score += 4;
                    reffUser.child( iduser ).child( "score" ).setValue( score_now );

                    (new Handler()).postDelayed(this::continued, 1000);

                } else
                    Toast.makeText( AskingActivity.this, "כתוב את השאלה", Toast.LENGTH_LONG ).show();
            }

            private void continued() {
                Intent intent = new Intent( getBaseContext(), MainActivity.class );
                startActivity( intent );
            }
        });

    }

    // If marked the question as important
    public void checkBoxOnClick(View v) {
        important_questions = true;
        counter_checkBox ++;
        if(counter_checkBox % 2 == 0)
            numImportantQuestions = numImportantQuestions + 1;
        else
            numImportantQuestions = numImportantQuestions - 1;
        reffUser.child( iduser ).child( "importantQuestions" ).setValue( numImportantQuestions );
    }

    //  The function returns the current date and time
    public String currentDateTime(){
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date currentTime = Calendar.getInstance().getTime();
        String todayAsString = df.format(currentTime);

        return todayAsString;
    }


// *******************************  For NavBar  *******************************
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.preID:
                            intent = new Intent( getBaseContext(), AskQuestionActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.mainID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.my_questionID:
                            intent = new Intent( getBaseContext(), MyAnswerActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.add_locationID:
                            break;
                            default:
                                throw new IllegalStateException( "Unexpected value: " + item.getItemId() );
                    }
                    return true;
                }
            };
}
