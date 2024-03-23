package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectScenario extends AppCompatActivity {

    private String codiceBimbo, nome;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseUser currentUser;
    private ImageView scena, character;
    private Button set, left, right;
    private String scenarioSelected, scenarioDefault;
    private int[] characterImages;
    private int currentIndex = 0;
    private DatabaseReference myRef;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scenario);

        codiceBimbo = getIntent().getStringExtra("codice");
        nome = getIntent().getStringExtra("nome");
        scena = findViewById(R.id.backGround);
        character = findViewById(R.id.showCharacter);
        set = findViewById(R.id.confirmButton);
        left = findViewById(R.id.btnLeft);
        right = findViewById(R.id.btnRight);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Abilita il pulsante per tornare indietro
            actionBar.setTitle(nome + " - " + SelectScenario.this.getString(R.string.scenario));
        }

        String percorso = "logopedisti/ABC/Pazienti/" + codiceBimbo;
        myRef = database.getReference(percorso);
        myRef.addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            scenarioSelected = snapshot.child("scenario").getValue(String.class);
            assert scenarioSelected != null;
            scenarioDefault = scenarioSelected;
            if(scenarioSelected.equals("A")){
                scena.setImageResource(R.drawable.sfondo1);
                characterImages = new int[]{R.drawable.personaggio_a, R.drawable.personaggio_b, R.drawable.personaggio_c,  R.drawable.personaggio_d,  R.drawable.personaggio_e};
                startImageSequence();
            }else if(scenarioSelected.equals("B")){
                scena.setImageResource(R.drawable.sfondo2);
                characterImages = new int[]{R.drawable.personaggio_wow, R.drawable.personaggio_star, R.drawable.personaggio_sciolto,  R.drawable.personaggio_love,  R.drawable.personaggio_cool};
                startImageSequence();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    private void startImageSequence() {
        myRef.removeEventListener(valueEventListener);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Mostra l'immagine corrente
                character.setImageResource(characterImages[currentIndex]);

                // Incrementa l'indice per passare all'immagine successiva
                currentIndex++;

                // Se abbiamo mostrato tutte le immagini, ricomincia dall'inizio
                if (currentIndex >= characterImages.length) {
                    currentIndex = 0;
                }

                // Ripeti il processo dopo un certo intervallo di tempo (ad esempio 3 secondi)
                handler.postDelayed(this, 1800);
            }
        }, 0); // Avvia immediatamente la sequenza
    }

    public void switchScnario(View view) {

        character.setVisibility(View.GONE);
        left.setEnabled(false);
        right.setEnabled(false);
        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Rendi di nuovo visibile l'ImageView character dopo 500 millisecondi (0.5 secondi)
                character.setVisibility(View.VISIBLE);
                left.setEnabled(true);
                right.setEnabled(true);
            }
        }, 1100); // Imposta un ritardo di 500 millisecondi

        if(scenarioSelected.equals("B")){
            scena.setImageResource(R.drawable.sfondo1);
            characterImages = new int[]{R.drawable.personaggio_a, R.drawable.personaggio_b, R.drawable.personaggio_c,  R.drawable.personaggio_d,  R.drawable.personaggio_e};
            scenarioSelected = "A";
        }else if(scenarioSelected.equals("A")){
            scena.setImageResource(R.drawable.sfondo2);
            characterImages = new int[]{R.drawable.personaggio_wow, R.drawable.personaggio_star, R.drawable.personaggio_sciolto,  R.drawable.personaggio_love,  R.drawable.personaggio_cool};
            scenarioSelected = "B";
        }

        if(!scenarioSelected.equals(scenarioDefault)){
            set.setText(SelectScenario.this.getString(R.string.select));
            set.setBackgroundColor(SelectScenario.this.getColor(R.color.blue));
            set.setEnabled(true);
        }else{
            set.setText(SelectScenario.this.getString(R.string.selected));
            set.setBackgroundColor(SelectScenario.this.getColor(R.color.green_light));
            set.setEnabled(false);
        }
    }

    public void setScenario(View view) {
        myRef.child("scenario").setValue(scenarioSelected);
        scenarioDefault = scenarioSelected;
        set.setText(SelectScenario.this.getString(R.string.selected));
        set.setBackgroundColor(SelectScenario.this.getColor(R.color.green_light));
        set.setEnabled(false);
    }
}