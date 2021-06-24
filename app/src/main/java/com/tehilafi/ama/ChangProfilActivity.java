package com.tehilafi.ama;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private static Context context;

    private Uri contentUri;
    private String imageFileName;
    private CircleImageView profile_image;
    private Button btnSave;
    private Boolean onClick_profile_image = false;
    // for profile_image
    private StorageReference storageReff;
    private DatabaseReference reff;
    private TextView usernameID, nLID,  nAID, nPID, nVID;
    private String nameuser, pas, ph, iduser;
    private EditText user_nameID, passwordID, phoneID;
    private ImageView ratingID;
    Users users;
    private ProgressBar progressBarID;
    private String numLike = "NUll", numAns = "NULL", numPic = "NULL", numVideo = "NULL";

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
        // For navBar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // get the Firebase  storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();
        progressBarID = findViewById(R.id.progressBarID);

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
                onClick_profile_image = true;
            }
        });

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

        usernameID = findViewById( R.id.usernameID );
        usernameID.setText(nameuser);
// *************************************  Save user details in users DB *************************************

        user_nameID = findViewById( R.id.user_nameID );
        passwordID = findViewById( R.id.passwordID );
        phoneID = findViewById( R.id.phoneID );
        nLID = findViewById(R.id.nLID);
        nAID = findViewById(R.id.nAID);
        nPID = findViewById(R.id.nPID);
        nVID = findViewById(R.id.nVID);

        user_nameID.setText( nameuser );
        passwordID.setText( pas );
        phoneID.setText( ph );

        users = new Users();
        reff = FirebaseDatabase.getInstance().getReference( "Users" ).child(iduser);

//************************************* Get the score from Users DB  *************************************
        ratingID = findViewById( R.id.ratingID );
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int stars = dataSnapshot.getValue( Users.class).getScore();
                if(stars < 150)
                    ratingID.setImageResource(R.drawable.star1);
                if(stars >= 150 && stars < 500)
                    ratingID.setImageResource(R.drawable.star2);
                if(stars >= 500)
                    ratingID.setImageResource(R.drawable.star3);

                numLike = String.valueOf( dataSnapshot.getValue( Users.class).getNumLike() );
                nLID.setText(numLike);
                numAns = String.valueOf( dataSnapshot.getValue( Users.class).getNumAnswer() );
                nAID.setText(numAns);
                numPic = String.valueOf( dataSnapshot.getValue( Users.class).getNumPicture() );
                nPID.setText(numPic);
                numVideo = String.valueOf( dataSnapshot.getValue( Users.class).getNumVideo() );
                nVID.setText(numVideo);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btnSave = findViewById( R.id.btnSaveID );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

                if (!nameuser.equals( user_nameID.getText().toString().trim() )) {
                    reff.child( "userName" ).setValue( user_nameID.getText().toString().trim() );
                    usernameID.setText(user_nameID.getText().toString().trim());

                }
                if (!pas.equals( passwordID.getText().toString().trim() ))
                    reff.child( "password" ).setValue( passwordID.getText().toString().trim() );

                if (!ph.equals( phoneID.getText().toString().trim() ))
                    reff.child( "phone" ).setValue( phoneID.getText().toString().trim() );

                // upload profile image to firebase storage
                Log.d(TAG, "contentUri = " + contentUri);
                if(onClick_profile_image)
                    if (uploadImageToFirebase(getApplicationContext(), imageFileName, contentUri, iduser, "profil", "null") == false)
                        Toast.makeText( ChangProfilActivity.this, "התמונה לא התעדכנה", Toast.LENGTH_SHORT ).show();

                progressBarID.setVisibility( View.VISIBLE);
                (new Handler()).postDelayed(this::continued, 2500);

            }

            private void continued() {
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

    // *******************************  For NavBar  *******************************
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.preID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            Toast.makeText( getApplicationContext(), "הכנס מיקום לחיפוש", Toast.LENGTH_SHORT ).show();
                            break;

                        case R.id.mainID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.my_questionID:
                            intent = new Intent( getBaseContext(), MyAnswerActivity.class );
                            startActivity( intent );
                            break;

                        case R.id.add_locationID:
                            intent = new Intent( getBaseContext(), MainActivity.class );
                            startActivity( intent );
                            Toast.makeText( getApplicationContext(), "הכנס מיקום לחיפוש", Toast.LENGTH_SHORT ).show();
                            break;
                    }
                    return true;
                }
            };

    public static Context getAppContext() {
        return ChangProfilActivity.context;
    }
}



