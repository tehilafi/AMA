package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

public class MyQuestionActivity extends Activity {

    private Button Questions_I_was_asked;
    private CircleImageView profile;

    DatabaseReference reff;

    ListView listView;
    ListViewAdapte listViewAdapte;
    ArrayList<ListView_item> arrayList = new ArrayList<>();

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

        Questions_I_was_asked = findViewById( R.id.Questions_I_was_askedID);
        Questions_I_was_asked.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), MyAnswerActivity.class);
                startActivity(intent);
            }
        } );

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
        listView = (ListView)findViewById(R.id.listView2ID);
        listViewAdapte = new ListViewAdapte(this,R.layout.my_listview_item, arrayList);

        listView.setAdapter(listViewAdapte);
        Query myQuery = reff.orderByChild("location").equalTo("קניון עזריאלי");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String content = snapshot.getValue( Question.class ).content();
                String title = snapshot.getValue( Question.class ).title();

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










//package com.tehilafi.ama;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MyQuestionActivity extends AppCompatActivity {
//
//    ListView listView;
//    List<Question> questionList;
//    Button btn;
//
//    DatabaseReference usersDBRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate( savedInstanceState );
//        setContentView( R.layout.activity_my_quastions );
//
//        // Hide the Activity Status Bar
//        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
//        // Hide the Activity  Bar
//        try {
//            this.getSupportActionBar().hide();
//        } catch (NullPointerException e) {
//        }
//        btn = findViewById(R.id.btnID);
//        btn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(android.view.View view) {
//                Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
//                startActivity(intent);
//            }
//        } );
//
//        listView = findViewById(R.id.listViewID);
//        questionList = new ArrayList<>();
//
//
//
//
//        // list item click event
//        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
//                startActivity(intent);            }
//        } );
//
//
//        usersDBRef = FirebaseDatabase.getInstance().getReference();
//        usersDBRef.addValueEventListener( new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                questionList.clear();
//                for(DataSnapshot userDatasnap : snapshot.getChildren()){
//                    Question question = userDatasnap.getValue(Question.class);
//                    questionList.add(question);
//                }
////                ListAdapter adapter = new ListAdapter(MyQuestionActivity.this, questionList);
////                listView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        } );
//
//
//
//    }
//}
//
//
//
//
//
//















