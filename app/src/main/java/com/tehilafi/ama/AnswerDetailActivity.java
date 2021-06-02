package com.tehilafi.ama;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.Nullable;
import com.tehilafi.ama.db.Answer;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.db.Users;
import com.tehilafi.ama.lists.ListViewAdapteDetail;
import com.tehilafi.ama.lists.ListView_item_detail;

import java.util.ArrayList;
import java.util.List;

public class AnswerDetailActivity extends Activity {

    private TextView txvname, txvdateTime, txvLocation, txvquestion;
    private EditText edtContent;
    private Button btnSave;
    private String iduser;
    private ImageView add_pic, add_video;
    private String num_question;
    private RatingBar myRating;
    private int numAns = 0;

    public static final String TAG = "MyTag";

    ListView listView;
    ListViewAdapteDetail listViewAdapteDetail;
    ArrayList<ListView_item_detail> arrayList = new ArrayList<>();
    private List<String> items = new ArrayList<String>();


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
        setContentView( R.layout.activity_answer_detail );
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
                String numberQuestion = snapshot.getValue( Question.class ).numQuestion();
                if(numberQuestion.equals(num_question)){
                    String content = snapshot.getValue( Question.class ).content();
                    String location = snapshot.getValue( Question.class ).location();
                    String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                    String userName = snapshot.getValue( Question.class ).getUsernameAsk();
                    id_asking = snapshot.getValue( Question.class ).id_user();
                    String importantQuestions = snapshot.getValue( Question.class ).important_questions();

                    txvname = findViewById(R.id.txvnameID);
                    txvname.setText(userName);
                    txvdateTime = findViewById(R.id.txvdateTimeID);
                    txvdateTime.setText(dateTime);
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


// *******************************  Get the data from the Answer DB and put them in list  *******************************
        reffAnswer = FirebaseDatabase.getInstance().getReference("Answers");
        listView = (ListView)findViewById(R.id.listView1ID);
        listViewAdapteDetail = new ListViewAdapteDetail(this,R.layout.listview_detils, arrayList);
        listView.setAdapter(listViewAdapteDetail);

        Query myQuery2 = reffAnswer.orderByChild("numQuestion");
        myQuery2.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                num_question = getIntent().getStringExtra("Extra numQuestion");
                String numberQuestion = snapshot.getValue( Question.class ).numQuestion();
                if(numberQuestion.equals(num_question)){
                    String contentAns = snapshot.getValue( Answer.class ).getContentAnswer();
                    String dateTime = snapshot.getValue( Answer.class ).getDateTimeAnswer();
                    String userName = snapshot.getValue( Answer.class ).getUserNameAns();
                    int id_answer = snapshot.getValue(Answer.class ).getIdAnswering();
                    int numLikes = snapshot.getValue( Answer.class ).getNumLikes();
                    int numDislike = snapshot.getValue( Answer.class ).getNumLikes();

                    numAns+=1;

                    arrayList.add( new ListView_item_detail( R.drawable.photo_profile_start, userName, dateTime,  contentAns, Integer.toString(numLikes), Integer.toString(numDislike), R.drawable.add_video, R.drawable.add_pic));
                    listViewAdapteDetail.notifyDataSetChanged();


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


    }

}


