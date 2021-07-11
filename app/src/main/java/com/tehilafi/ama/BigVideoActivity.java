package com.tehilafi.ama;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.VideoView;

public class BigVideoActivity extends Activity {

    VideoView VideoView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_big_video );
        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        VideoView2 = findViewById( R.id.videoView2ID );
//        Intent callingActivityIntent = getIntent();
//        if (callingActivityIntent != null) {
//            Uri imageUri = callingActivityIntent.getData();
//            if (imageUri != null)
//                Glide.with( getApplicationContext() ).load( imageUri ).into(VideoView2);
//        }
    }
}


