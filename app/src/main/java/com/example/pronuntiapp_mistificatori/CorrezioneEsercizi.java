package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CorrezioneEsercizi extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private String codiceBimbo, nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<Integer> tipologie = new ArrayList<>();
        setContentView(R.layout.activity_correzione_esercizi);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Abilita il pulsante per tornare indietro
        }

        codiceBimbo = getIntent().getStringExtra("codice");
        nome = getIntent().getStringExtra("nome");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        TextView nomeTV = findViewById(R.id.nomeBimbo);
        nomeTV.setText(nome);

        LinearLayout lista = findViewById(R.id.listaCorrezioni);
        DatabaseReference databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/Esercizi/");
        DatabaseReference tipoReference = database.getReference("logopedisti/ABC/esercizi/");

        tipoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tipologie.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Integer tipologia = childSnapshot.child("tipologia").getValue(Integer.class);
                    tipologie.add(tipologia);

                }

                databaseReference.orderByKey().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            String date = dateSnapshot.getKey();
                            Boolean esito = dateSnapshot.child("esito").getValue(Boolean.class);
                            Integer tipo = dateSnapshot.child("es_assegnato").getValue(Integer.class);

                            View childView = getLayoutInflater().inflate(R.layout.list_element_parent, null);
                            TextView textViewData = childView.findViewById(R.id.esData);
                            textViewData.setText(date.substring(0, 5));
                            TextView textViewTipo = childView.findViewById(R.id.esTipo);
                            LinearLayout bordi = childView.findViewById(R.id.bordo);
                            // Aggiungi il codice per leggere la tipologia dall'altro percorso
                            if(tipo != null) {
                                if (tipologie.get(tipo-1) == 1){
                                    textViewTipo.setText("TIPO: Denominazione");
                                    childView.setOnClickListener(v -> {
                                        String url = dateSnapshot.child("audio").getValue(String.class);
                                        Intent i = new Intent(CorrezioneEsercizi.this, CorrezioneDenominazione.class);
                                        i.putExtra("esercizio", tipo.toString());
                                        i.putExtra("codice", codiceBimbo);
                                        i.putExtra("data", date);
                                        i.putExtra("url", url);
                                        i.putExtra("esito", esito);
                                        i.putExtra("nome", nome);
                                        startActivity(i);
                                        finish();
                                    });
                                }
                                else if (tipologie.get(tipo-1) == 2){
                                    textViewTipo.setText("TIPO: Sequenze");
                                    childView.setOnClickListener(v -> {

                                    });
                                }
                                else if (tipologie.get(tipo-1) == 3){
                                    textViewTipo.setText("TIPO: Riconoscimento");
                                    childView.setOnClickListener(v -> {

                                    });
                                }


                                ImageView result = childView.findViewById(R.id.esResult);

                                if (esito != null && esito){
                                    result.setImageResource(R.drawable.correct);
                                    bordi.setForeground(getResources().getDrawable(R.drawable.border_drawable_green));
                                }
                                else if (esito != null) result.setImageResource(R.drawable.wrong);



                                lista.addView(childView);

                                Space space = new Space(CorrezioneEsercizi.this);
                                space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80)); // Altezza dello spazio: 16dp
                                lista.addView(space);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Gestisci eventuali errori di lettura dal database
                        Log.e("TAGGA", "Errore durante la lettura dei dati dal database", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Torna indietro quando il pulsante viene premuto
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}