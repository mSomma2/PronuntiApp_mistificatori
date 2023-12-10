package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

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
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("ABC/Pazienti/001");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Processa i dati qui
                if (dataSnapshot.exists()) {
                    // Ottieni i dati
                    String nome = dataSnapshot.child("Nome").getValue(String.class);
                    String sesso = dataSnapshot.child("Sesso").getValue(String.class);

                    // Fai qualcosa con i dati
                    Toast.makeText(MainActivity.this, "Nome: " + nome + ", Sesso: " + sesso, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Dati non trovati", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Errore nel leggere i dati");

            }
        });

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

    public void showDialog(View view) {
        final Dialog dialog = new Dialog(this);

        // Imposta il layout del dialog
        dialog.setContentView(R.layout.dialog_kidcode_layout);

        // Trova gli elementi all'interno del dialog
        final EditText editTextCode = dialog.findViewById(R.id.editTextCode);
        Button buttonSubmit = dialog.findViewById(R.id.buttonSubmit);

        // Aggiungi un listener al button nel dialog
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gestisci l'azione del button qui
                String codiceInserito = editTextCode.getText().toString();

                String percorsoGenitoreFigli = "genitori/" + user.getUid() + "/figli";

                databaseReference = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app").getReference(percorsoGenitoreFigli);

                // Genera un nuovo ID univoco per il figlio
                String nuovoFiglioKey = databaseReference.push().getKey();

                // Aggiungi il codiceInserito alla lista dei figli sotto il nuovo ID
                assert nuovoFiglioKey != null;
                databaseReference.child(nuovoFiglioKey).setValue(codiceInserito);
                // Chiudi il dialog
                dialog.dismiss();
            }
        });

        // Mostra il dialog
        dialog.show();
    }
}