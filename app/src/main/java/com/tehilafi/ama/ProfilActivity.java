//package com.tehilafi.ama;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class ProfilActivity extends AppCompatActivity {
//
//    private Button change;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate( savedInstanceState );
//        setContentView( R.layout.activity_profile );
//        // Hide the Activity Status Bar
//        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
//        // Hide the Activity  Bar
//        try {
//            this.getSupportActionBar().hide();
//        } catch (NullPointerException e) {
//        }
//
//        // Moves to activity of change profile
//        change = findViewById( R.id.changeID );
//        change.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent( getBaseContext(), ChangProfilActivity.class );
//                startActivity( intent );
//            }
//        } );
//
//    }
//}
