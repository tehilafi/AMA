package com.tehilafi.ama;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class AskQuestionActivity extends Activity {

    private TextView txvLocation;
    private String locationQuestion;

    DatabaseReference reff;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();

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

            txvLocation = findViewById( R.id.txvLocationID );
            if (getIntent().getStringExtra( "Extra locations" ) == null)
                txvLocation.setText("facaza");
            txvLocation.setText( getIntent().getStringExtra( "Extra locations" ) );


            final String locationToAddQuestion = txvLocation.getText().toString();

            reff = FirebaseDatabase.getInstance().getReference("Questions");
            listView = (ListView)findViewById(R.id.listView1ID);
            //arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
            arrayAdapter = new ArrayAdapter<String>(this,R.layout.my_listview_item, arrayList);

            listView.setAdapter(arrayAdapter);
            Query myQuery = reff.orderByChild("location").equalTo("israel");
            myQuery.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String value = snapshot.getValue( Question.class ).title();
                    arrayList.add( value );
                    arrayAdapter.notifyDataSetChanged();
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



