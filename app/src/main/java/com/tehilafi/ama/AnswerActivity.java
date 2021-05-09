package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tehilafi.ama.db.Answer;
import com.tehilafi.ama.db.Users;
import com.tehilafi.ama.media.AddvideoActivity;
import com.tehilafi.ama.media.PotoActivity;

public class AnswerActivity extends Activity {

    private TextView txvname, txvrating, txvLocation, txvquestion;
    private EditText edtContent;
    private Button btnSave;
    private String iduser;
    private ImageView  add_pic, add_video;


    long counter = 0;
    private boolean important_answer = false;
    DatabaseReference reff, reffuser;
    Answer answer;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_answer );
        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        txvname = findViewById(R.id.txvnameID);
        txvname.setText("user name");

        txvrating = findViewById(R.id.txvratingID);
        txvrating.setText("Rating 2");

        txvLocation = findViewById(R.id.txvLocationID);
        txvLocation.setText( getIntent().getStringExtra( "Extra locations" ) );

        txvquestion = findViewById(R.id.txvquestionID);
        txvquestion.setText( getIntent().getStringExtra( "Extra content" ) );

        edtContent = findViewById(R.id.edtContentID);
        // For add image or video //
        add_pic = findViewById(R.id.add_picID);
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnswerActivity.this, PotoActivity.class));
            }
        });
        add_video = findViewById(R.id.add_videoID);
        add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnswerActivity.this, AddvideoActivity.class));
            }
        });
        // *** //

        if(getIntent().getStringExtra( "Extra important_questions" ) == "true")
            important_answer = true;

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPreferences.edit();

        iduser =  mPreferences.getString(getString(R.string.id), "");
        if(iduser == "")
            iduser = "null";

        answer = new Answer();

        reffuser = FirebaseDatabase.getInstance().getReference().child("Users");

        reff = FirebaseDatabase.getInstance().getReference().child( "Answers");
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

        btnSave = findViewById(R.id.btnSaveID);
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numAnswer;
                boolean checkContent;
                //If the answer is missing:
                if (edtContent.getText().toString().equals( "" )) {
                    Toast.makeText( AnswerActivity.this, "Missing Answer", Toast.LENGTH_LONG ).show();
                    checkContent = false;
                } else
                    checkContent = true;
                if (checkContent) {
                    numAnswer= (int)counter;

                    answer.setIdAnswering(Integer.parseInt( iduser ));
                    answer.setContentAnswer(edtContent.getText().toString().trim());
                    answer.setNumQuestion(Integer.parseInt(getIntent().getExtras().getString("Extra numQuestion")));
                    answer.setNumAnswer(numAnswer +1);

                    reff.child( String.valueOf( counter + 1 ) ).setValue( answer );


                }


            }
        } );

//        if(important_answer = true)
//            //Query myQuery = reffuser.orderByChild("id").equalTo(iduser);
//            Query mQuery = reffuser.orderByChild("id").equalTo(iduser);
//            myQuery.addChildEventListener( new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                content = snapshot.getValue( Question.class ).content();
//                title = snapshot.getValue( Question.class ).title();
//                location = snapshot.getValue( Question.class ).location();
//                numQuestion = snapshot.getValue( Question.class ).numQuestion();
//                id_user = snapshot.getValue( Question.class ).id_user();
//                importantQuestions = snapshot.getValue( Question.class).important_questions();
//
//                arrayList.add(new ListView_item(R.drawable.photo_profile_start, title, content ));
//                listViewAdapte.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        } );



    }
}
