package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    LinearLayout kidsLayout;
    int cont=1;
    private HorizontalScrollView horizontalScrollView;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    boolean ctrl = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kidsLayout = findViewById(R.id.kidsList);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        // Verifica lo stato di accesso
        if (!isLoggedIn()) {
            // Se non è autenticato, avvia l'activity di login
            startLoginActivity();
            // Chiudi questa activity per evitare che l'utente torni indietro
            finish();
        }
        user = firebaseAuth.getCurrentUser();
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        progressBar.setVisibility(View.VISIBLE);
        showKids();

        //DRAWER
        drawerLayout = findViewById(R.id.drawer_layout);

        // Configura il toggle per gestire l'apertura e la chiusura del Navigation Drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Configura il listener per gestire gli item del Navigation Drawer
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Gestisci gli item del menu qui
            drawerLayout.closeDrawers();
            return true;
        });
        //END DRAWER
    }


    private boolean isLoggedIn() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null;
    }

    // Metodo per avviare l'activity di login
    private void startLoginActivity() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    //mostra finestra di dialogo per inserire codice figli
    public void showDialog(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_kidcode_layout);

        // Trova gli elementi all'interno del dialog
        final EditText editTextCode = dialog.findViewById(R.id.editTextCode);
        Button buttonSubmit = dialog.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(v -> {
            // Gestisci l'azione del button qui
            String codiceInserito = editTextCode.getText().toString();

            String percorsoGenitoreFigli = "genitori/" + user.getUid() + "/figli";

            databaseReference = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app").getReference(percorsoGenitoreFigli);

            // Controlla se il codice è già presente nel database
            databaseReference.orderByValue().equalTo(codiceInserito).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Il codice è già presente nel database, mostra un Toast
                        Toast.makeText(mainActivity.this, "Il codice è già presente nel database", Toast.LENGTH_SHORT).show();
                    } else {
                        // Genera un nuovo ID univoco per il figlio
                        String nuovoFiglioKey = databaseReference.push().getKey();

                        // Aggiungi il codiceInserito alla lista dei figli sotto il nuovo ID
                        assert nuovoFiglioKey != null;
                        databaseReference.child(nuovoFiglioKey).setValue(codiceInserito);
                        restartActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Gestisci eventuali errori nell'accesso al database
                    Toast.makeText(mainActivity.this, "Errore nell'accesso al database", Toast.LENGTH_SHORT).show();
                }
            });
            // Chiudi il dialog
            dialog.dismiss();
        });

        // Mostra il dialog
        dialog.show();
    }

    private void restartActivity() {
        recreate();
    }


    private void showKids(){
        String percorsoGenitoreFigli = "genitori/" + user.getUid() + "/figli";
        databaseReference = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app").getReference(percorsoGenitoreFigli);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> listaCodici = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        listaCodici.add(childSnapshot.getValue(String.class));
                    }

                    for (String codice : listaCodici) {
                        showImages(codice);
                        cont++;
                        //Toast.makeText(MainActivity.this, codice, Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gestisci eventuali errori nell'accesso al database
                Toast.makeText(mainActivity.this, "Errore nell'accesso al database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImages(String code){
        String percorso = "logopedisti/ABC/Pazienti/" + code;
        databaseReference = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app").getReference(percorso);

        // Aggiungi un listener per ottenere i dati dal database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Controllo se il percorso esiste nel database
                if (dataSnapshot.exists()) {
                    // Ottieni i valori Sesso e Nome
                    String sesso = dataSnapshot.child("Sesso").getValue(String.class);
                    String nome = dataSnapshot.child("Nome").getValue(String.class);

                    assert sesso != null;
                    createKid(nome, sesso, code);
                } else {
                    Toast.makeText(mainActivity.this, "codice non presente", Toast.LENGTH_SHORT).show();
                }
                showAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mainActivity.this, "Errore nel recupero dei dati: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createKid(String nome, String sesso, String code){
        LinearLayout newLinearLayout = new LinearLayout(this);
        newLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newLinearLayout.setOrientation(LinearLayout.VERTICAL);
        newLinearLayout.setGravity(Gravity.CENTER);

        // Crea un nuovo ImageView
        ImageView newImageView = new ImageView(this);
        newImageView.setLayoutParams(new LinearLayout.LayoutParams(
                300, // width
                300  // height
        ));

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL); // Imposta la forma come ovale (rotondo)
        int randomColor = Color.argb(255, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
        gradientDrawable.setColor(randomColor);
        newImageView.setBackground(gradientDrawable);

        //newImageView.setBackgroundResource(R.drawable.rounded); // Sostituisci con il tuo drawable
        if(sesso.equals("M"))
            newImageView.setImageResource(R.drawable.male); // Sostituisci con la tua immagine
        else
            newImageView.setImageResource(R.drawable.female);

        // Crea un nuovo TextView
        TextView newTextView = new TextView(this);
        newTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newTextView.setTextSize(20);
        newTextView.setText(nome);

        // Aggiungi ImageView e TextView al nuovo LinearLayout
        newLinearLayout.addView(newImageView);
        newLinearLayout.addView(newTextView);
        if(!ctrl){
            addMenuItemLogOut();
            ctrl=true;
        }
        addMenuItem(nome, code);

        newLinearLayout.setOnClickListener(v -> {

                Intent i = new Intent(this, MenuParent.class);
                i.putExtra("nome", nome );
                i.putExtra("code", code );
                startActivity(i);
              //aprire activity bambino mandando come parametro "code, nome"
        });

        // Aggiungi il nuovo LinearLayout a kidsLayout
        if(cont>1){
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(
                    50, // larghezza in dp convertita a pixel
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            kidsLayout.addView(space);
        }
        kidsLayout.addView(newLinearLayout);

    }

    private void addMenuItem(String title, String code) {
        int dynamicId = View.generateViewId();
        Menu menu = navigationView.getMenu();
        MenuItem newItem = menu.add(Menu.NONE, dynamicId, Menu.NONE, title);
        newItem.setCheckable(true);
        newItem.setIcon(R.drawable.kids_nav);
        newItem.setOnMenuItemClickListener(item -> {
            drawerLayout.closeDrawers();
            // Gestisci il clic sull'item
            Intent i = new Intent(this, MenuParent.class);
            i.putExtra("nome", title );
            i.putExtra("code", code );
            startActivity(i);
            return true;
        });
    }

    private void addMenuItemLogOut(){
        int dynamicId = View.generateViewId();
        Menu menu = navigationView.getMenu();
        MenuItem newItem = menu.add(Menu.NONE, dynamicId, Menu.NONE, getString(R.string.logout));
        newItem.setCheckable(true);
        newItem.setIcon(R.drawable.baseline_logout_24);
        newItem.setOnMenuItemClickListener(item -> {
            new AlertDialog.Builder(mainActivity.this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logoutConfirm))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        dialog.dismiss();

                        // Riavvia l'applicazione dopo il logout
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) {
                            // L'utente è stato disconnesso, riavvia l'app
                            recreate();
                        }
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });
    }

    private void showAnimation(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(horizontalScrollView, "scrollX", 0, 250).setDuration(1000);
        objectAnimator.start();
    }
}