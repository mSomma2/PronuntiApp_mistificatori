package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Objects;

public class EsercizioRipetizione extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    FirebaseUser currentUser;
    private String parola, outputFile;
    private TextToSpeechManager textToSpeechManager;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private ImageButton recAudio, playAudio;
    boolean isRecording = false;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esercizio_ripetizione);

        String codice = getIntent().getStringExtra("codice");
        String esercizio = getIntent().getStringExtra("esercizio");

        Initialize(esercizio);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        textToSpeechManager = new TextToSpeechManager(this);
        outputFile = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/recording.3gp";

        // Inizializza il MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        seekBar = findViewById(R.id.seekBar);

    }

    private void Initialize(String es){
        String percorso = "logopedisti/ABC/esercizi/" + es;
        DatabaseReference myRef = database.getReference(percorso);

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

    public void checkResul(View view) {
    }

    public void reset(View view) {
        recreate();
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

    public void rec(View view) {
        if (!isRecording) {
            // Prepara il MediaRecorder e inizia la registrazione
            //new StartRecordingTask().doInBackground();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                recAudio.setImageResource(R.drawable.stop);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Smetti di registrare e rilascia le risorse
            mediaRecorder.stop();
            mediaRecorder.release();
            recAudio.setImageResource(R.drawable.microfone);
            recAudio.setVisibility(View.GONE);
            playAudio.setVisibility(View.VISIBLE);
            isRecording = false;
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputFile);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public void playAudio(View view) {
        textToSpeechManager.speak(parola, 1f);
        seekBar.setMax(textToSpeechManager.estimateSpeechDuration(parola, 1f));
        //updateSeekBar();
        textToSpeechManager.speak(parola, 1f);
    }
}