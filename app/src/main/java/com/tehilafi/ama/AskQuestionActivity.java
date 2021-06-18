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
import android.widget.ListView;
import android.widget.TextView;
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
import com.tehilafi.ama.lists.ListViewAdapte;
import com.tehilafi.ama.lists.ListView_item;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AskQuestionActivity extends Activity {

    private TextView txvLocation;
    private int idUser, stars;
    private int starKind;
    private StorageReference storageReff;
    FirebaseStorage storage;
    private CircleImageView profile;
    private Uri downloadUrlProfile;

    DatabaseReference reff, reffUser;

    ListView listView;
    ListViewAdapte listViewAdapte;
    ArrayList<ListView_item> arrayList = new ArrayList<>();
    private List<String> items = new ArrayList<String>();

    public static final String TAG = "MyTag";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_preasking);

            // Hide the Activity Status Bar
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
            // Hide the Activity  Bar
            try {
                this.getActionBar().hide();
            } catch (NullPointerException e) {
            }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        mEditor = mPreferences.edit();

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
                Glide.with( AskQuestionActivity.this).load(downloadUrl).into(profile);
            }
        });

        txvLocation = findViewById(R.id.txvLocationID);
        txvLocation.setText( mPreferences.getString(getString(R.string.searchLocation), ""));

        reff = FirebaseDatabase.getInstance().getReference("Questions");
        listView = (ListView)findViewById(R.id.listView1ID);
        listViewAdapte = new ListViewAdapte(this,R.layout.listview_pre, arrayList);
        listView.setAdapter(listViewAdapte);


//************************************* Looking for location questions from Questions DB  *************************************

        Query myQuery = reff.orderByChild("location");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int numComments;
                String location = snapshot.getValue( Question.class ).location();
                if(location.equals(mPreferences.getString(getString(R.string.searchLocation), ""))){
                    String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                    String nameUser = snapshot.getValue( Question.class ).getUsernameAsk();
                    String text = snapshot.getValue( Question.class ).getContentQuestion();
                    idUser = snapshot.getValue( Question.class ).getIdAsking();
                    numComments = snapshot.getValue( Question.class ).getNumComments();

//************************************* Get the score from Users DB  *************************************
                    reffUser = FirebaseDatabase.getInstance().getReference("Users").child( String.valueOf( idUser ));
                    reffUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    int numQuestion = snapshot.getValue( Question.class ).getNumQuestion();

                    // Show the profile image in profileID
                    storageReff.child("profile picture/").child( String.valueOf( idUser ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            if(numComments >= 1)
                                arrayList.add( new ListView_item( downloadUrl.toString(), nameUser, dateTime, text, R.drawable.with_answer, starKind) );
                            else
                                arrayList.add( new ListView_item( downloadUrl.toString(), nameUser, dateTime, text, R.drawable.transillumination, starKind) );

                            items.add( String.valueOf( numQuestion ));
                            listViewAdapte.notifyDataSetChanged();
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
                Toast.makeText( AskQuestionActivity.this, "Extra numQuestion" + items.get( position ), Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(getBaseContext(), AnswerDetailActivity.class);
                intent.putExtra( "Extra numQuestion", items.get( position ) );
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
                            break;
                            default:
                                throw new IllegalStateException( "Unexpected value: " + item.getItemId() );

                        case R.id.my_questionID:
                            intent = new Intent( getBaseContext(), MyAnswerActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.add_locationID:
                            intent = new Intent( getBaseContext(), AskingActivity.class );
                            startActivity( intent );
                            break;
                    }
                    return true;
                }
            };

}



