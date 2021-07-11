package com.tehilafi.ama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashScreenActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

        @Override
        public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

            // Hide the Activity Status Bar
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // Hide the Activity  Bar
            try
            {
                this.getActionBar().hide();
            }
            catch (NullPointerException e){}

            //Code to start timer and take action after the timer ends
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    //Do any action here. Now we are moving to next page
                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    if (pref.getBoolean("activity_executed", false)) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, PrivacyPolicyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

}
