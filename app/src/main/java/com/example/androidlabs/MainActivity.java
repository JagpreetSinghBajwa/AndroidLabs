package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPass;
    private Button buttonLogin;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "email";

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodata);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        // load user's email address from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        email = sharedPreferences.getString(EMAIL, "");

        // Set email address of Email Edit Text
        editTextEmail.setText(email);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profilePage = new Intent(MainActivity.this, ProfileActivity.class);
                profilePage.putExtra("email", editTextEmail.getText().toString());
                startActivity(profilePage);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        // save a user's email address
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, editTextEmail.getText().toString());

        editor.apply();
    }
}
