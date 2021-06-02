package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.lists.ListViewAdapte;
import com.tehilafi.ama.lists.ListView_item;

import java.util.ArrayList;
import java.util.List;

public class AskQuestionActivity extends Activity {

    private TextView txvLocation;
    private String locationQuestion;

    DatabaseReference reff;

    ListView listView;
    ListViewAdapte listViewAdapte;
    ArrayList<ListView_item> arrayList = new ArrayList<>();
    private List<String> items = new ArrayList<String>();

    public static final String TAG = "MyTag";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    String id_user;

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

            txvLocation = findViewById( R.id.txvLocationID );
            txvLocation.setText( getIntent().getStringExtra( "Extra locations" ) );

            final String locationToAddQuestion = txvLocation.getText().toString();

            reff = FirebaseDatabase.getInstance().getReference("Questions");
            listView = (ListView)findViewById(R.id.listView1ID);
            listViewAdapte = new ListViewAdapte(this,R.layout.listview_pre, arrayList);
            listView.setAdapter(listViewAdapte);


            Query myQuery = reff.orderByChild("location");
            myQuery.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String location = snapshot.getValue( Question.class ).location();
                    if(location.equals(getIntent().getStringExtra( "Extra locations" ))){
                        String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                        String nameUser = snapshot.getValue( Question.class ).getUsernameAsk();
                        String text = snapshot.getValue( Question.class ).getContentQuestion();
                        String numQuestion = snapshot.getValue( Question.class ).numQuestion();


                        arrayList.add( new ListView_item( R.drawable.photo_profile_start, nameUser, dateTime,  text , R.drawable.with_answer ) );
                        items.add( numQuestion );
                        listViewAdapte.notifyDataSetChanged();
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
                            txvLocation.setText( getIntent().getStringExtra( "Extra locations" ) );
                            break;

                        case R.id.my_questionID:
                            String my_token = mPreferences.getString( getString( R.string.myToken ), "" );
                            Log.d(TAG, "my_token = " + my_token );
                            intent = new Intent( getBaseContext(), MyAnswerActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.add_locationID:
                            if (getIntent().getStringExtra( "Extra locations" ) != null || !getIntent().getStringExtra( "Extra locations" ).equals( "" )) {
                                intent = new Intent( getBaseContext(), AskingActivity.class );
                                intent.putExtra( "Extra locations", getIntent().getStringExtra( "Extra locations" ) );
                                intent.putExtra( "Extra id", id_user );
                                startActivity( intent );
                            }
                            break;
                    }
                    return true;
                }
            };
// *******************************  End NavBar  *******************************

}



