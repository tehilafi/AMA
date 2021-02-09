package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAnswerActivity extends Activity {

    private Button Questions_I_sent;
    private TextView newid;
    private CircleImageView profile;
    private String content, title, location, numQuestion, id_user, importantQuestions;

    DatabaseReference reff;

    ListView listView;
    ListViewAdapte listViewAdapte;
    ArrayList<ListView_item> arrayList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_answers );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        Questions_I_sent = findViewById( R.id.Questions_I_sentID);
        Questions_I_sent.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), MyQuestionActivity.class);
                startActivity(intent);
            }
        } );
        newid = findViewById( R.id.newID);

        // Moves to activity of profile
        profile = findViewById( R.id.profileID );
        profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), ChangProfilActivity.class);
                startActivity(intent);
            }
        } );

        reff = FirebaseDatabase.getInstance().getReference("Questions");
        listView = (ListView)findViewById(R.id.listView1ID);
        listViewAdapte = new ListViewAdapte(this,R.layout.my_listview_item, arrayList);

        listView.setAdapter(listViewAdapte);
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //newid.setEnabled(false);
                Intent intent = new Intent(getBaseContext(), AnswerActivity.class);
                if(content == null)
                    content = null;
                intent.putExtra( "Extra content", content);
                if(title == null)
                    title = null;
                intent.putExtra( "Extra title", title);
                if(location == null)
                    location = null;
                intent.putExtra( "Extra location", location);
                intent.putExtra( "Extra numQuestion", numQuestion);
                intent.putExtra( "Extra id_user", id_user);
                intent.putExtra( "Extra important_questions", importantQuestions);

                startActivity(intent);
            }
        } );

        Query myQuery = reff.orderByChild("location").equalTo("israel");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                content = snapshot.getValue( Question.class ).content();
                title = snapshot.getValue( Question.class ).title();
                location = snapshot.getValue( Question.class ).location();
                numQuestion = snapshot.getValue( Question.class ).numQuestion();
                id_user = snapshot.getValue( Question.class ).id_user();
                importantQuestions = snapshot.getValue( Question.class).important_questions();

                arrayList.add(new ListView_item(R.drawable.photo_profile_start, title, content ));
                listViewAdapte.notifyDataSetChanged();
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
