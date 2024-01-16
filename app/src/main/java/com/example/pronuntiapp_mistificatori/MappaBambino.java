package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MappaBambino extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private DatabaseReference databaseReference;
    private Map<Button, Drawable> originalButtonDrawables = new HashMap<>();
    private String codiceBimbo, gameSelect;
    private LinearLayout mappa;
    private TextView coinTextView;
    int cont=1, numButton = 0;
    private ScrollView scrollView;
    private ImageView personaggio;
    private float initialX, initialY;
    private Intent i = null;
    private Integer es, coin, punteggio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa_bambino);

        codiceBimbo = getIntent().getStringExtra("codice");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        RelativeLayout sfondo = findViewById(R.id.sfondo);

        DatabaseReference dbref = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String scenario = snapshot.child("scenario").getValue(String.class);
                assert scenario != null;
                if(scenario.equals("A")){
                    sfondo.setBackground(ContextCompat.getDrawable(MappaBambino.this, R.drawable.sfondo_a));
                }else if(scenario.equals("B")){
                    sfondo.setBackground(ContextCompat.getDrawable(MappaBambino.this, R.drawable.sfondo_b));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        coinTextView = findViewById(R.id.coin);
        mappa = findViewById(R.id.map);
        scrollView = findViewById(R.id.scroll);
        databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/Esercizi/");

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(currentDate);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    Boolean esito = dateSnapshot.child("esito").getValue(Boolean.class);
                    numButton++;
                    assert date != null;
                    Log.d("MIMMA", date);
                    Button button = new Button(MappaBambino.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            400, 400
                    );

                    if (cont % 2 != 0) {
                        layoutParams.setMargins(150, -80, 0, 0);
                    } else {
                        // Se il bottone è dispari, posizionalo nella riga successiva
                        layoutParams.setMargins(600, -200, 0, 0);
                    }

                    if(cont == 1) layoutParams.setMargins(150, 0, 0, 0);

                    //layoutParams.setMargins(random.nextInt(300), -20, random.nextInt(300), 0);
                    button.setLayoutParams(layoutParams);

                    if(esito == null){
                        button.setBackgroundResource(R.drawable.es_passaggio);
                    }else if(esito){
                        button.setBackgroundResource(R.drawable.es_corretto);
                    }else{
                        button.setBackgroundResource(R.drawable.es_errato);
                    }

                    button.setTextColor(ContextCompat.getColor(MappaBambino.this, R.color.white));
                    button.setTextSize(17);
                    button.setTypeface(null, Typeface.BOLD);
                    button.setText(date.substring(0, 5));

                    mappa.addView(button);

                    if(date.equals(formattedDate)){
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

                        for (int i = 0; i < mappa.getChildCount(); i++) {
                            View child = mappa.getChildAt(i);
                            if (child instanceof Button) {
                                int[] buttonLocation = new int[2];
                                child.getLocationOnScreen(buttonLocation);

                                int buttonLeft = buttonLocation[0];
                                int buttonTop = buttonLocation[1];

                                RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) personaggio.getLayoutParams();
                                imageParams.leftMargin = buttonLeft + 150;  // Imposta il margine sinistro
                                imageParams.topMargin = buttonTop;    // Imposta il margine superiore
                                personaggio.setLayoutParams(imageParams);
                            }
                        }
                    }
                    cont++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gestisci eventuali errori di lettura dal database
                Log.e("TAGGA", "Errore durante la lettura dei dati dal database", databaseError.toException());
            }
        });

        personaggio = findViewById(R.id.personaggio);

        DatabaseReference dbRef = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coin = snapshot.child("coin").getValue(Integer.class);
                coinTextView.setText(String.valueOf(coin));
                String pers = snapshot.child("personaggi/selezionato").getValue(String.class);
                punteggio = snapshot.child("punteggio").getValue(Integer.class);
                getCharacter getCharacter = new getCharacter();
                assert pers != null;
                personaggio.setImageResource(getCharacter.get(pers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        personaggio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getRawX();
                        initialY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - initialX;
                        float deltaY = event.getRawY() - initialY;
                        initialX = event.getRawX();
                        initialY = event.getRawY();
                        adjustScrollView(deltaX, deltaY);
                        checkButtonColor();
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });

    }

    private void adjustScrollView(float deltaX, float deltaY) {
        scrollView.scrollBy((int) deltaX, (int) deltaY * (numButton/5));
        personaggio.animate().translationXBy(deltaX).translationYBy(deltaY).setDuration(0).start();
    }

    private void checkButtonColor() {
        int[] imgLocation = new int[2];
        personaggio.getLocationOnScreen(imgLocation);

        for (int i = 0; i < mappa.getChildCount(); i++) {
            View child = mappa.getChildAt(i);
            if (child instanceof Button) {
                int[] buttonLocation = new int[2];
                child.getLocationOnScreen(buttonLocation);

                Button currentButton = (Button) child;
                Drawable currentDrawable = currentButton.getBackground();

                if (!originalButtonDrawables.containsKey(currentButton)) {
                    // Salva lo stato originale del pulsante se non è già stato salvato
                    originalButtonDrawables.put(currentButton, currentDrawable);
                }

                if (isViewOverlapping(personaggio, child)) {
                    // L'ImageView sovrappone il Button, cambia il background
                    currentButton.setBackgroundResource(R.drawable.es_futuro);
                    gameSelect = currentButton.getText().toString();
                } else {
                    // L'ImageView non sovrappone più il Button, ripristina il background originale
                    currentButton.setBackground(originalButtonDrawables.get(currentButton));
                }
            }
        }
    }


    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int firstViewLeft = firstPosition[0];
        int firstViewRight = firstPosition[0] + firstView.getWidth();
        int firstViewTop = firstPosition[1];
        int firstViewBottom = firstPosition[1] + firstView.getHeight();

        int secondViewLeft = secondPosition[0];
        int secondViewRight = secondPosition[0] + secondView.getWidth();
        int secondViewTop = secondPosition[1];
        int secondViewBottom = secondPosition[1] + secondView.getHeight();

        return firstViewLeft < secondViewRight &&
                firstViewRight > secondViewLeft &&
                firstViewTop < secondViewBottom &&
                firstViewBottom > secondViewTop;
    }

    public void playGame(View view) {
        databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/Esercizi/" + gameSelect + "-2024");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("es_assegnato")) {
                    es = dataSnapshot.child("es_assegnato").getValue(Integer.class);
                    if (es != null) {
                        databaseReference.removeEventListener(this);
                        DatabaseReference dbref = database.getReference("logopedisti/ABC/esercizi/" + es);
                        dbref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                if(snap.hasChild("tipologia")){
                                    Integer tipo = snap.child("tipologia").getValue(Integer.class);
                                    if(tipo != null){
                                        switch (tipo){
                                            case 1:
                                                i = new Intent(MappaBambino.this, EsercizioDenominazione.class);
                                                break;
                                            case 2:
                                                i = new Intent(MappaBambino.this, EsercizioRipetizione.class);
                                                break;
                                            case 3:
                                                i = new Intent(MappaBambino.this, EsercizioCoppie.class);
                                                break;
                                        }
                                        if(i != null){
                                            i.putExtra("esercizio", es.toString());
                                            i.putExtra("codice", codiceBimbo);
                                            i.putExtra("data", gameSelect + "-2024");
                                            i.putExtra("coin", coin);
                                            i.putExtra("punteggio", punteggio);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                }else{
                                    Toast.makeText(MappaBambino.this, "mimma", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Log.e("TAGGA", "Il valore 'es_assegnato' è nullo per la data ");
                    }
                } else {
                    Log.e("MappaBambino", "Il nodo 'es_assegnato' non esiste per la data " + dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAGGA", "Errore durante la lettura dei dati dal database", error.toException());
            }
        });

    }

    public void character(View view) {
        Intent i = new Intent(MappaBambino.this, SelectCharacter.class);
        i.putExtra("codice", codiceBimbo);
        startActivity(i);
    }

    public void rank(View view) {
        Intent i = new Intent(MappaBambino.this, SelectScenario.class);
        i.putExtra("codice", codiceBimbo);
        startActivity(i);
    }
}