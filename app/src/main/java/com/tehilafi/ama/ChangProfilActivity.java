package com.tehilafi.ama;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChangProfilActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    private CircleImageView profile_image;
    private Button btnSave;
    private StorageReference storageReference;
    DatabaseReference reff;
    private String currentPhotoPath, iduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chang_profile );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        profile_image = findViewById( R.id.profile_imageID );
        profile_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult( gallery, GALLERY_REQUEST_CODE );
            }
        } );
//
//        btnSave = findViewById(R.id.btnSaveID);
//        btnSave.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(android.view.View view) {
//                uploadProfilImage();
//            }
//        } );

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPreferences.edit();
        iduser =  mPreferences.getString(getString(R.string.id), "");
        if(iduser == "")
            iduser = "null";

        reff = FirebaseDatabase.getInstance().getReference("Users").child(iduser).child("profil_image");
        reff.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DataSnapshot value = snapshot.child("videoUri");
                Picasso.with( ChangProfilActivity.this ).load( String.valueOf( value ) ).into( profile_image );

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

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode == CAMERA_PERM_CODE){
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchTakePictureIntent();
                }else {
                    Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult( requestCode, resultCode, data );
            // For a photo from the gallery
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contentUri = data.getData();
                    String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
                    String imageFileName = "JPEG_" + timeStamp + "." + getFileExt( contentUri );
                    Log.d( "tag", "onActivityResult: Gallery Image Uri:  " + imageFileName );
                    profile_image.setImageURI(contentUri);

                    uploadImageToFirebase(imageFileName,contentUri);
                }
            }
        }

    // Updating photos on firebase
    private void uploadImageToFirebase(final String name , final Uri contentUri) {

        final String timestamp = "" + System.currentTimeMillis();
        final String filePathAndName = "profil_image";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(contentUri).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", "" + timestamp);
                    hashMap.put("pic_profil_Uri", "" + downloadUri);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(iduser);
                    reference.child( filePathAndName ).setValue(hashMap).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    } );
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        } );
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        Toast.makeText(this, "befor .", Toast.LENGTH_LONG).show();
        //storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);// keep the image in gallery
        Toast.makeText(this, "after .", Toast.LENGTH_LONG).show();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "net.smallacademy.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);

            }
        }
    }

}