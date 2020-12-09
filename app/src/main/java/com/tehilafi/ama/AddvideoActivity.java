package com.tehilafi.ama;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddvideoActivity extends AppCompatActivity {

    private EditText etTitle;
    private VideoView videoView;
    private Button btnUpload;
    private FloatingActionButton fabPicVideo;

    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermissions;
    private Uri videoUri = null;

    private String title;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.menu_addvideo );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        etTitle = findViewById(R.id.etTitleID);
        videoView = findViewById(R.id.videoViewID);
        btnUpload = findViewById(R.id.btnUploadID);
        fabPicVideo = findViewById(R.id.fabPicVideoiD);

        // setup progress dialog
        progressDialog = new ProgressDialog( this );
        progressDialog.setTitle( "Please wait" );
        progressDialog.setMessage( "Uploading Video" );
        progressDialog.setCanceledOnTouchOutside( false );

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = etTitle.getText().toString().trim();
                if(TextUtils.isEmpty( title ))
                    Toast.makeText( AddvideoActivity.this, "Title is required...", Toast.LENGTH_LONG ).show();
                else if(videoUri == null)
                    Toast.makeText( AddvideoActivity.this, "Pick a video before you can upload...", Toast.LENGTH_LONG ).show();
                else
                    uploadVieoFirebase();
            }
        } );

        fabPicVideo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();
            }
        } );

    }

    private void uploadVieoFirebase() {
        // show progress
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();
        String filePathAndName = "Videos/" + "video_"+timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile( videoUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", "" + timestamp);
                    hashMap.put("title", "" + title);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("videoUri", "" + downloadUri);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("videos");
                    reference.child( timestamp ).setValue( hashMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText( AddvideoActivity.this, "video uploaded...", Toast.LENGTH_LONG ).show();
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( AddvideoActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG ).show();
                        }
                    } );
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText( AddvideoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void videoPickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Pick Video From" ).setItems( options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    // camera clicked
                    if(!checkCameraPermission())
                        requestCameraPermission();
                    else
                        videoPickCamera();
                }
                else if(i == 1){
                    // gallery clicked
                    videoPickGallery();
                }
            }
        } ).show();
    }

    // request camera permission
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }
    // pick video from gallery - intent
    private void videoPickGallery(){
        Intent intent = new Intent();
        intent.setType("vidoe/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"), VIDEO_PICK_GALLERY_CODE);
    }

    // pick video from camera - intent
    private void videoPickCamera(){
        Intent intent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE );
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }

    private void setVideoToVideoView() {
        MediaController mediaController = new MediaController( this );
        mediaController.setAnchorView( videoView );
        // set media controller to video view
        videoView.setMediaController( mediaController);
        // set video uri
        videoView.setVideoURI( videoUri );
        videoView.requestFocus();
        videoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.pause();
            }
        } );
    }

    // handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    // check permission allowed or not
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        videoPickCamera();
                    }
                    else{
                        Toast.makeText( this, "Camera & Storage permission are required" , Toast.LENGTH_LONG).show();
                    }
                }
        }
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);
    }

    // called after picking video from camera / gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == VIDEO_PICK_GALLERY_CODE){
                videoUri = data.getData();
                // show picked video in VideoView
                setVideoToVideoView();
            }
            else if (requestCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data.getData();
                // show picked video in VideoView
                setVideoToVideoView();
            }
        }
        super.onActivityResult( requestCode, resultCode, data );
    }


}
