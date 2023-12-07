package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        // Verifica lo stato di accesso
        if (!isLoggedIn()) {
            // Se non è autenticato, avvia l'activity di login
            startLoginActivity();
            // Chiudi questa activity per evitare che l'utente torni indietro
            finish();
        }
    }

    private boolean isLoggedIn() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null;
    }

    // Metodo per avviare l'activity di login
    private void startLoginActivity() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

}