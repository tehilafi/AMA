//package com.tehilafi.ama;
//
////package com.example.frederik.snapsule;
//
////Importing
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//
//public class MainActivity extends Activity {
//
//    LoginButton login;
//    CallbackManager callbackManager;
//
//    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//    SharedPreferences.Editor loginStateEditor = sharedPref.edit();
//
//    //Creating and initializing the feature(Code from Facebook install guide)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext()); //Calls for the facebook feature
//        callbackManager = CallbackManager.Factory.create();
//        setContentView(R.layout.content_main);
//
//        login = (LoginButton) findViewById(R.id.login_button);
//        login.setReadPermissions("public_profile", "email");
//
//        if (sharedPref.getBoolean("success_login", false)) {
//            Intent i = new Intent(MainActivity.this, LandingActivity.class);
//            startActivity(i);
//        }
//
//        // Callback registration
//        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Intent i = new Intent(MainActivity.this, LandingActivity.class);
//
//                loginStateEditor.putBoolean("success_login", true);
//                loginStateEditor.commit();
//
//                startActivity(i);
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//
//            }
//        });
//
//    }
//
//    //Facebook code
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//}
