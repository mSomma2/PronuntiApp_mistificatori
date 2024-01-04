package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EsercizioCoppie extends AppCompatActivity {

    private ImageView img1, img2;
    private String parola;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    FirebaseUser currentUser;
    private ImageButton conf;
    private DatabaseReference databaseReference;
    Integer risposta, select;
    private TextToSpeechManager textToSpeechManager;
    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esercizio_coppie);

        String codice = getIntent().getStringExtra("codice");
        String esercizio = getIntent().getStringExtra("esercizio");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);

        conf = findViewById(R.id.confirm);
        conf.setEnabled(false);

        Initialize(esercizio);
        databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codice + "/Esercizi/07-12-2023");
        textToSpeechManager = new TextToSpeechManager(this);
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
                    Glide.with(EsercizioCoppie.this)
                            .load(url1)
                            .into(img1);
                    Glide.with(EsercizioCoppie.this)
                            .load(url2)
                            .into(img2);
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

    public void setImg1(View view) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setStroke(5, Color.GREEN); // Larghezza e colore del bordo
        borderDrawable.setColor(Color.TRANSPARENT);
        img1.setBackground(borderDrawable);
        GradientDrawable noBorderDrawable = new GradientDrawable();
        noBorderDrawable.setShape(GradientDrawable.RECTANGLE);
        noBorderDrawable.setStroke(0, Color.TRANSPARENT);
        img2.setBackground(noBorderDrawable);
        conf.setEnabled(true);
        select = 1;
    }

    public void setImg2(View view) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setStroke(5, Color.GREEN); // Larghezza e colore del bordo
        borderDrawable.setColor(Color.TRANSPARENT);
        img2.setBackground(borderDrawable);
        GradientDrawable noBorderDrawable = new GradientDrawable();
        noBorderDrawable.setShape(GradientDrawable.RECTANGLE);
        noBorderDrawable.setStroke(0, Color.TRANSPARENT);
        img1.setBackground(noBorderDrawable);
        conf.setEnabled(true);
        select = 2;
    }

    public void reset(View view) {
        recreate();
    }

    public void checkResul(View view) {
        if(Objects.equals(risposta, select)){
            mediaPlayer = MediaPlayer.create(EsercizioCoppie.this, R.raw.correct);
            mediaPlayer.start();
            showAnswer(R.layout.dialog_correct);
            databaseReference.child("esito").setValue(true);
        }else{
            mediaPlayer = MediaPlayer.create(EsercizioCoppie.this, R.raw.wrong);
            mediaPlayer.start();
            showAnswer(R.layout.dialog_wrong);
            databaseReference.child("esito").setValue(false);
        }
    }

    private void showAnswer(int layout) {
        Handler handler = new Handler();
        // Chiudi l'activity
        handler.postDelayed(this::finish, 4300);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(false); // Impedisci la chiusura del Dialog cliccando fuori da esso
        dialog.show();
    }

    public void playAudio(View view) {
        textToSpeechManager.speak(parola, 0.7f);
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