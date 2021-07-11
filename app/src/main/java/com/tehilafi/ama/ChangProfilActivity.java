package com.tehilafi.ama;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageFromCamera;
import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageToFirebase;


public class ChangProfilActivity extends Activity {

    public static final String TAG = "MyTag";
    private static Context context;

    private Uri contentUri;
    private CircleImageView profile_image;
    private Button btnSave;
    private Boolean onClick_profile_image = false, from1, from2;
    private StorageReference storageReff;
    private DatabaseReference reff;
    private TextView usernameID, nLID,  nAID, nPID, nVID;
    private String nameuser, pas, ph, iduser, imageFileName, numLike = "NUll", numAns = "NULL", numPic = "NULL", numVideo = "NULL";
    private EditText user_nameID, passwordID, phoneID;
    private ImageView ratingID;
    Users users;
    private ProgressBar progressBarID;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private AlertDialog.Builder builder;
    private String[] cameraPermissions;
    private byte bb[];

    public static final int CAMERA_REQUEST_CODE = 102;
    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int RESULT_LOAD_IMG = 101;

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
        mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        mEditor = mPreferences.edit();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
                Glide.with( getApplicationContext()).load(downloadUrl).into(profile_image);
            }
        });

        // To change the profile picture
        profile_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pickDialog();
                    onClick_profile_image = true;

                }
                catch (ActivityNotFoundException e) {
                    onClick_profile_image = false;
                }
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
                    mEditor.putString(getString(R.string.name), user_nameID.getText().toString().trim());
                }
                if (!pas.equals( passwordID.getText().toString().trim() )) {
                    reff.child( "password" ).setValue( passwordID.getText().toString().trim() );
                    mEditor.putString(getString(R.string.pas), passwordID.getText().toString().trim());
                }

                if (!ph.equals( phoneID.getText().toString().trim() )) {
                    reff.child( "phone" ).setValue( phoneID.getText().toString().trim() );
                    mEditor.putString( getString( R.string.ph ), phoneID.getText().toString().trim() );
                }
                mEditor.commit();

                // upload profile image to firebase storage
                if(onClick_profile_image) {
                    if (from1) {
                        if (uploadImageToFirebase( getApplicationContext(), imageFileName, contentUri, iduser, "profil", "null" ) == false)
                            Toast.makeText( ChangProfilActivity.this, "שגיאה. התמונה לא עלתה", Toast.LENGTH_SHORT ).show();

                    }
                    if(from2){
                        if(uploadImageFromCamera(getApplicationContext(), bb, "null", iduser,  "profil" ) == false)
                            Toast.makeText( ChangProfilActivity.this, "שגיאה. התמונה לא עלתה", Toast.LENGTH_SHORT ).show();
                    }
                }
                progressBarID.setVisibility( View.VISIBLE);
                (new Handler()).postDelayed(this::continued, 500);

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
        // image from galerya
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
            contentUri = data.getData();
            String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
            imageFileName = "JPEG_" + timeStamp;
            from1 = true;
            from2 = false;
            Glide.with( getApplicationContext()).load(contentUri).into(profile_image);
        }
        // image from camera
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            bb = bytes.toByteArray();
            from2 = true;
            from1 = false;
            Glide.with(getApplicationContext()).load(bb).into(profile_image);
        }

        else{
            Toast.makeText(this, "שגיאה, נסה שוב", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error. requestCode = " + requestCode + "resultCode = " + resultCode);

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

    private void pickDialog() {
        String[] options = {"Camera", "Gallery"};
        builder = new AlertDialog.Builder(this);
        builder.setTitle( "Pick picture From" ).setItems( options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){  // camera clicked
                    if(!checkCameraPermission())
                        requestCameraPermission();
                    else
                        pickCamera();
                }
                else if(i == 1){  //  gallery clicked
                    pickGallery();
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

    // pick from camera - intent
    private void pickCamera(){
        Intent piccamera = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(piccamera, REQUEST_IMAGE_CAPTURE);
    }

    // pick from gallery - intent
    private void pickGallery(){
            Intent picgallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( picgallery, RESULT_LOAD_IMG );
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
                        pickCamera();
                    }
                }
        }
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);
    }
}



