package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.tehilafi.ama.db.Answer;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.db.Users;
import com.tehilafi.ama.lists.ListViewAdapteDetail;
import com.tehilafi.ama.lists.ListView_item_detail;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnswerDetailActivity extends Activity {

    private TextView txvname, txvdateTime, txvLocation, txvquestion, num_ansID;
    private String num_question;
//    private int numAns;
    public static final String TAG = "MyTag";
    private String id_asking;
    ArrayList<String> askingToken = new ArrayList<String>();
    public static long counter = 0;
    private boolean important_answer = false;
    DatabaseReference reff, reffAnswer, reffUser;
    int starKind;
    String id_user;
    ImageView imageView, starID, transillumination, closeID;
    String location;
    VideoView videoView;
    private SharedPreferences mPreferences;
    private StorageReference storageReff;
    private CircleImageView profile, profileuserID;
    private int numLikes, numL;
    private int count = 0;
    ListView listView;
    ListViewAdapteDetail listViewAdapteDetail;
    final ArrayList<ListView_item_detail> arrayList = new ArrayList<>();
    private List<String> items = new ArrayList<String>();
    FirebaseStorage storage;
    int stars;
    int score;
    int drawablePKind, drawableVKind;
    private PlayerView mExoplayerView;
    View mView;
    SimpleExoPlayer exoPlayer;

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

        // For navBar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageView = findViewById(R.id.imageViewID);
        videoView = findViewById(R.id.videoViewID);
        starID = findViewById(R.id.starID);
        profileuserID = findViewById(R.id.profileuserID);
        num_ansID = findViewById(R.id.num_ansID);
        transillumination = findViewById(R.id.transilluminationID);
        closeID = findViewById(R.id.closeID);
        closeID.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                closeID.setImageResource(R.drawable.transillumination);
                transillumination.setImageResource(R.drawable.transillumination);
                imageView.setImageResource(R.drawable.transillumination);
            }
        } );


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
                Glide.with( AnswerDetailActivity.this).load(downloadUrl).into(profile);
            }
        });



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
                    location = snapshot.getValue( Question.class ).location();
                    String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                    String userName = snapshot.getValue( Question.class ).getUsernameAsk();
                    id_asking = snapshot.getValue( Question.class ).id_user();
                    String importantQuestions = snapshot.getValue( Question.class ).getImportant_questions();
                    int numA = snapshot.getValue( Question.class ).getNumComments();
                    Log.d(TAG, "id_asking!! = " + id_asking);

//                  // Show profile image in profileuserID
                    storageReff.child("profile picture/").child(id_asking).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            Glide.with( AnswerDetailActivity.this).load(downloadUrl).into(profileuserID);
                        }
                    });

                    txvname = findViewById(R.id.txvnameID);
                    txvname.setText(userName);
                    txvdateTime = findViewById(R.id.txvdateTimeID);
                    txvdateTime.setText(dateTime);
                    txvLocation = findViewById(R.id.txvLocationID);
                    txvLocation.setText(location);
                    txvquestion = findViewById(R.id.txvquestionID);
                    txvquestion.setText(content);
                    num_ansID.setText(numA + " תשובות ");

                    //************************************* Get the score from Users DB  *************************************
                    reffUser = FirebaseDatabase.getInstance().getReference("Users").child( String.valueOf( id_asking ));
                    reffUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            starKind = R.drawable.star;
                            stars = dataSnapshot.getValue( Users.class).getScore();
                            if(stars < 150){
                                starKind = R.drawable.star1;
                            }
                            if(stars >= 150 && stars < 500){
                                starKind = R.drawable.star2;
                            }
                            if(stars >= 500){
                                starKind = R.drawable.star3;
                            }

                            Glide.with( AnswerDetailActivity.this).load(starKind).into(starID);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


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
                    numLikes = snapshot.getValue( Answer.class ).getNumLikes();

                    //************************************* Get the score from Users DB  *************************************
                    reffUser = FirebaseDatabase.getInstance().getReference("Users").child( String.valueOf( id_answer ));
                    reffUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int stars = dataSnapshot.getValue( Users.class).getScore();
                            if(stars < 150)
                                starKind = R.drawable.star1;
                            if(stars >= 150 && stars < 500)
                                starKind = R.drawable.star2;
                            if(stars >= 500)
                                starKind = R.drawable.star3;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    int numAns = snapshot.getValue( Answer.class ).getNumAnswer();

                    storageReff.child("picture answer/").child( String.valueOf( numAns ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            drawablePKind = R.drawable.add_pic;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            drawablePKind = R.drawable.transillumination;
                        }
                    });

                    storageReff.child("video answer/").child( String.valueOf( numAns ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            drawableVKind = R.drawable.add_video;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            drawableVKind = R.drawable.transillumination;
                        }
                    });

                    // Show the profile image in profileID
                    storageReff.child("profile picture/").child( String.valueOf( id_answer ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            arrayList.add( new ListView_item_detail( downloadUrl.toString(), userName, dateTime,  contentAns, Integer.toString(numLikes), drawableVKind, drawablePKind, starKind));
                            items.add( String.valueOf( numAns ) );

                            listViewAdapteDetail.notifyDataSetChanged();
                        }
                    });
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
        // *******************************  When click on one of the questions  *******************************
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText( AnswerDetailActivity.this, "Extra numAns" + items.get( position ), Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(getBaseContext(), OneAnswerActivity.class);
                intent.putExtra( "Extra numAnswer", items.get( position ) );
                startActivity(intent);
            }
        } );

    }




    // *******************************  For NavBar  *******************************
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.mainID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.preID:
                            intent = new Intent( getBaseContext(), AskQuestionActivity.class );
                            intent.putExtra( "Extra locations", location );
                            intent.putExtra( "Extra id", id_user );
                            startActivity( intent );
                            break;

                        case R.id.my_questionID:
                            String my_token = mPreferences.getString( getString( R.string.myToken ), "" );
                            Log.d(TAG, "my_token = " + my_token );
                            intent = new Intent( getBaseContext(), MyAnswerActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.add_locationID:
                            intent = new Intent( getBaseContext(), AskingActivity.class );
                            intent.putExtra( "Extra locations", location );
                            intent.putExtra( "Extra id", id_user );
                            startActivity( intent );
                            break;
                    }
                    return true;
                }
            };

}


