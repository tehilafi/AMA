

package com.tehilafi.ama;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;


public class LoginActivity extends AppCompatActivity {

    private Button logIn;
    private EditText password, userName, idUser, phone;
    private String score;
    private CheckBox access_location;
    private boolean check_acces;
    private SharedPreferences sp; //  To know who the user is
    private Intent intent;
    private DatabaseReference reff;
    DatabaseReference reff1;
    Users users;
    Users chec_users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        sp = getSharedPreferences( "MyPref", Context.MODE_PRIVATE );

        logIn = findViewById( R.id.logInID );
        userName = findViewById( R.id.userNameID );
        password = findViewById( R.id.passwordID );
        idUser = findViewById( R.id.idID );
        phone = findViewById( R.id.phoneID );
        access_location = findViewById( R.id.checkBoxID );


        users = new Users();
        chec_users=new Users();
        reff = FirebaseDatabase.getInstance().getReference().child( "Users" );
        score = "0";
        logIn.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.setUserName( userName.getText().toString().trim() );
                users.setPassword( password.getText().toString().trim() );
                users.setId(Integer.parseInt(idUser.getText().toString().trim()));
                users.setPhone( phone.getText().toString().trim() );
                users.setScore( score );
                if (access_location.isChecked())
                    check_acces = true;
                else
                    check_acces = false;
                boolean flag = false, check_username, check_password, check_id, check_phone;

                //If one of the details is missing:

                if (userName.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "Missing userName", Toast.LENGTH_LONG ).show();
                    check_username = false;
                } else
                    check_username = true;
                if (password.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "Missing password", Toast.LENGTH_LONG ).show();
                    check_password = false;
                } else
                    check_password = true;
                if (idUser.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "Missing id", Toast.LENGTH_LONG ).show();
                    check_id = false;
                } else {
                    if (validId(Integer.parseInt( idUser.getText().toString())))
                        check_id = true;
                    else
                        check_id = false;
                }
                if (phone.getText().toString().equals( "" )) {
                    Toast.makeText( LoginActivity.this, "Missing phone", Toast.LENGTH_LONG ).show();
                    check_phone = false;
                } else
                    check_phone = true;
                if(!flag&& check_username && check_password  && check_phone && check_acces && check_id){
                reff.child(idUser.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "id alredy exits", Toast.LENGTH_LONG).show();
                        }
                        else {
                            users.setUserName( userName.getText().toString().trim() );
                            users.setPassword( password.getText().toString().trim() );
                            users.setId(Integer.parseInt(idUser.getText().toString().trim()));
                            users.setPhone( phone.getText().toString().trim() );
                            users.setScore( score );

                            reff.child( idUser.getText().toString().trim() ).setValue( users );
                            Toast.makeText( LoginActivity.this, "insert!", Toast.LENGTH_SHORT ).show();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

                }

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
        // find how much need to complete to divide the digit by ten.
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
