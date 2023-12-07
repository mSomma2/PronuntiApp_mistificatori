package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToSignUp(View view) {
        Intent i = new Intent(this, singUp.class);
        startActivity(i);
        finish();
    }
}