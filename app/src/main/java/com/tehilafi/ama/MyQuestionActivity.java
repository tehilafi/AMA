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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.tehilafi.ama.db.Question;

import java.util.ArrayList;
import java.util.List;

public class MyQuestionActivity extends Activity {

    private Button Questions_I_was_asked;
    private ImageView profile;

    DatabaseReference reff;

    ListView listView;
    ListViewAdapte listViewAdapte;
    ArrayList<ListView_item> arrayList = new ArrayList<>();
    private List<String> items = new ArrayList<String>();

    private SharedPreferences mPreferences;

    public static final String TAG = "MyTag";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_quastions );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
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

        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        Questions_I_was_asked = findViewById( R.id.Questions_I_was_askedID);
        Questions_I_was_asked.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), MyAnswerActivity.class);
                startActivity(intent);
            }
        } );



        reff = FirebaseDatabase.getInstance().getReference("Questions");
        listView = (ListView)findViewById(R.id.listView2ID);
        listViewAdapte = new ListViewAdapte(this,R.layout.listview_my, arrayList);
        listView.setAdapter(listViewAdapte);

        String idUser = mPreferences.getString( getString( R.string.id), "" );
        Log.d(TAG, "idUser = " + idUser);
        //Query myQuery = reff.orderByChild("idAsking").equalTo(id_asking);
        Query myQuery = reff.orderByChild("idAsking");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                String location = snapshot.getValue( Question.class ).location();
                String idAsking = snapshot.getValue( Question.class ).id_user();
                String userName = snapshot.getValue( Question.class ).getUsernameAsk();


                if(idAsking.equals(idUser)){
                    arrayList.add( new ListView_item( R.drawable.photo_profile_start, userName , dateTime, location) );

                    listViewAdapte.notifyDataSetChanged();
                }

                Log.d(TAG, "arrayList = " + arrayList);

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
                Toast.makeText( MyQuestionActivity.this, "Extra numQuestion" + items.get( position ), Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(getBaseContext(), AnswerActivity.class);
                intent.putExtra( "Extra numQuestion", items.get( position ) );
                startActivity(intent);
            }
        } );



    }
}