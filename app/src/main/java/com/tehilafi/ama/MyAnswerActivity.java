package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.Nullable;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.lists.ListViewAdapteMy;
import com.tehilafi.ama.lists.ListView_item_my;

import java.util.ArrayList;
import java.util.List;


public class MyAnswerActivity extends Activity {

    private Button Questions_I_sent;
    private TextView newid;
    private ImageView profile;
    private String content, title, location, numQuestion, id_user, importantQuestions, dateTime, userName ;
    ArrayList<String> send_to_token = new ArrayList<String>();

    private SharedPreferences mPreferences;
    private List<String> items = new ArrayList<String>();

    public static final String TAG = "MyTag";


    DatabaseReference reff;

    ListView listView;
    ListViewAdapteMy listViewAdapteMy;
    ArrayList<ListView_item_my> arrayList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_answers );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        // Moves to activity of profile
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), ChangProfilActivity.class);
                startActivity(intent);
            }
        } );

        Questions_I_sent = findViewById( R.id.Questions_I_sentID );
        Questions_I_sent.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent( getBaseContext(), MyQuestionActivity.class );
                startActivity( intent );
            }
        } );

        newid = findViewById( R.id.newID );

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );


        reff = FirebaseDatabase.getInstance().getReference("Questions");
        listView = (ListView)findViewById(R.id.listView1ID);
        listViewAdapteMy = new ListViewAdapteMy(this,R.layout.listview_my, arrayList);
        listView.setAdapter(listViewAdapteMy);

        Query myQuery = reff.orderByChild("send_to_tokens");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String my_token = mPreferences.getString( getString( R.string.myToken ), "" );
                send_to_token = snapshot.getValue( Question.class ).getSend_to_tokens();
                Log.d(TAG, "send_to_token = " + send_to_token);
                Log.d(TAG, "myToken = " + my_token);
                if(send_to_token != null) {
                    if (send_to_token.contains( my_token )) {
                        content = snapshot.getValue( Question.class ).content();
                        location = snapshot.getValue( Question.class ).location();
                        numQuestion = snapshot.getValue( Question.class ).numQuestion();
                        id_user = snapshot.getValue( Question.class ).id_user();
                        importantQuestions = snapshot.getValue( Question.class ).important_questions();
                        dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                        userName = snapshot.getValue( Question.class ).getUsernameAsk();
                        //////////////////////////////////////////
                        String anew = "";
                        //////////////////////////////////////////

                        arrayList.add( new ListView_item_my( R.drawable.photo_profile_start, userName , dateTime, location, anew, R.drawable.with_answer));
                        items.add( numQuestion );

                        listViewAdapteMy.notifyDataSetChanged();
                    }
                }

                Log.d(TAG, "items = " + items);

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
                Toast.makeText( MyAnswerActivity.this, "Extra numQuestion" + items.get( position ), Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(getBaseContext(), AnswerActivity.class);
                intent.putExtra( "Extra numQuestion", items.get( position ) );
                startActivity(intent);
            }
        } );


    }

}
