package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class CorrezioneRipetizione extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    FirebaseUser currentUser;
    private String parola, codice, url;
    private TextToSpeechManager textToSpeechManager;
    private CardView cardView;
    int count = 0;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile, data, nome;
    private ImageButton recAudio, playAudio, wrong, correct;
    private CardView cardWrong, cardCorrect;
    boolean isRecording = false;
    private DatabaseReference databaseReference;
    private Integer coin, punteggio;
    private boolean esito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correzione_ripetizione);

        codice = getIntent().getStringExtra("codice");
        String esercizio = getIntent().getStringExtra("esercizio");
        data = getIntent().getStringExtra("data");
        url = getIntent().getStringExtra("url");
        coin = getIntent().getIntExtra("coin", 0);
        punteggio = getIntent().getIntExtra("punteggio", 0);
        esito = getIntent().getBooleanExtra("esito", false);
        nome = getIntent().getStringExtra("nome");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);// Abilita il pulsante per tornare indietro
            actionBar.setTitle(data);
        }

        correct = findViewById(R.id.correct);
        wrong = findViewById(R.id.wrong);
        cardCorrect = findViewById(R.id.cardCorrect);
        cardWrong = findViewById(R.id.cardWrong);

        Initialize(esercizio);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        textToSpeechManager = new TextToSpeechManager(this);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(CorrezioneRipetizione.this, CorrezioneEsercizi.class);
            i.putExtra("codice", codice);
            i.putExtra("nome", nome);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CorrezioneRipetizione.this, CorrezioneEsercizi.class);
        i.putExtra("codice", codice);
        i.putExtra("nome", nome);
        startActivity(i);
        finish();
    }

    private void Initialize(String es){
        String percorso = "logopedisti/ABC/esercizi/" + es;
        DatabaseReference myRef = database.getReference(percorso);

        if(esito){
            correct.setEnabled(false);
            cardWrong.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        }else{
            wrong.setEnabled(false);
            cardCorrect.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    parola = dataSnapshot.child("parola").getValue(String.class);
                } else {
                    System.out.println("Il dato non esiste nel percorso specificato.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Lettura del dato annullata: " + error.getMessage());
            }
        });
    }

    public void riproduci(View view) {
        if (!mediaPlayer.isPlaying()) {
            try {
                // Avvia la riproduzione
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void playAudio(View view) {
        textToSpeechManager.speak(parola, 1f);
    }


    private void dialog(int c){
        AlertDialog.Builder builder = new AlertDialog.Builder(CorrezioneRipetizione.this);
        builder.setTitle(CorrezioneRipetizione.this.getString(R.string.modCorrezioneTitolo))
                .setMessage(CorrezioneRipetizione.this.getString(R.string.modCorrezioneMsg))
                .setPositiveButton(CorrezioneRipetizione.this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Azioni da eseguire quando l'utente risponde "sì"
                        DatabaseReference db = database.getReference("logopedisti/ABC/Pazienti/" + codice + "/Esercizi/"+data);
                        DatabaseReference dbCoin = database.getReference("logopedisti/ABC/Pazienti/" + codice);
                        if(c==1){ //setWrong
                            db.child("esito").setValue(false);
                            cardCorrect.setCardBackgroundColor(ContextCompat.getColor(CorrezioneRipetizione.this, android.R.color.darker_gray));
                            cardWrong.setCardBackgroundColor(ContextCompat.getColor(CorrezioneRipetizione.this, android.R.color.holo_red_light));
                            wrong.setEnabled(false);
                            correct.setEnabled(true);
                            coin -= 10;
                            punteggio -= 10;
                            if(coin < 0) coin = 0;
                            if(punteggio < 0) punteggio = 0;
                            dbCoin.child("coin").setValue(coin);
                            dbCoin.child("punteggio").setValue(punteggio);
                        }else{ //setCorrect
                            db.child("esito").setValue(true);
                            cardWrong.setCardBackgroundColor(ContextCompat.getColor(CorrezioneRipetizione.this, android.R.color.darker_gray));
                            cardCorrect.setCardBackgroundColor(ContextCompat.getColor(CorrezioneRipetizione.this, android.R.color.holo_green_light));
                            correct.setEnabled(false);
                            wrong.setEnabled(true);
                            coin += 10;
                            punteggio += 10;
                            dbCoin.child("coin").setValue(coin);
                            dbCoin.child("punteggio").setValue(punteggio);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Azioni da eseguire quando l'utente risponde "no"

                    }
                })
                .setCancelable(false)
                .show();
    }

    public void setWrong(View view) {
        dialog(1);
    }

    public void setCorrect(View view) {
        dialog(2);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeechManager != null) {
            textToSpeechManager.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}