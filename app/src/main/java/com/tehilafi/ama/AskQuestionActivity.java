package com.tehilafi.ama;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
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
    ListViewAdapte listViewAdapte;
    ArrayList<ListView_item> arrayList = new ArrayList<>();
    public static final String TAG = "MyTag";



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
            txvLocation.setText( getIntent().getStringExtra( "Extra locations" ) );

            final String locationToAddQuestion = txvLocation.getText().toString();

            reff = FirebaseDatabase.getInstance().getReference("Questions");
            listView = (ListView)findViewById(R.id.listView1ID);
            listViewAdapte = new ListViewAdapte(this,R.layout.my_listview_item, arrayList);
            listView.setAdapter(listViewAdapte);

            Query myQuery = reff.orderByChild("location");
            myQuery.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String location = snapshot.getValue( Question.class ).location();
                    Log.d(TAG, "location = " + location);
                    if(location.equals(getIntent().getStringExtra( "Extra locations" ))){
                        String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                        String idAsking = snapshot.getValue( Question.class ).id_user();

                        arrayList.add( new ListView_item( R.drawable.photo_profile_start, idAsking, dateTime,  location ) );
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

        }

}



