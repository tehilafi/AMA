package com.tehilafi.ama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tehilafi.ama.db.Users;

import java.util.HashMap;

public class LoginActivity extends Activity {
    private Button logIn;
    private EditText password, userName, idUser, phone;
    private DatabaseReference reff, reffExist;
    Users users, chec_users;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private StorageReference storageReff, reffS = null;
    private String usern, pass, phon, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        // Hide the Activity Status Bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // Hide the Activity  Bar
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }
        // Init
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        users = new Users();
        chec_users=new Users();
        // get the Firebase  storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        // Find the token of the device
        token = MyFirebaseMessagingService.getToken(LoginActivity.this);

        logIn = findViewById( R.id.logInID );
        userName = findViewById( R.id.userNameID );
        password = findViewById( R.id.passwordID );
        idUser = findViewById( R.id.iduserID);
        phone = findViewById( R.id.phoneID );

        reff = FirebaseDatabase.getInstance().getReference().child( "Users" );
        reffExist = FirebaseDatabase.getInstance().getReference().child( "Users" );

        logIn.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check_token, check_username, check_password, check_id, check_phone;
                //Check if one of the details is missing:
                if( token == null || token.equals(""))
                    check_token = false;
                else
                    check_token = true;

                if (userName.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "חסר שם משתמש", Toast.LENGTH_LONG ).show();
                    check_username = false;
                } else
                    check_username = true;

                if (password.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "חסר סיסמה", Toast.LENGTH_LONG ).show();
                    check_password = false;
                } else
                    check_password = true;

                if (idUser.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this,"חסר ת.ז", Toast.LENGTH_LONG ).show();
                    check_id = false;
                } else {
                    if (validId(Integer.parseInt( idUser.getText().toString())))
                        check_id = true;
                    else
                        check_id = false;
                }

                if (phone.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "חסר מספר טלפון", Toast.LENGTH_LONG ).show();
                    check_phone = false;
                } else
                    check_phone = true;

//************************************  Save the data in Users DB  *************************************
                String name = userName.getText().toString();
                String pas = password.getText().toString();
                String ph = phone.getText().toString();

                String id_without_0 = remove0(idUser.getText().toString());

                if(check_username && check_password  && check_phone && check_id && check_token){
                    reff.child(id_without_0).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot){
//************************************  If ID user dosent exists  *************************************
                            if (! snapshot.exists()) {
                                users.setUserName( userName.getText().toString().trim() );
                                users.setPassword( password.getText().toString().trim() );
                                users.setId(Integer.parseInt(id_without_0));
                                users.setPhone( phone.getText().toString().trim() );
                                users.setScore(10);
                                users.setLatitude(0.0);
                                users.setLongitude(0.0);
                                users.setToken(token);
                                users.setImportantQuestions(3);
                                users.setNumLike(0);
                                users.setNumAnswer(0);
                                users.setNumPicture(0);
                                users.setNumVideo(0);

                                reff.child(id_without_0).setValue( users );

                                // Put default profile image in firebase storage
                                reffS = storageReff.child( "profile picture/" + id_without_0);
                                Uri imageUri = Uri.parse("android.resource://com.tehilafi.ama/" + R.drawable.profil);
                                reffS.putFile(imageUri).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());
                                        Uri downloadUri = uriTask.getResult();
                                        if(uriTask.isSuccessful()){
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("title", "" + id_without_0);
                                            hashMap.put("contentUri", "" + downloadUri);
                                        }
                                    }
                                }).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                } );

                                mEditor.putString(getString(R.string.id), id_without_0);
                                mEditor.putString(getString(R.string.name), name);
                                mEditor.putString(getString(R.string.pas), pas);
                                mEditor.putString(getString(R.string.ph), ph);
                                mEditor.putString(getString(R.string.myToken), token);
                                mEditor.commit();

                                //set activity_executed inside insert() method.
                                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edt = pref.edit();
                                edt.putBoolean("activity_executed", true);
                                edt.commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
//************************************  If ID user already exists  *************************************
                            else if(snapshot.exists()) {
                                DatabaseReference reffU = reff.child(id_without_0);
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        usern = dataSnapshot.getValue(Users.class).getUserName();
                                        pass = dataSnapshot.getValue(Users.class).getPassword();
                                        phon = dataSnapshot.getValue(Users.class).getPhone();

                                        reffExist.child(id_without_0).child("token").setValue(token);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                };
                                reffU.addListenerForSingleValueEvent(valueEventListener);
                                if(userName.getText().toString().trim().equals( usern ) && password.getText().toString().trim().equals( pass ) && phone.getText().toString().trim().equals(phon)) {
                                    mEditor.putString(getString(R.string.id), id_without_0);
                                    mEditor.putString(getString(R.string.name), name);
                                    mEditor.putString(getString(R.string.pas), pas);
                                    mEditor.putString(getString(R.string.ph), ph);
                                    mEditor.putString(getString(R.string.myToken), token);
                                    mEditor.commit();

                                    users.setToken(token);

                                    //set activity_executed inside insert() method.
                                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edt = pref.edit();
                                    edt.putBoolean("activity_executed", true);
                                    edt.commit();

                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }

    // Delete all the leading zeros in the ID number
    private String remove0(String idString) {
        String idCorrect = idString.replaceFirst("^0+(?!$)", "");
        return idCorrect;
    }

    // Function check if ID is valid
    public boolean validId(int idNumber){
        int sum = 0, i = 1;
        int check_digit = idNumber % 10; // the last digit its the check digit
        int temp = idNumber / 10;

        for ( i = 1; i < 9; i++)
        {
            if (i % 2 == 0)
            {
                sum += temp % 10; // the digit * 1
                temp = temp / 10;
            }
            else // if (i % 2 != 0)
            {
                int counter = (temp % 10) * 2; // the digit * 2
                temp = temp / 10;
                if (counter > 9) //if tje multiplied number is more than one digit, the digits are summed
                {
                    int n = 0;
                    while (counter != 0)
                    {
                        n += counter % 10;
                        counter = counter / 10;
                    }
                    sum += n;
                }
                else
                    sum += counter;
            }
        }
        //find how much need to complete to divide the digit by ten.
        int balance = 0;
        while (sum % 10 != 0)
        {
            balance++;
            sum++;
        }
        if (balance == check_digit)
            return true;
        else
            return false;
    }
}


