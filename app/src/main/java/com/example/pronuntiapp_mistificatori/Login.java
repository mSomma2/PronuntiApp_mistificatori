package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText mail, password;
    boolean m=false;
    Button login;
    TextView ErrorMail;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.loginButton);
        ErrorMail = findViewById(R.id.errorEmail);

        mAuth = FirebaseAuth.getInstance();

        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailInput = s.toString();

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    // L'indirizzo email è valido, esegui le azioni desiderate
                    m = true;
                    ErrorMail.setVisibility(View.GONE);
                    if(m){
                        login.setEnabled(true);
                        login.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.blue));
                    }
                } else {
                    // L'indirizzo email non è valido, gestisci di conseguenza
                    m=false;
                    ErrorMail.setVisibility(View.VISIBLE);
                    login.setEnabled(false);
                    login.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.red));
                }
            }
        });
    }

    public void goToSignUp(View view) {
        Intent i = new Intent(this, signUp.class);
        startActivity(i);
        finish();
    }

    public void logga(View view) {
        String m = mail.getText().toString(), p = password.getText().toString();
        mAuth.signInWithEmailAndPassword(m, p)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login riuscito
                        Toast.makeText(Login.this, getString(R.string.welcomeBack), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Login.this, mainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        // Se il login non è riuscito, mostra un messaggio all'utente
                        ErrorMail.setText(R.string.failedLogin);
                        ErrorMail.setVisibility(View.VISIBLE);
                    }
                });

    }
}