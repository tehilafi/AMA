package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class MyQuestionActivity extends Activity {

    private Button Questions_I_was_asked;
    private ImageView profile;
    String location;
    DatabaseReference reff, reffUser;
    private int starKind, stars;

    ListView listView;
    ListViewAdapteMy listViewAdapteMy;
    ArrayList<ListView_item_my> arrayList = new ArrayList<>();
    private List<String> items = new ArrayList<String>();

    private SharedPreferences mPreferences;

    private StorageReference storageReff;
    FirebaseStorage storage;

    public static final String TAG = "MyTag";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView( R.layout.activity_my_quastions );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
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

//*******************************  Activity transitions to Questions I was asked  *******************************
        Questions_I_was_asked = findViewById( R.id.Questions_I_was_askedID);
        Questions_I_was_asked.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), MyAnswerActivity.class);
                startActivity(intent);
            }
        } );


        listView = (ListView)findViewById(R.id.listView2ID);
        listViewAdapteMy = new ListViewAdapteMy(this,R.layout.listview_my, arrayList);
        listView.setAdapter(listViewAdapteMy);

        String idUser = mPreferences.getString( getString( R.string.id), "" );

//************************************* Looking for question that i asked from Questions DB  *************************************
        reff = FirebaseDatabase.getInstance().getReference("Questions");
        Query myQuery = reff.orderByChild("idAsking");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int numComments;
                final int[] starKind = new int[1];
                final int[] stars = new int[1];
                String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                location = snapshot.getValue( Question.class ).getLocation();
                String loc = snapshot.getValue( Question.class ).getLocation();
                String idAsking = String.valueOf( snapshot.getValue( Question.class ).getIdAsking() );
                String userName = snapshot.getValue( Question.class ).getUsernameAsk();
                String numQuestion = String.valueOf( snapshot.getValue( Question.class ).getNumQuestion() );
                numComments = snapshot.getValue( Question.class ).getNumComments();
                Boolean importantQuestions = snapshot.getValue( Question.class ).getImportant_questions();

                //************************************* Get the score from Users DB  *************************************
                reffUser = FirebaseDatabase.getInstance().getReference("Users");
                reffUser.child( String.valueOf( idAsking )).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        stars[0] = dataSnapshot.getValue( Users.class).getScore();
                        if(stars[0] < 150)
                            starKind[0] = R.drawable.star1;
                        if(stars[0] >= 150 && stars[0] < 500)
                            starKind[0] = R.drawable.star2;
                        if(stars[0] >= 500)
                            starKind[0] = R.drawable.star3;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                if(idAsking.equals(idUser)){
                    // Show the profile image in profileID
                    storageReff.child("profile picture/").child( String.valueOf( idAsking ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            String text_mark = "";
                            if(importantQuestions)
                                text_mark = " ! ";
                            if(numComments >= 1 )
                                    arrayList.add( new ListView_item_my( downloadUrl.toString(), userName, dateTime, String.valueOf(numQuestion), loc, R.drawable.with_answer, starKind[0], text_mark) );
                            else
                                    arrayList.add( new ListView_item_my( downloadUrl.toString(), userName, dateTime, String.valueOf(numQuestion), loc, R.drawable.transillumination, starKind[0], text_mark) );

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

                Intent intent = new Intent(getBaseContext(), AnswerDetailActivity.class);
                intent.putExtra( "Extra numQuestion", list1.getNumQ());
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