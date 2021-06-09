package com.tehilafi.ama;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tehilafi.ama.db.Users;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageToFirebase;


public class ChangProfilActivity extends Activity {

    public static final String TAG = "MyTag";
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;


    private Uri contentUri;
    private String imageFileName;
    private CircleImageView profile_image;
    private Button btnSave;

    // for profile_image
    private StorageReference storageReff;
    private DatabaseReference reff;

    private String nameuser, pas, ph, iduser;
    private EditText user_nameID, passwordID, phoneID;

    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chang_profile );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        // Init
        reff = FirebaseDatabase.getInstance().getReference().child( "Users" );
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        // get the Firebase  storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();


        profile_image = findViewById( R.id.profile_imageID );

        // Show the profile image in profileID
        storageReff.child("profile picture/").child(mPreferences.getString( getString( R.string.id ), "" )).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Glide.with( ChangProfilActivity.this).load(downloadUrl).into(profile_image);
            }
        });

        // To change the profile picture
        profile_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio( 1, 1 ).start( ChangProfilActivity.this );
            }
        });

// *************************************  Save user details in users DB *************************************

        user_nameID = findViewById( R.id.user_nameID );
        passwordID = findViewById( R.id.passwordID );
        phoneID = findViewById( R.id.phoneID );

        iduser = mPreferences.getString( getString( R.string.id ), "" );
        if (iduser == "")
            iduser = "null";
        nameuser = mPreferences.getString( getString( R.string.name ), "" );
        if (nameuser == "")
            nameuser = "null";
        pas = mPreferences.getString( getString( R.string.pas ), "" );
        if (pas == "")
            pas = "null";
        ph = mPreferences.getString( getString( R.string.ph ), "" );
        if (ph == "")
            ph = "null";

        user_nameID.setText( nameuser );
        passwordID.setText( pas );
        phoneID.setText( ph );

        users = new Users();
        reff = FirebaseDatabase.getInstance().getReference( "Users" );

        btnSave = findViewById( R.id.btnSaveID );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

                if (!nameuser.equals( user_nameID.getText().toString().trim() ))
                    reff.child( iduser ).child( "userName" ).setValue( user_nameID.getText().toString().trim() );

                if (!pas.equals( passwordID.getText().toString().trim() ))
                    reff.child( iduser ).child( "password" ).setValue( passwordID.getText().toString().trim() );

                if (!ph.equals( phoneID.getText().toString().trim() ))
                    reff.child( iduser ).child( "phone" ).setValue( phoneID.getText().toString().trim() );

                // upload profile image to firebase storage
                uploadImageToFirebase(imageFileName,contentUri, iduser);

                Intent intent = new Intent( getBaseContext(), MainActivity.class );
                startActivity( intent );
            }
        } );
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
            contentUri = result.getUri();
            profile_image.setImageURI(contentUri);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast( mediaScanIntent );
            String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
            imageFileName = "JPEG_" + timeStamp + "." + getFileExt( contentUri );
        }
        else{
            Toast.makeText(this, "Error. Try again", Toast.LENGTH_SHORT).show();
        }
    }
}



