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

import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;


public class LoginActivity extends AppCompatActivity {

    private Button logIn;
    private EditText password, userName, idUser, phone;
    private String score;
    private CheckBox access_location;
    private boolean check_acces;
    private SharedPreferences sp; //  To know who the user is
    private Intent intent;
    private DatabaseReference reff;
    Users users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login );
        sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        logIn = findViewById(R.id.logInID);
        userName = findViewById(R.id.userNameID);
        password = findViewById(R.id.passwordID);
        idUser = findViewById(R.id.idID);
        phone = findViewById(R.id.phoneID);
        access_location = findViewById(R.id.checkBoxID);

        users = new Users();
        reff = FirebaseDatabase.getInstance().getReference().child("Users");
        score = "0";
        logIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.setUserName(userName.getText().toString().trim());
                users.setPassword(password.getText().toString().trim());
                users.setId(Long.parseLong(idUser.getText().toString().trim()));
                users.setPhone(phone.getText().toString().trim());
                users.setScore(score);

                reff. child(idUser.getText().toString().trim()).setValue(users);
                Toast.makeText(LoginActivity.this,  "insert!", Toast.LENGTH_SHORT).show();

            }
        });

    }


}