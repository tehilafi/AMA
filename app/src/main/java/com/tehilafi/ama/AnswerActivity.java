package com.tehilafi.ama;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.tehilafi.ama.db.Answer;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.db.Users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.tehilafi.ama.not.NotificationSender.sendNotification;

public class AnswerActivity extends Activity {

    private TextView txvname, txvdateTime, txvLocation, txvquestion;
    private EditText edtContent;
    private Button btnSave;
    private String iduser;
    private ImageView  add_pic, add_video;
    private String num_question;

    public static final String TAG = "MyTag";


    private String id_asking;
    ArrayList<String> askingToken = new ArrayList<String>();

    public static long counter = 0;
    private boolean important_answer = false;
    DatabaseReference reff, reffAnswer, reffUser;
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

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

// *******************************  Get the data from the Question DB  *******************************
        reff = FirebaseDatabase.getInstance().getReference("Questions");
        Query myQuery = reff.orderByChild("numQuestion");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                num_question = getIntent().getStringExtra("Extra numQuestion");
                Log.d(TAG, "Extra numQuestion" + num_question);
                String numberQuestion = snapshot.getValue( Question.class ).numQuestion();
                if(numberQuestion.equals(num_question)){
                    String content = snapshot.getValue( Question.class ).content();
                    String location = snapshot.getValue( Question.class ).location();
                    id_asking = snapshot.getValue( Question.class ).id_user();
                    String importantQuestions = snapshot.getValue( Question.class ).important_questions();

                    txvname = findViewById(R.id.txvnameID);
                    txvname.setText(mPreferences.getString(getString(R.string.name), ""));
                    txvdateTime = findViewById(R.id.txvdateTimeID);
                    txvdateTime.setText(currentDateTime());
                    txvLocation = findViewById(R.id.txvLocationID);
                    txvLocation.setText(location);
                    txvquestion = findViewById(R.id.txvquestionID);
                    txvquestion.setText(content);
                }

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


// *******************************  Get the token of device asking  *******************************
        reff = FirebaseDatabase.getInstance().getReference("Users");
        Query myQueryUser = reff.orderByChild("token");
        myQueryUser.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int idAsking = snapshot.getValue( Users.class ).getId();
                Log.d(TAG, "idAsking = " + idAsking);
                Log.d(TAG, "String.valueOf( idAsking )  = " + String.valueOf( idAsking ) );
                if (id_asking.equals( String.valueOf( idAsking ) ))
                    askingToken.add(snapshot.getValue( Users.class ).getToken());

                Log.d(TAG, "askingToken = " + askingToken);
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
// *******************************  Sava the answer data in the Answers DB  *******************************
        answer = new Answer();
        reff = FirebaseDatabase.getInstance().getReference().child( "Answers" );
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

        edtContent = findViewById( R.id.edtContentID );
        btnSave = findViewById( R.id.btnSaveID );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num_ans;
                boolean checkContent;
                if (edtContent.getText().toString().equals( "" )) {
                    Toast.makeText( AnswerActivity.this, "Missing Answer", Toast.LENGTH_LONG ).show();
                    checkContent = false;
                } else
                    checkContent = true;
                if (checkContent) {

                    // send notification to tokens of asking
                    sendNotification( AnswerActivity.this, askingToken, "try", "massege", "answer");

                    num_ans= (int)counter;
                    // save in DB question
                    answer.setIdAsking( Integer.parseInt(id_asking) );
                    answer.setContentAnswer( edtContent.getText().toString().trim() );
                    answer.setNumAnswer(num_ans +1);
                    answer.setNumQuestion(Integer.parseInt(num_question));
                    answer.setIdAnswering(Integer.parseInt(mPreferences.getString(getString(R.string.id), "")));
                    answer.setDateTimeAnswer(currentDateTime());
                    answer.setNumLikes(0);
                    answer.setNumComments(0);
                    answer.setUserNameAns(mPreferences.getString(getString(R.string.name), ""));


//                    answer.setSend_to_token( askingToken.get( 0 ) );

                    reff.child( String.valueOf( counter + 1 ) ).setValue(answer);
                }
            else
                Toast.makeText(AnswerActivity.this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();


            }
        });
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
