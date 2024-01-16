package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCharacter extends AppCompatActivity {

    private Button bottomButton;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private String codiceBimbo, scenario, select;
    private Integer coin;
    private int scelta = 0, character;
    private TextView coinTW;
    private ArrayList<Integer> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_character);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        codiceBimbo = getIntent().getStringExtra("codice");

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        coinTW = findViewById(R.id.coin);

        DatabaseReference databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo);
        List<Integer> imageList = new ArrayList<>();
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageList);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scenario = snapshot.child("scenario").getValue(String.class);
                coin = snapshot.child("coin").getValue(Integer.class);
                select = snapshot.child("personaggi/selezionato").getValue(String.class);
                coinTW.setText(String.valueOf(coin));
                switch (Objects.requireNonNull(scenario)){
                    case "A":
                        imageList.add(R.drawable.personaggio_a);
                        imageList.add(R.drawable.personaggio_b);
                        imageList.add(R.drawable.personaggio_c);
                        imageList.add(R.drawable.personaggio_d);
                        imageList.add(R.drawable.personaggio_e);
                        break;
                    case "B":
                        imageList.add(R.drawable.personaggio_cool);
                        imageList.add(R.drawable.personaggio_love);
                        imageList.add(R.drawable.personaggio_sciolto);
                        imageList.add(R.drawable.personaggio_star);
                        imageList.add(R.drawable.personaggio_wow);
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dbRef = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/personaggi/possesso");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String a = dateSnapshot.getValue(String.class);
                    int b = estraiNumeroDaStringa(a);
                    //Toast.makeText(SelectCharacter.this, String.valueOf(b), Toast.LENGTH_SHORT).show();
                    array.add(b-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(new OverlapPageTransformer());

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {}).attach();

        //viewPager.setPageTransformer(new ZoomOutPageTransformer());

        bottomButton = findViewById(R.id.bottomButton);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // La posizione della pagina è cambiata, aggiorna il pulsante
                updateBottomButton(position);
            }
        });

        btnLeft.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
            }
        });

        btnRight.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            int itemCount = Objects.requireNonNull(viewPager.getAdapter()).getItemCount();
            if (currentItem < itemCount - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });
    }

    private void updateBottomButton(int position) {
        String buttonText;
        if(position == (estraiNumeroDaStringa(select) - 1 )){
            buttonText = getString(R.string.selected);
            bottomButton.setBackgroundColor(ContextCompat.getColor(SelectCharacter.this, R.color.orange));
            scelta = 0;
        }else if(array.contains(position)){
            buttonText = getString(R.string.select);
            bottomButton.setBackgroundColor(ContextCompat.getColor(SelectCharacter.this, R.color.green_light));
            character = position + 1;
            scelta = 1;
        }else{
            buttonText = "20 C";
            if(coin >= 20){
                character = position + 1;
                bottomButton.setBackgroundColor(ContextCompat.getColor(SelectCharacter.this, R.color.orange));
                scelta = 2;
            }else {
                bottomButton.setBackgroundColor(ContextCompat.getColor(SelectCharacter.this, R.color.red));
                scelta = 3;
            }
        }
        bottomButton.setText(buttonText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Aggiungi qui il codice da eseguire quando viene premuto il pulsante indietro
            finish(); // Chiudi l'attività
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int estraiNumeroDaStringa(String input) {
        // Definisci il pattern per cercare una sequenza di cifre
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        // Se trova una sequenza di cifre, la converte in un intero
        if (matcher.find()) {
            String numeroStringa = matcher.group();
            return Integer.parseInt(numeroStringa);
        } else {
            // Se non trova nessuna cifra, puoi gestire il caso di default o lanciare un'eccezione
            throw new IllegalArgumentException("La stringa non contiene alcun numero.");
        }
    }

    public void btnChar(View view) {
        switch (scelta){
            case 1:
                DatabaseReference ref = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/personaggi/selezionato");
                String val = scenario + String.valueOf(character);
                ref.setValue(val);
                bottomButton.setText(getString(R.string.selected));
                bottomButton.setBackgroundColor(ContextCompat.getColor(SelectCharacter.this, R.color.orange));
                break;
            case 2:
                DatabaseReference dbRef = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/personaggi/possesso");
                String nuovoIndice = String.valueOf(System.currentTimeMillis());
                String val2 = scenario + String.valueOf(character);
                DatabaseReference ref2 = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/personaggi/selezionato");
                ref2.setValue(val2);
                dbRef.child(nuovoIndice).setValue(val2);
                bottomButton.setText(getString(R.string.selected));
                bottomButton.setBackgroundColor(ContextCompat.getColor(SelectCharacter.this, R.color.orange));
                break;
            case 3:
                Toast.makeText(SelectCharacter.this, getString(R.string.poor), Toast.LENGTH_SHORT).show();
                break;
        }

        // Chiude l'activity
        if(scelta == 2 || scelta == 1)
            new Handler().postDelayed(this::finish, 1000);
    }

}