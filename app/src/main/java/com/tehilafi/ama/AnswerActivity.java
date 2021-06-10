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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageFromCamera;
import static com.tehilafi.ama.media.UploadPhotoAndVideos.uploadImageToFirebase;
import static com.tehilafi.ama.not.NotificationSender.sendNotification;

public class AnswerActivity extends Activity {

    private TextView txvname, txvdateTime, txvLocation, txvquestion;
    private EditText edtContent;
    private Button btnSave;
    private String iduser;
    private ImageView  add_pic, add_video, importent;
    private String num_question;
    private RatingBar myRating;
    private ImageView with_ans;
    private int score;
    private String importantQuestions;
    private Uri videoUri = null;
    private String[] cameraPermissions;
    private ProgressBar progressBar;
    private Uri contentUri;
    private String imageFileName;
    private String from;
    private int num_ans;

    private AlertDialog.Builder builder;


    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    StorageReference storageReff;
    FirebaseStorage storage;


    public static final String TAG = "MyTag";


    private String id_asking;
    ArrayList<String> askingToken = new ArrayList<String>();

    public static long counter = 0;
    DatabaseReference reff, reffAnswer, reffUser;
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

        myRating = findViewById( R.id.MyRatingID);


// *******************************  Get the data from the Question DB  *******************************
        reff = FirebaseDatabase.getInstance().getReference("Questions");
        Query myQuery = reff.orderByChild("numQuestion");
        myQuery.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                num_question = getIntent().getStringExtra("Extra numQuestion");
                Log.d(TAG, "Extra numQuestion" + num_question);
                String numberQuestion = snapshot.getValue( Question.class ).numQuestion();
                if(numberQuestion.equals(num_question)){
                    String content = snapshot.getValue( Question.class ).content();
                    String location = snapshot.getValue( Question.class ).location();
                    String dateTime = snapshot.getValue( Question.class ).getDateTimeQuestion();
                    String userName = snapshot.getValue( Question.class ).getUsernameAsk();
                    id_asking = snapshot.getValue( Question.class ).id_user();
                    importantQuestions = snapshot.getValue( Question.class ).getImportant_questions();
                    Log.d(TAG, "importantQuestions = " + importantQuestions);



                    txvname = findViewById(R.id.txvnameID);
                    txvname.setText(userName);
                    txvdateTime = findViewById(R.id.txvdateTimeID);
                    txvdateTime.setText(dateTime);
                    txvLocation = findViewById(R.id.txvLocationID);
                    txvLocation.setText(location);
                    txvquestion = findViewById(R.id.txvquestionID);
                    txvquestion.setText(content);
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

        importent = findViewById(R.id.importentID);
        if(importantQuestions == "false")
            importent.setVisibility(View.INVISIBLE);

        with_ans = findViewById( R.id.with_ansID );
        with_ans.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(getBaseContext(), AnswerDetailActivity.class);
                intent.putExtra( "Extra numQuestion", num_question );
                startActivity(intent);
                startActivity(intent);
            }
        } );


// *******************************  Get the token of device asking  *******************************
        reffUser = FirebaseDatabase.getInstance().getReference("Users");
        Query myQueryUser = reffUser.orderByChild("token");
        myQueryUser.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int idAsking = snapshot.getValue( Users.class ).getId();
                Log.d(TAG, "idAsking = " + idAsking);
                Log.d(TAG, "String.valueOf( idAsking )  = " + String.valueOf( idAsking ) );
                if (id_asking.equals( String.valueOf( idAsking ) ))
                    askingToken.add( snapshot.getValue( Users.class ).getToken() );
                if(mPreferences.getString(getString(R.string.id), "").equals(String.valueOf(snapshot.getValue( Users.class ).getId()))) {
                    score = snapshot.getValue( Users.class ).getScore();
                    Log.d( TAG, "score is = " + score );
                }
                Log.d(TAG, "askingToken = " + askingToken);
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
// *******************************  Sava the answer data in the Answers DB  *******************************
        answer = new Answer();
        reff = FirebaseDatabase.getInstance().getReference().child( "Answers" );
        reff.addValueEventListener( new ValueEventListener() {
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
        progressBar = findViewById(R.id.progressBarID);


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
                    answer.setNumQuestion(Integer.parseInt(num_question));
                    answer.setIdAnswering(Integer.parseInt(mPreferences.getString(getString(R.string.id), "")));
                    answer.setDateTimeAnswer(currentDateTime());
                    answer.setNumLikes(0);
                    answer.setNumComments(0);
                    answer.setUserNameAns(mPreferences.getString(getString(R.string.name), ""));

                    progressBar.setVisibility( View.VISIBLE );
                    if (uploadImageToFirebase( getApplicationContext(), imageFileName, contentUri, "null", from, String.valueOf(num_ans +1) ) == true)
                        progressBar.setVisibility( View.INVISIBLE );

                    reff.child( String.valueOf( counter + 1 ) ).setValue(answer);

                    // update score
                    int score_now;
                    if(importantQuestions.equals("true"))
                        score_now = score + 13;
                    else
                        score_now = score + 10;
                    reffUser.child(mPreferences.getString(getString(R.string.id), "")).child("score").setValue(score_now);

                    // send notification to tokens of asking
                    sendNotification( AnswerActivity.this, askingToken, "try", "massege", "answer");

                }
            else
                Toast.makeText(AnswerActivity.this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();


            }
        });

        add_pic = findViewById( R.id.add_picID );
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

        if ((requestCode == GALLERY_REQUEST_CODE || requestCode == CAMERA_PIC_REQUEST || requestCode == REQUEST_VIDEO_CAPTURE) && resultCode == RESULT_OK && data != null) {
            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
            contentUri = data.getData();
            Log.d(TAG, "contentUri = " + contentUri);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast( mediaScanIntent );
            String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
            imageFileName = "JPEG_" + timeStamp;

        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            byte bb[] = bytes.toByteArray();

            progressBar.setVisibility( View.VISIBLE );
            if (uploadImageFromCamera(getApplicationContext(), bb, String.valueOf(num_ans +1)) == true)
                progressBar.setVisibility( View.INVISIBLE );
        }
        else if(requestCode == VIDEO_PICK_CAMERA_CODE && resultCode == RESULT_OK) {
            contentUri = data.getData();

        }
        else{
            Toast.makeText(this, "Error. Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickDialog() {
        Log.d(TAG, "in pickDialog");
        String[] options = {"Camera", "Gallery"};
        builder = new AlertDialog.Builder(this);

//        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Pick picture From" ).setItems( options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){  // camera clicked
                    if(!checkCameraPermission())
                        requestCameraPermission();
                    else
                        pickCamera();
                    Log.d(TAG, "in camera");
                }
                else if(i == 1){  //  gallery clicked
                    pickGallery();
                    Log.d(TAG, "in gallery");

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
        else if(from == "video"){
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
            startActivityForResult( picgallery, GALLERY_REQUEST_CODE );
        }
        else if(from == "video"){
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


}
