package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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


public class EsercizioDenominazione extends AppCompatActivity{

    private ImageView imageView;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    FirebaseUser currentUser;
    private String parola;
    private TextToSpeechManager textToSpeechManager;
    private CardView cardView;
    int count = 0;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;
    private ImageButton recAudio, playAudio;
    boolean isRecording = false;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esercizio_denominazione);

        String codice = getIntent().getStringExtra("codice");
        String esercizio = getIntent().getStringExtra("esercizio");

        imageView = findViewById(R.id.image);
        cardView = findViewById(R.id.sfonoHelp);
        recAudio = findViewById(R.id.mic);
        playAudio = findViewById(R.id.playAudio);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        textToSpeechManager = new TextToSpeechManager(this);

        Initialize(esercizio);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        // Imposta il percorso del file di output
        outputFile = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/recording.3gp";

        // Inizializza il MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codice + "/Esercizi/07-12-2023");
    }

    void Initialize(String es){
        String percorso = "logopedisti/ABC/esercizi/" + es;
        DatabaseReference myRef = database.getReference(percorso);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String url = dataSnapshot.child("immagine").getValue(String.class);
                    parola = dataSnapshot.child("parola").getValue(String.class);
                    Glide.with(EsercizioDenominazione.this)
                            .load(url)
                            .into(imageView);
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

    public void play(View view) {
        if(count<3){
            float speed;
            if(count == 0) speed = 0.9f;
            else if(count == 1) speed = 0.8f;
            else speed = 0.6f;
            textToSpeechManager.speak(parola, speed);
            count++;
        }
        if(count == 1) cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        else if(count == 2) cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red));
        else if(count == 3) cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.grey));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(EsercizioDenominazione.this, "chiedi aiuto al tuo genitore", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void reset(View view) {
        recreate();
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
                    mediaPlayer = MediaPlayer.create(EsercizioDenominazione.this, R.raw.correct);
                    mediaPlayer.start();
                    showAnswer(R.layout.dialog_correct);
                    databaseReference.child("esito").setValue(true);
                    provaCarica();

                }else{
                    mediaPlayer = MediaPlayer.create(EsercizioDenominazione.this, R.raw.wrong);
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

            // Implementa gli altri metodi dell'interfaccia RecognitionListener...
        });
        speechRecognizer.startListening(intent);

//code for playing the audio file which you wish to give as an input
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(outputFile); // here file is the location of the audio file you wish to use an input
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
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

}

