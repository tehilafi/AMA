//package com.tehilafi.ama;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//
//public class AnsweringActivity extends AppCompatActivity {
//
//   private static final int PICK_IMAGE_REQUEST = 1;
//   private Button send;
//   private ImageView imageButton, imageView1;
//   private Uri mImageUri;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate( savedInstanceState );
//            setContentView( R.layout.activity_answering );
//            // Hide the Activity Status Bar
//            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
//            // Hide the Activity  Bar
//            try {
//                this.getSupportActionBar().hide();
//            } catch (NullPointerException e) {
//            }
//
//            imageView1 = findViewById(R.id.imageView1ID);
//            imageButton = findViewById(R.id.imageButtonID);
//            imageButton.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openFileChooser();
//                }
//            } );
//
//            send = findViewById(R.id.sendID);
//            send.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(android.view.View view) {
//                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                    startActivity(intent);
//
//                }
//            } );
//
//        }
//
//        private void openFileChooser(){
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult( intent, PICK_IMAGE_REQUEST);
//        }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult( requestCode, resultCode, data );
//
//        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
//            mImageUri = data.getData();
//            Picasso.with(this).load(mImageUri).into(imageView1);
//
//            //mImageUri.setImageURI(mImageUri);
//        }
//    }
//}
//
//
//
