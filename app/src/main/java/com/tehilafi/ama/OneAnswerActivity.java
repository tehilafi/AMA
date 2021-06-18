package com.tehilafi.ama;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private TextView txvname, txvdateTime,  txvanswer, numLikeID;
    private String num_answer, id_asking, id_answer;
    public static final String TAG = "MyTag";
    DatabaseReference reff, reffUser;
    ImageView imageView, starID, transillumination, closeID, add_picID, add_videoID, likeID;
    VideoView videoView;
    private SharedPreferences mPreferences;
    private CircleImageView profileuserID;
    private StorageReference storageReff;
    FirebaseStorage storage;
    private int numLike, score;



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
        transillumination = findViewById( R.id.transilluminationID );
        add_picID = findViewById( R.id.add_picID );
        add_videoID = findViewById( R.id.add_videoID );
        likeID = findViewById( R.id.likeID );
        closeID = findViewById( R.id.closeID );

        storageReff.child("picture answer/").child(num_answer).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                add_picID.setImageResource(R.drawable.add_pic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                add_picID.setImageResource(R.drawable.transillumination);
            }
        });

        storageReff.child("video answer/").child(num_answer).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                add_videoID.setImageResource(R.drawable.add_video);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                add_videoID.setImageResource(R.drawable.transillumination);
            }
        });

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
                        Glide.with( OneAnswerActivity.this ).load( downloadUrl ).into( profileuserID );
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
        reffUser = FirebaseDatabase.getInstance().getReference("Users").child(mPreferences.getString( getString( R.string.id ), "" ));
        reffUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                score = dataSnapshot.getValue( Users.class ).getScore();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

// *******************************  OnClick  *******************************

        closeID.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                closeID.setImageResource( R.drawable.transillumination );
                transillumination.setImageResource( R.drawable.transillumination );
                imageView.setImageResource( R.drawable.transillumination );

                // update num like to answer
                reff.child( "numLikes" ).setValue(numLike+1);
            }
        } );

        likeID.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                numLikeID.setText("numLike+1");
                likeID.setImageResource(R.drawable.like_yellow);
                likeID.setEnabled(false);

                // update num like to answer user
                reffUser = FirebaseDatabase.getInstance().getReference("Users").child(id_answer).child("numlike");
                reffUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int numL = dataSnapshot.getValue( Users.class ).getNumLike();
                        reffUser.setValue(numL+1);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });




            }
        } );

        add_picID.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (score < 150)
                    Toast.makeText( OneAnswerActivity.this, "ברמת הדירוג שלך אי אפשר לצפות בתמונות", Toast.LENGTH_SHORT ).show();
                else {
                    Toast.makeText( OneAnswerActivity.this, "viewPic", Toast.LENGTH_SHORT ).show();
                    // Show the image in profileID
                    storageReff.child( "picture answer/" ).child(num_answer).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            transillumination.setImageResource( R.drawable.half_trans );
                            Glide.with( OneAnswerActivity.this ).load( downloadUrl ).into( imageView );
                            closeID.setImageResource( R.drawable.ic_baseline_close_24 );
                        }
                    } );
                }
            }
        } );

//        add_videoID.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(android.view.View view) {
//                if(score < 150)
//                    Toast.makeText( OneAnswerActivity.this, "ברמת דירוג שלך אי אפשר לצפות בסרטונים", Toast.LENGTH_SHORT ).show();
//                else if(score >= 150 && score < 500)
//                    Toast.makeText( OneAnswerActivity.this, "ברמת דירוג שלך אי אפשר לצפות בסרטונים", Toast.LENGTH_SHORT ).show();
//                else{
////            Toast.makeText( this, "numAns = " + numAns, Toast.LENGTH_SHORT ).show();
//                    storageReff.child("video answer/").child( String.valueOf( "numAns" ) ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>()
//                    {
//                        @Override
//                        public void onSuccess(Uri downloadUrl) {
//                            transillumination.setImageResource(R.drawable.half_trans);
//                            DownloadManager.Request request = new DownloadManager.Request( Uri.parse( String.valueOf( downloadUrl ) ) );
//                            request.setDescription( "download" );
//                            request.setTitle( "" + downloadUrl+".mp4" );
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                                request.allowScanningByMediaScanner();
//                                request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );
//                            }
//                            request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, "" + downloadUrl + ".mp4" );
////                // get download service and enqueue file
//                            DownloadManager manager = (DownloadManager) getSystemService( Context.DOWNLOAD_SERVICE );
//                            manager.enqueue( request );
//                            Toast.makeText( AnswerDetailActivity.this, "download!!!!", Toast.LENGTH_SHORT ).show();
//
//                            // View video in viewVideo from gallery
//                            MediaController mediaController= new MediaController(AnswerDetailActivity.this);
//                            mediaController.setAnchorView(videoView);
//                            //specify the location of media file
//                            Uri uri = Uri.parse("אחסון פנימי/DCIM/Camera/111.mp4");
//                            Toast.makeText( AnswerDetailActivity.this, "uri"+ uri, Toast.LENGTH_SHORT ).show();
//                            Log.d(TAG, "uri = "+ uri);
//
//                            //Setting MediaController and URI, then starting the videoView
//                            videoView.setMediaController(mediaController);
//                            videoView.setVideoURI(uri);
//                            videoView.requestFocus();
//                            videoView.start();
//
//                            closeID.setImageResource(R.drawable.ic_baseline_close_24);
//
//                        }
//                    });
//                    //        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.numAns);
////        MediaController mediaController= new MediaController(AnswerDetailActivity.this);
////        mediaController.setAnchorView(videoView);
////        videoView.setMediaController(mediaController);
//
//                }
//            }
//        } );
    }
}


