package com.tehilafi.ama;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tehilafi.ama.db.Users;


public class LoginActivity extends Activity {

    private Button logIn;
    private EditText password, userName, idUser, phone;
    private String token;
    private DatabaseReference reff;
    Users users;
    Users chec_users;

    public static final String TAG = "MyTag";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

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

        // Find the token of the device
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){
                            token=task.getResult().getToken();
                            Log.d(TAG, "onComplete: Token: "+token);
                        }else{
                        }

                    }
                });


        logIn = findViewById( R.id.logInID );
        userName = findViewById( R.id.userNameID );
        password = findViewById( R.id.passwordID );
        idUser = findViewById( R.id.iduserID);
        phone = findViewById( R.id.phoneID );

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        // Init
        users = new Users();
        chec_users=new Users();
        reff = FirebaseDatabase.getInstance().getReference().child( "Users" );

        logIn.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                users.setUserName( userName.getText().toString().trim() );
//                users.setPassword( password.getText().toString().trim() );
//                users.setId(Integer.parseInt(idUser.getText().toString().trim()));
//                users.setPhone( phone.getText().toString().trim() );
//                users.setScore(score);
//                users.setLatitude(0.0);
//                users.setLongitude(0.0);
//                users.setToken(token);
//                users.setImportantQuestions(3);
//                users.setNumLike(0);
//                users.setNumLike(0);

                boolean check_token, check_username, check_password, check_id, check_phone;

                //If one of the details is missing:

                if(token.equals( "" ))
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
                if(check_username && check_password  && check_phone && check_id && check_token){
                    reff.child(idUser.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot){

                        // if ID user already exists
                        if (snapshot.exists()) {
                            Query myQueryUser = reff.orderByChild("id");
                            myQueryUser.addChildEventListener( new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if(userName.getText().toString().trim().equals(snapshot.getValue( Users.class ).getUserName()) && password.getText().toString().trim().equals(snapshot.getValue( Users.class ).getPassword()) && phone.getText().toString().trim().equals(snapshot.getValue( Users.class ).getPhone())){
                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                        Toast.makeText(LoginActivity.this, "שמשתמש מחובר לאפליקציה. הפרטים לא נכונים", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        // if ID user dosent exists
                        else {
                            users.setUserName( userName.getText().toString().trim() );
                            users.setPassword( password.getText().toString().trim() );
                            users.setId(Integer.parseInt(idUser.getText().toString().trim()));
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


                            reff.child( idUser.getText().toString().trim() ).setValue( users );

                            String id = idUser.getText().toString();
                            String name = userName.getText().toString();
                            String pas = password.getText().toString();
                            String ph = phone.getText().toString();
                            String myToken = token;

                            mEditor.putString(getString(R.string.id), id);
                            mEditor.putString(getString(R.string.name), name);
                            mEditor.putString(getString(R.string.pas), pas);
                            mEditor.putString(getString(R.string.ph), ph);
                            mEditor.putString(getString(R.string.myToken), myToken);
                            mEditor.commit();

                            Toast.makeText( LoginActivity.this, "insert!", Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                    });
                }
                else
                   Toast.makeText(LoginActivity.this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();
            }
        });
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


