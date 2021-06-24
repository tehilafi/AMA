package com.tehilafi.ama;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tehilafi.ama.db.Answer;
import com.tehilafi.ama.db.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class OneAnswerActivity extends Activity {

    private TextView txvname, txvdateTime,  txvanswer;
    private String num_answer, id_asking, id_answer, numL = "NUll";
    public static final String TAG = "MyTag";
    DatabaseReference reff, reffUser;
    ImageView imageView, starID, likeID, whiteID;
    VideoView videoView;
    private SharedPreferences mPreferences;
    private CircleImageView profileuserID;
    private StorageReference storageReff;
    FirebaseStorage storage;
    private int numLike = 0, score, numLNew = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_one_anser );
        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        // Init

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        num_answer = getIntent().getStringExtra( "Extra numAnswer" );

        imageView = findViewById( R.id.imageViewID );
        videoView = findViewById( R.id.videoViewID );
        starID = findViewById( R.id.starID );
        profileuserID = findViewById( R.id.profileuserID );
        likeID = findViewById( R.id.likeID );
        whiteID = findViewById(R.id.whiteID);


        storageReff.child("picture answer/").child(num_answer).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (score < 150)
                    Toast.makeText( OneAnswerActivity.this, "ברמת הדירוג שלך אי אפשר לצפות בתמונות", Toast.LENGTH_SHORT ).show();
                else {
                    Toast.makeText( OneAnswerActivity.this, "viewPic", Toast.LENGTH_SHORT ).show();
                    // Show the image
                    storageReff.child( "picture answer/" ).child(num_answer).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Glide.with( getApplicationContext()).load( downloadUrl ).into( imageView );

                        }
                    } );
                }            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageResource(R.drawable.transillumination);
            }
        });

//        storageReff.child("video answer/").child(num_answer).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//           @Override
//           public void onSuccess(Uri uri) {
//               if (score < 500)
//                   Toast.makeText( OneAnswerActivity.this, "ברמת דירוג שלך אי אפשר לצפות בסרטונים", Toast.LENGTH_SHORT ).show();
//               else {
//                   whiteID.setVisibility(View.INVISIBLE);
//                   storageReff.child( "video answer/" ).child( String.valueOf( "numAns" ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
//                       @Override
//                       public void onSuccess(Uri downloadUrl) {
//                           DownloadManager.Request request = new DownloadManager.Request( Uri.parse( String.valueOf( downloadUrl ) ) );
//                           request.setDescription( "download" );
//                           request.setTitle( "" + downloadUrl + ".mp4" );
//                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                               request.allowScanningByMediaScanner();
//                               request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );
//                           }
//                           request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, "" + downloadUrl + ".mp4" );
//                           // get download service and enqueue file
//                           DownloadManager manager = (DownloadManager) getSystemService( Context.DOWNLOAD_SERVICE );
//                           manager.enqueue( request );
//                           Toast.makeText( OneAnswerActivity.this, "download!!!!", Toast.LENGTH_SHORT ).show();
//
//                           // View video in viewVideo from gallery
//                           MediaController mediaController = new MediaController( OneAnswerActivity.this );
//                           mediaController.setAnchorView( videoView );
//                           //specify the location of media file
//                           Uri uri = Uri.parse( "אחסון פנימי/DCIM/Camera/111.mp4" );
//                           Toast.makeText( OneAnswerActivity.this, "uri" + uri, Toast.LENGTH_SHORT ).show();
//                           Log.d( TAG, "uri = " + uri );
//
//                           //Setting MediaController and URI, then starting the videoView
//                           videoView.setMediaController( mediaController );
//                           videoView.setVideoURI( uri );
//                           videoView.requestFocus();
//                           videoView.start();
//
//
//                       }
//                   } ).addOnFailureListener( new OnFailureListener() {
//                       @Override
//                       public void onFailure(@NonNull Exception exception) {
//                           videoView.setVisibility( View.INVISIBLE );
//                       }
//                   } );
//               }
//           }
//        });


// *******************************  Get the data from the Answer DB  *******************************
        reff = FirebaseDatabase.getInstance().getReference("Answers").child(num_answer);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String content = snapshot.getValue( Answer.class ).getContentAnswer();
                String dateTime = snapshot.getValue( Answer.class ).getDateTimeAnswer();
                String userName = snapshot.getValue( Answer.class ).getUserNameAns();
                id_asking = String.valueOf( snapshot.getValue( Answer.class ).getIdAsking() );
                id_answer = String.valueOf( snapshot.getValue( Answer.class ).getIdAnswering() );
                numLike = snapshot.getValue( Answer.class ).getNumLikes();

                // Show profile image in profileuserID
                storageReff.child( "profile picture/" ).child( id_answer ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Glide.with( getApplicationContext()).load( downloadUrl ).into( profileuserID );
                    }
                } );

                txvname = findViewById( R.id.userNameID );
                txvname.setText( userName );
                txvdateTime = findViewById( R.id.dateID );
                txvdateTime.setText( dateTime );
                txvanswer = findViewById( R.id.text_answerID );
                txvanswer.setText( content );


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

// *******************************  Get score from Users DB  *******************************
        reffUser = FirebaseDatabase.getInstance().getReference("Users");
        reffUser.child(mPreferences.getString( getString( R.string.id ), "" )).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                score = dataSnapshot.getValue( Users.class ).getScore();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

// *******************************  OnClick  *******************************

        likeID.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
               likeID.setImageResource( R.drawable.like_yellow );
               likeID.setEnabled( false );

               // update num like to answer
               reff.child( "numLikes" ).setValue( numLike + 1 );

               // update num like in User DB
               Log.d( TAG, "id_answer = " + id_answer );
               reffUser = FirebaseDatabase.getInstance().getReference( "Users" ).child( id_answer );
               reffUser.addValueEventListener( new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       numL = String.valueOf( snapshot.getValue( Users.class ).getNumLike() );
                       numLNew= Integer.parseInt( numL);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                   }
               } );
                reffUser.child( "numLike" ).setValue( numLNew + 1 );
            }
        });

    }
}


