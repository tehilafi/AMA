package com.tehilafi.ama;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class PrivacyPolicyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_privacy_policy );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }
    }

    // Opens the Privacy policy linK
    public void privacyPolicy(View v)
    {
        Uri uri = Uri.parse("https://askmeanywhere123.blogspot.com/2021/07/privacy-policy-tehila-ruben-built-ama.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // Continue to ExplanationLocationPermission activity
    public void yesBtn(View v)
    {
        Intent intent = new Intent(PrivacyPolicyActivity.this, ExplanationLocationPermission.class);
        startActivity(intent);
    }

    // exit
    public void noBtn(View v)
    {
      finish();
    }
}
