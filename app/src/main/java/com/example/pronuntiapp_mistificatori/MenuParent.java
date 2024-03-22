package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuParent extends AppCompatActivity {

    private String codice, nome;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parentmenu);

        nome = getIntent().getStringExtra("nome");
        codice = getIntent().getStringExtra("code");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(nome);
        }

        TextView name, code;

        name = findViewById(R.id.kidName);
        code = findViewById(R.id.kidCode);
        name.setText(nome);
        code.setText(codice);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Gestisci il clic sul pulsante "Up"
            super.onBackPressed(); // Puoi anche eseguire altre azioni qui
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void background(View view) {
        Intent i = new Intent(MenuParent.this, SelectScenario.class);
        i.putExtra("codice", codice);
        startActivity(i);
    }

    public void start(View view) {
        Intent i = new Intent(MenuParent.this, MappaBambino.class);


        i.putExtra("codice", codice);
        startActivity(i);
    }

    public void therapy(View view) {
    }

    public void correction(View view) {
        Intent i = new Intent(MenuParent.this, CorrezioneEsercizi.class);
        i.putExtra("codice", codice);
        i.putExtra("nome", nome);
        startActivity(i);
    }

    public void deleteKid(View view) {
        new AlertDialog.Builder(MenuParent.this)
                .setTitle(getString(R.string.deleteProfile))
                .setMessage(getString(R.string.confirmDeleteKid) + " " + nome + "?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    DatabaseReference genitoriRef = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("genitori").child(currentUser.getUid()).child("figli");

// Esegui una query per trovare l'ID del nodo con il valore "003"
                    genitoriRef.orderByValue().equalTo(codice).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String idNodo = snapshot.getKey();

                                // Una volta ottenuto l'ID, puoi rimuovere il nodo
                                assert idNodo != null;
                                DatabaseReference nodoCodiceRef = genitoriRef.child(idNodo);
                                nodoCodiceRef.removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Rimozione riuscita
                                        Log.d("TAG", "Nodo eliminato con successo");
                                        Intent intent = new Intent(MenuParent.this, mainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Gestire l'errore durante la rimozione
                                        Log.e("TAG", "Errore durante l'eliminazione del nodo", task.getException());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MenuParent.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }
}