package com.tehilafi.ama;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyQuestionActivity extends AppCompatActivity {

    ListView listView;
    List<Question> questionList;
    Button btn;

    DatabaseReference usersDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_quastions );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        btn = findViewById(R.id.btnID);
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
                startActivity(intent);
            }
        } );

        listView = findViewById(R.id.listViewID);
        questionList = new ArrayList<>();




        // list item click event
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
                startActivity(intent);            }
        } );


        usersDBRef = FirebaseDatabase.getInstance().getReference();
        usersDBRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for(DataSnapshot userDatasnap : snapshot.getChildren()){
                    Question question = userDatasnap.getValue(Question.class);
                    questionList.add(question);
                }
//                ListAdapter adapter = new ListAdapter(MyQuestionActivity.this, questionList);
//                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );



    }
}





















