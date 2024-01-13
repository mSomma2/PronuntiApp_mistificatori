package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class EsercizioRipetizione extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    FirebaseUser currentUser;
    private String parola, outputFile, codice;
    private TextToSpeechManager textToSpeechManager;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private ImageButton recAudio, playAudio;
    boolean isRecording = false;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esercizio_ripetizione);

        codice = getIntent().getStringExtra("codice");
        String esercizio = getIntent().getStringExtra("esercizio");
        String data = getIntent().getStringExtra("data");

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

        recAudio = findViewById(R.id.mic);
        playAudio = findViewById(R.id.playAudio);
        databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codice + "/Esercizi/" + data);
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
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

// Inizia l'ascolto
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                showAnswer(R.layout.dialog_wrong);
                databaseReference.child("esito").setValue(false);
                provaCarica();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                assert matches != null;
                if(matches.contains(parola)){
                    mediaPlayer = MediaPlayer.create(EsercizioRipetizione.this, R.raw.correct);
                    mediaPlayer.start();
                    showAnswer(R.layout.dialog_correct);
                    databaseReference.child("esito").setValue(true);
                    provaCarica();

                }else{
                    mediaPlayer = MediaPlayer.create(EsercizioRipetizione.this, R.raw.wrong);
                    mediaPlayer.start();
                    showAnswer(R.layout.dialog_wrong);
                    databaseReference.child("esito").setValue(false);
                    provaCarica();
                }
                // 'matches' contiene il testo riconosciuto

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        speechRecognizer.startListening(intent);

        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(outputFile);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAnswer(int layout) {
        Intent i = new Intent(EsercizioRipetizione.this, MappaBambino.class);
        i.putExtra("codice", codice);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(i);
            finish();
        }, 4300);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(false); // Impedisci la chiusura del Dialog cliccando fuori da esso
        dialog.show();
    }

    private void provaCarica(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String nomeFileAudio = UUID.randomUUID().toString();

        StorageReference audioRef = storageRef.child("audio/" + nomeFileAudio + ".3gp");
        Uri audioUri = Uri.fromFile(new File(outputFile));
        UploadTask uploadTask = audioRef.putFile(audioUri);

        uploadTask.addOnProgressListener(taskSnapshot -> {
        }).addOnSuccessListener(taskSnapshot -> audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String audioUrl = uri.toString();
            databaseReference.child("audio").setValue(audioUrl);
            Log.d("URL", audioUrl);
        })).addOnFailureListener(exception -> {
        });
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
    }
}