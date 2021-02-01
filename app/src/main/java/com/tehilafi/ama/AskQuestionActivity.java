package com.tehilafi.ama;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AskQuestionActivity extends AppCompatActivity {

    private TextView txvLocation;
    private Button btnNewquestion;
    private String locationQuestion;

    DatabaseReference reff;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_askquestion);

            // Hide the Activity Status Bar
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
            // Hide the Activity  Bar
            try {
                this.getSupportActionBar().hide();
            } catch (NullPointerException e) {
            }

            txvLocation = findViewById( R.id.txvLocationID );
            txvLocation.setText(getIntent().getStringExtra("Extra locations"));

            final String locationToAddQuestion = txvLocation.getText().toString();

            //Moves to asking activity - add question
            btnNewquestion = findViewById( R.id.btnNewquestionID );
            btnNewquestion.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(getBaseContext(), AskingActivity.class);
                    intent.putExtra( "Extra locations", locationToAddQuestion);
                    startActivity( intent );
                }
            });


            reff = FirebaseDatabase.getInstance().getReference("Questions");
            listView = (ListView)findViewById(R.id.listViewtxt);
            arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);
            Query myQuery = reff.orderByChild("location").equalTo("קניון עזריאלי");
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



