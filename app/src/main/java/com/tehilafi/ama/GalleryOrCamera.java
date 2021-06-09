//package com.tehilafi.ama;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.widget.Toast;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//
//import java.io.File;
//import java.io.IOException;
//
//public class GalleryOrCamera {
//
//    public static final int CAMERA_PERM_CODE = 101;
//    public static final int CAMERA_REQUEST_CODE = 102;
//    public static final int GALLERY_REQUEST_CODE = 105;
//
//
//    public static void gallery_camera(String from) {
//        if (from.equals( "gallery" )) {
//            Intent gallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
//            startActivityForResult( gallery, GALLERY_REQUEST_CODE );
//        }
//        if (from.equals( "camera" )) {
//            askCameraPermissions();
//        }
//    }
//
//    //  in order not to allow access to the device's camera
//    private static void askCameraPermissions() {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
//        }else {
//            dispatchTakePictureIntent();
//        }
//    }
//
//    private static void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//                Toast.makeText(this, "in  2 .", Toast.LENGTH_LONG).show();
//            } catch (IOException ex) {
//                Toast.makeText(this, "in  3 .", Toast.LENGTH_LONG).show();
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Toast.makeText(this, "in  4 .", Toast.LENGTH_LONG).show();
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "net.smallacademy.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
//
//            }
//        }
//    }
//
//
//}
