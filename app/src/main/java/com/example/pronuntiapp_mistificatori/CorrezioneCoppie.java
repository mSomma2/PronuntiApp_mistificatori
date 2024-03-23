package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CorrezioneCoppie extends AppCompatActivity {

    private ImageView img1, img2;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    FirebaseUser currentUser;
    private String parola, codice, url;
    private TextToSpeechManager textToSpeechManager;
    private MediaPlayer mediaPlayer;
    private String data, nome;
    private ImageButton playAudio, wrong, correct;
    private CardView cardWrong, cardCorrect;
    private Integer coin, punteggio, risposta;
    private DatabaseReference databaseReference;
    private boolean esito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correzione_coppie);

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
            actionBar.setDisplayHomeAsUpEnabled(true); // Abilita il pulsante per tornare indietro
            actionBar.setTitle(data);
        }

        cardCorrect = findViewById(R.id.cardCorrect);
        cardWrong = findViewById(R.id.cardWrong);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);

        Initialize(esercizio);
        databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codice);
        textToSpeechManager = new TextToSpeechManager(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(CorrezioneCoppie.this, CorrezioneEsercizi.class);
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
        Intent i = new Intent(CorrezioneCoppie.this, CorrezioneEsercizi.class);
        i.putExtra("codice", codice);
        i.putExtra("nome", nome);
        startActivity(i);
        finish();
    }


    void Initialize(String es){
        String percorso = "logopedisti/ABC/esercizi/" + es;
        DatabaseReference myRef = database.getReference(percorso);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String url1 = dataSnapshot.child("immagine1").getValue(String.class);
                    String url2 = dataSnapshot.child("immagine2").getValue(String.class);
                    parola = dataSnapshot.child("parola").getValue(String.class);
                    risposta = dataSnapshot.child("risposta").getValue(Integer.class);
                    Glide.with(CorrezioneCoppie.this)
                            .load(url1)
                            .into(img1);
                    Glide.with(CorrezioneCoppie.this)
                            .load(url2)
                            .into(img2);
                    checkResult();
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

    private void checkResult(){
        if(esito){
            cardWrong.setVisibility(View.GONE);
            if(risposta == 1){
                GradientDrawable borderDrawable = new GradientDrawable();
                borderDrawable.setShape(GradientDrawable.RECTANGLE);
                borderDrawable.setStroke(5, Color.GREEN); // Larghezza e colore del bordo
                borderDrawable.setColor(Color.TRANSPARENT);
                img1.setBackground(borderDrawable);
            }else{
                GradientDrawable borderDrawable = new GradientDrawable();
                borderDrawable.setShape(GradientDrawable.RECTANGLE);
                borderDrawable.setStroke(5, Color.GREEN); // Larghezza e colore del bordo
                borderDrawable.setColor(Color.TRANSPARENT);
                img2.setBackground(borderDrawable);
            }
        }else{
            cardCorrect.setVisibility(View.GONE);
            if(risposta == 1){
                GradientDrawable borderDrawable = new GradientDrawable();
                borderDrawable.setShape(GradientDrawable.RECTANGLE);
                borderDrawable.setStroke(5, Color.RED); // Larghezza e colore del bordo
                borderDrawable.setColor(Color.TRANSPARENT);
                img2.setBackground(borderDrawable);
            }else{
                GradientDrawable borderDrawable = new GradientDrawable();
                borderDrawable.setShape(GradientDrawable.RECTANGLE);
                borderDrawable.setStroke(5, Color.RED); // Larghezza e colore del bordo
                borderDrawable.setColor(Color.TRANSPARENT);
                img1.setBackground(borderDrawable);
            }
        }
    }

    public void playAudio(View view) {
        textToSpeechManager.speak(parola, 0.7f);
    }

    public void showMsg(View view) {
        Toast.makeText(CorrezioneCoppie.this, CorrezioneCoppie.this.getString(R.string.noCorrezione), Toast.LENGTH_SHORT).show();
    }

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