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
import com.google.firebase.auth.FirebaseUser;

public class signUp extends AppCompatActivity {
    EditText mail, password1, password2;
    TextView ErrorMail, ErrorPassword1, ErrorPassword2;
    Button SignUp;
    boolean m = false, p1 = false, p2 = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        mail = findViewById(R.id.editTextEmail);
        password1 = findViewById(R.id.editTextPassword);
        password2 = findViewById(R.id.editTextRepeatPassword);
        SignUp = findViewById(R.id.SignUp);

        ErrorMail = findViewById(R.id.errorEmail);
        ErrorPassword1 = findViewById(R.id.errorPassword1);
        ErrorPassword2 = findViewById(R.id.errorPassword2);

        mAuth = FirebaseAuth.getInstance();

        //controllo campo email
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
                    if(m && p1 && p2){
                        SignUp.setEnabled(true);
                        SignUp.setBackgroundColor(ContextCompat.getColor(signUp.this, R.color.blue));
                    }
                } else {
                    // L'indirizzo email non è valido, gestisci di conseguenza
                    m=false;
                    ErrorMail.setVisibility(View.VISIBLE);
                    SignUp.setEnabled(false);
                    SignUp.setBackgroundColor(ContextCompat.getColor(signUp.this, R.color.red));
                }
            }
        });

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String passwordInput = s.toString();
                String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z]).*$";

                if (passwordInput.matches(passwordPattern)) {
                    //la password contiene un numero e una maiuscola
                    p1 = true;
                    ErrorPassword1.setVisibility(View.GONE);
                    if(m && p1 && p2){
                        SignUp.setEnabled(true);
                        SignUp.setBackgroundColor(ContextCompat.getColor(signUp.this, R.color.blue));
                    }
                } else {
                    p1=false;
                    ErrorPassword1.setVisibility(View.VISIBLE);
                    SignUp.setEnabled(false);
                    SignUp.setBackgroundColor(ContextCompat.getColor(signUp.this, R.color.red));
                }
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password1Input = password1.getText().toString();
                String password2Input = s.toString();

                if (password1Input.equals(password2Input)) {
                    // Le password coincidono, esegui le azioni desiderate
                    p2 = true;
                    ErrorPassword2.setVisibility(View.GONE);
                    if(m && p1 && p2){
                        SignUp.setEnabled(true);
                        SignUp.setBackgroundColor(ContextCompat.getColor(signUp.this, R.color.blue));
                    }
                } else {
                    // Le password non coincidono, gestisci di conseguenza
                    p2=false;
                    ErrorPassword2.setVisibility(View.VISIBLE);
                    SignUp.setEnabled(false);
                    SignUp.setBackgroundColor(ContextCompat.getColor(signUp.this, R.color.red));
                }
            }
        });
    }

    public void signUpMethod(View view) {
        String mailS, pw1;
        mailS = String.valueOf(mail.getText());
        pw1 = String.valueOf(password1.getText());

        mAuth.createUserWithEmailAndPassword(mailS, pw1)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registrazione riuscita
                        Toast.makeText(signUp.this, getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(signUp.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        // Se la registrazione non è riuscita, mostra un messaggio all'utente
                        Toast.makeText(signUp.this, getString(R.string.failerSignup) + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void goToLogIn(View view) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        finish();
    }
}