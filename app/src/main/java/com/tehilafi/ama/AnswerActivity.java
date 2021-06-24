package com.tehilafi.ama;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tehilafi.ama.db.Answer;
import com.tehilafi.ama.db.Question;
import com.tehilafi.ama.db.Users;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageFromCamera;
import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageToFirebase;
import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadVideoToFirebase;
import static com.tehilafi.ama.not.NotificationSender.sendNotification;

public class AnswerActivity extends Activity {

    private TextView txvname, txvdateTime, txvLocation, txvquestion, num_ansID;
    private EditText edtContent;
    private Button btnSave;
    private String iduser;
    private CircleImageView profileID, profileuserID;
    private ImageView  add_pic, add_video, importent;
    private ImageView with_ans, starID;
    private int score, numAns, numPIc, numVideo, stars;
    private Boolean importantQuestions, from1 = false, from2 = false, from3 = false ;
    private Uri videoUri = null;
    private String[] cameraPermissions;
    private ProgressBar progressBarID;
    private Uri contentUri, contentUri1;
    private String imageFileName , imageFileName1;
    private String from = "empty";
    private int num_ans;
    String id_user;
    String location;
    private SharedPreferences mPreferences;
    int num_question, numComments;
    private AlertDialog.Builder builder;
    private byte bb[];


    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 101;


    StorageReference storageReff;
    FirebaseStorage storage;


    public static final String TAG = "MyTag";


    private String id_asking;
    ArrayList<String> askingToken = new ArrayList<String>();

    public static long counter = 0;
    DatabaseReference reff, reffQ, reffAnswer, reffUser;
    Answer answer;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_answer );
        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // For navBar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        profileuserID = findViewById(R.id.profileuserID);
        profileID = findViewById(R.id.profileID);
        starID = findViewById(R.id.starID);
        num_ansID = findViewById(R.id.num_ansID);
        add_pic = findViewById( R.id.add_picID );

///////////////////////////////////////
        answer = new Answer();
        reffAnswer = FirebaseDatabase.getInstance().getReference().child( "Answers" );
        reffAnswer.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    counter = (snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
/////////////////////////////////////////

        // Show the profile image in profileID
        storageReff.child("profile picture/").child(mPreferences.getString( getString( R.string.id ), "" )).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Glide.with( AnswerActivity.this).load(downloadUrl).into(profileID);
            }
        });


        num_question = Integer.parseInt( getIntent().getStringExtra("Extra numQuestion") );
        reff = FirebaseDatabase.getInstance().getReference("Questions").child( String.valueOf(num_question));
        reff.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int numberQuestion = snapshot.getValue( Question.class ).getNumQuestion();
                if (numberQuestion == num_question) {
                    String content = snapshot.getValue( Question.class ).getContentQuestion();
                    location = snapshot.getValue( Question.class ).getLocation();
                    String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                    String userName = snapshot.getValue( Question.class ).getUsernameAsk();
                    id_asking = String.valueOf( snapshot.getValue( Question.class ).getIdAsking() );
                    importantQuestions = snapshot.getValue( Question.class ).getImportant_questions();
                    numComments = snapshot.getValue( Question.class).getNumComments();
                    int numA = snapshot.getValue( Question.class ).getNumComments();


                    txvname = findViewById( R.id.txvnameID );
                    txvname.setText( userName );
                    txvdateTime = findViewById( R.id.txvdateTimeID );
                    txvdateTime.setText( dateTime );
                    txvLocation = findViewById( R.id.txvLocationID );
                    txvLocation.setText( location );
                    txvquestion = findViewById( R.id.txvquestionID );
                    txvquestion.setText( content );
                    num_ansID.setText(numA + " תשובות ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        with_ans = findViewById( R.id.with_ansID );
        with_ans.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), AnswerDetailActivity.class);
                intent.putExtra( "Extra numQuestion", getIntent().getStringExtra("Extra numQuestion") );
                startActivity(intent);
                startActivity(intent);
            }
        } );

// *******************************  Get the token of device asking and details from Users DB  *******************************
        reffUser = FirebaseDatabase.getInstance().getReference("Users");
        Query myQueryUser = reffUser.orderByChild("token");
        myQueryUser.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int idAsking = snapshot.getValue( Users.class ).getId();
                if (id_asking.equals( String.valueOf( idAsking ) ))
                    askingToken.add( snapshot.getValue( Users.class ).getToken() );
                if(mPreferences.getString(getString(R.string.id), "").equals(String.valueOf(snapshot.getValue( Users.class ).getId()))) {
                    score = snapshot.getValue( Users.class ).getScore();
                    numAns = snapshot.getValue( Users.class ).getNumAnswer();
                    numPIc = snapshot.getValue( Users.class ).getNumPicture();
                    numVideo = snapshot.getValue( Users.class ).getNumVideo();

                }
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

        if(score < 150)
            starID.setImageResource(R.drawable.star1);
        if(stars >= 150 && stars < 500)
            starID.setImageResource(R.drawable.star2);
        if(stars >= 500)
            starID.setImageResource(R.drawable.star3);

// *******************************  Sava the answer data in the Answers DB  *******************************
        answer = new Answer();
        reffAnswer = FirebaseDatabase.getInstance().getReference().child( "Answers" );
        reffAnswer.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    counter = (snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // get the Firebase  storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();
        progressBarID = findViewById(R.id.progressBarID);

        edtContent = findViewById( R.id.edtContentID );

        btnSave = findViewById( R.id.btnSaveID );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkContent;
                if (edtContent.getText().toString().equals( "" )) {
                    Toast.makeText( AnswerActivity.this, "Missing Answer", Toast.LENGTH_LONG ).show();
                    checkContent = false;
                } else
                    checkContent = true;
                if (checkContent) {
                    num_ans= (int)counter;
                    // save in DB question
                    answer.setIdAsking( Integer.parseInt(id_asking) );
                    answer.setContentAnswer( edtContent.getText().toString().trim() );
                    answer.setNumAnswer(num_ans +1);
                    answer.setNumQuestion(num_question);
                    answer.setIdAnswering(Integer.parseInt(mPreferences.getString(getString(R.string.id), "")));
                    answer.setDateTimeAnswer(currentDateTime());
                    answer.setNumLikes(0);
                    answer.setUserNameAns(mPreferences.getString(getString(R.string.name), ""));

                    if(from2){
                        Log.d( TAG, "contentUri from2 = " + contentUri1 );
                        if (uploadVideoToFirebase( getApplicationContext(), imageFileName1, contentUri1, "null", from, String.valueOf( num_ans + 1 ) ) == true)
                            reffUser.child( mPreferences.getString( getString( R.string.id ), "" ) ).child( "numPicture" ).setValue( numPIc + 1 );
                        else
                            Toast.makeText( AnswerActivity.this, "העלאה נכשלה", Toast.LENGTH_SHORT ).show();
                    }
                    if(from1)
                        reffUser.child( mPreferences.getString( getString( R.string.id ), "" ) ).child( "numPicture" ).setValue( numPIc + 1 );

                    if(from3) {
                        if(uploadImageFromCamera(getApplicationContext(), bb, String.valueOf(num_ans + 1)) == true)
                            reffUser.child(mPreferences.getString(getString(R.string.id), "")).child("numPicture").setValue(numPIc+1);
                        else
                            Toast.makeText( AnswerActivity.this, "העלאה נכשלה", Toast.LENGTH_SHORT ).show();
                    }

                    reffAnswer.child( String.valueOf( counter + 1 ) ).setValue(answer);

                    // update score
                    int score_now;
                    if(importantQuestions)
                        score_now = score + 13;
                    else
                        score_now = score + 10;
                    reffUser.child(mPreferences.getString(getString(R.string.id), "")).child("score").setValue(score_now);

                    // update answer namber
                    reffUser.child(mPreferences.getString(getString(R.string.id), "")).child("numAnswer").setValue(numAns+1);

                    // update num aswers to question
                    reff.child("numComments").setValue(numComments+1);

                    // send notification to tokens of asking
                    sendNotification( AnswerActivity.this, askingToken, "תשובה חדשה",  edtContent.getText().toString().trim(), "answer");

                    progressBarID.setVisibility( View.VISIBLE);
                    (new Handler()).postDelayed(this::continued, 3000);
                }
            else
                Toast.makeText(AnswerActivity.this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();
            }

            private void continued() {
               Intent intent = new Intent( getBaseContext(), MyAnswerActivity.class );
               startActivity( intent );
            }
        });

        add_pic.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = "pic";
                pickDialog();
            }
        } );
        add_video = findViewById( R.id.add_videoID );
        add_video.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = "video";
                pickDialog();
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @androidx.annotation.Nullable Intent data){
        super.onActivityResult( requestCode, resultCode, data );
        // image from galerya
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
            contentUri = data.getData();
            Log.d( TAG, "contentUri pic = " + contentUri );
            String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
            imageFileName = "JPEG_" + timeStamp;
            if (uploadImageToFirebase( getApplicationContext(), imageFileName, contentUri, "null", from, String.valueOf( (int)counter + 1 ) ) == true)
                from1 = true;
            else
                Toast.makeText( AnswerActivity.this, "העלאה נכשלה", Toast.LENGTH_SHORT ).show();
        }
        // video
        else if ((requestCode == REQUEST_VIDEO_CAPTURE || requestCode == GALLERY_REQUEST_CODE) && resultCode == RESULT_OK && data != null) {
            Intent mediaScanIntent1 = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
            contentUri1 = data.getData();
            Log.d( TAG, "contentUri1 video = " + contentUri1 );
            String timeStamp1 = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
            imageFileName1 = "JPEG_" + timeStamp1;
            from2 = true;

        }
        // image from camera
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            bb = bytes.toByteArray();
            Log.d(TAG, "bb innn = " + bb);
            from3 = true;
        }

        else{
            Toast.makeText(this, "Error. Try again", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error. requestCode = " + requestCode + "resultCode = " + resultCode);

        }
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

    //  The function returns the current date and time
    public String currentDateTime(){
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date currentTime = Calendar.getInstance().getTime();
        String todayAsString = df.format(currentTime);

        return todayAsString;
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
        if(from == "pic") {
            Intent piccamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(piccamera, REQUEST_IMAGE_CAPTURE);
        }
        if(from == "video"){
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    // pick from gallery - intent
    private void pickGallery(){
        if(from == "pic") {
            Intent picgallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( picgallery, RESULT_LOAD_IMG );
        }
        if(from == "video"){
            Intent videogallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            videogallery.setType("video/*");
            startActivityForResult( videogallery, GALLERY_REQUEST_CODE );
        }
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
                    else{
                        Toast.makeText( this, "Camera & Storage permission are required" , Toast.LENGTH_LONG).show();
                    }
                }
        }
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);
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

}
