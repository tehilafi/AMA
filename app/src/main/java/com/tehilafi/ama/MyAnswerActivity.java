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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.tehilafi.ama.lists.ListViewAdapteMy;
import com.tehilafi.ama.lists.ListView_item_my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MyAnswerActivity extends Activity {

    private Button Questions_I_sent;
    private ImageView profile;
    private String id_user;
    private SharedPreferences mPreferences;
    private List<String> items = new ArrayList<String>();
    private StorageReference storageReff;
    FirebaseStorage storage;
    ArrayList<String> id_t =  new ArrayList<String>();
    private DatabaseReference reff, reffUser;
    ListView listView;
    ListViewAdapteMy listViewAdapteMy;
    ArrayList<ListView_item_my> arrayList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView( R.layout.activity_my_answers );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        // For navBar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

//*******************************  Activity transitions to profile  *******************************
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), ChangProfilActivity.class);
                startActivity(intent);
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
                Glide.with( getApplicationContext()).load(downloadUrl).into(profile);
            }
        });

//*******************************  Activity transitions to Questions I sent  *******************************
        Questions_I_sent = findViewById( R.id.Questions_I_sentID );
        Questions_I_sent.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), MyQuestionActivity.class );
                startActivity( intent );
            }
        } );

        listView = (ListView)findViewById(R.id.listView1ID);
        listViewAdapteMy = new ListViewAdapteMy(this,R.layout.listview_my, arrayList);
        listView.setAdapter(listViewAdapteMy);

//************************************* Looking for question that asked me from Questions DB  *************************************
        reff = FirebaseDatabase.getInstance().getReference("Questions");
        Query myQuery = reff.orderByChild("send_to_tokens");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int numComments;
                ArrayList<String> send_to_token = snapshot.getValue( Question.class ).getSend_to_tokens();
                if(send_to_token != null){
                    ArrayList<String> id_match_token = check_id_for_token(send_to_token);
                    if(id_match_token != null && id_match_token.contains(mPreferences.getString( getString( R.string.id ), "" ))){
                        String content = snapshot.getValue( Question.class ).getContentQuestion();
                        String loc = snapshot.getValue( Question.class ).getLocation();
                        String numQuestion = String.valueOf( snapshot.getValue( Question.class ).getNumQuestion() );
                        id_user = String.valueOf( snapshot.getValue( Question.class ).getIdAsking() );
                        String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                        String userName = snapshot.getValue( Question.class ).getUsernameAsk();
                        numComments = snapshot.getValue( Question.class ).getNumComments();
                        Boolean importantQuestions = snapshot.getValue( Question.class ).getImportant_questions();

                        final int[] starR = {R.drawable.star};

//************************************* Get the score from Users DB  *************************************
                        reffUser.child( String.valueOf( id_user )).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    starR[0] = dataSnapshot.getValue( Users.class ).StarRating();
                                }catch(Exception e){
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        // Show the profile image in profileID
                        storageReff.child("profile picture/").child( String.valueOf( id_user ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri downloadUrl)
                            {
                                String text_mark = "";
                                if(importantQuestions)
                                    text_mark = " ! ";
                                if(numComments >= 1)
                                    arrayList.add( new ListView_item_my( downloadUrl.toString(), userName, dateTime, String.valueOf(numQuestion), loc , R.drawable.with_answer, starR[0], text_mark) );
                                else
                                    arrayList.add( new ListView_item_my( downloadUrl.toString(), userName, dateTime, String.valueOf(numQuestion), loc, R.drawable.transillumination, starR[0], text_mark) );

                                Collections.sort( arrayList, new Comparator<ListView_item_my>(){
                                    @Override
                                    public int compare(ListView_item_my t1, ListView_item_my t2) {
                                        return Integer.parseInt(t2.getNumQ()) - Integer.parseInt(t1.getNumQ());
                                    }
                                });
                                listViewAdapteMy.notifyDataSetChanged();
                            }

                        });
                    }
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
                ListView_item_my list1 = arrayList.get(position);
                Intent intent = new Intent(getBaseContext(), AnswerActivity.class);
                intent.putExtra( "Extra numQuestion", list1.getNumQ());
                startActivity(intent);
            }
        } );
    }

    private  ArrayList<String> check_id_for_token(ArrayList<String> send_to_token) {
        id_t.clear();
        for(int i = 0; i <send_to_token.size(); i++) {
            String s = send_to_token.get( i );
            if(s!= null ) {
                String[] parts = s.split( "," );
                String id = parts[1];
                id_t.add( id );
            }
        }
        return id_t;
    }

    // *******************************  For NavBar  *******************************
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.preID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            Toast.makeText( getApplicationContext(), "הכנס מיקום לחיפוש", Toast.LENGTH_SHORT ).show();
                            break;

                        case R.id.mainID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.my_questionID:
                            break;
                        default:
                            throw new IllegalStateException( "Unexpected value: " + item.getItemId() );

                        case R.id.add_locationID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            Toast.makeText( getApplicationContext(), "הכנס מיקום לחיפוש", Toast.LENGTH_SHORT ).show();
                            break;
                    }
                    return true;
                }
            };
}
