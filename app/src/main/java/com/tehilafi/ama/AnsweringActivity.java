package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class AnsweringActivity extends AppCompatActivity {

   private static final int PICK_IMAGE_REQUEST = 1;
   private Button send;
   private ImageView imageid, vidoeid;
   private Uri mImageUri;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_answering );
            // Hide the Activity Status Bar
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
            // Hide the Activity  Bar
            try {
                this.getSupportActionBar().hide();
            } catch (NullPointerException e) {
            }

            imageid = findViewById(R.id.imageID);
            vidoeid = findViewById(R.id.videoID);
            send = findViewById(R.id.sendID);
            send.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            } );
            // upload image
            imageid.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(getBaseContext(), PotoActivity.class);
                    startActivity(intent);

                }
            } );
            // upload vidoe
            vidoeid.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(getBaseContext(), AddvideoActivity.class);
                    startActivity(intent);

                }
            } );

        }


}



