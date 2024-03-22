package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import com.google.firebase.database.ValueEventListener;

public class rank extends AppCompatActivity {

    private String codiceBimbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        codiceBimbo = getIntent().getStringExtra("codice");
    }
    public class LeaderboardActivity extends AppCompatActivity {
        private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
        private ListView listView;
        private ArrayList<String> leaderboardList;
        private ArrayAdapter<String> adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_rank);

            listView = findViewById(R.id.listView);
            leaderboardList = new ArrayList<>();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leaderboardList);
            listView.setAdapter(adapter);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("leaderboard");

            databaseRef.orderByChild("score").limitToLast(10).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    leaderboardList.clear(); // Pulisce la lista per evitare duplicati

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String username = snapshot.child("username").getValue(String.class);
                        Integer score = snapshot.child("score").getValue(Integer.class); // Utilizziamo Integer invece di int

                        if (username != null && score != null) {
                            String entry = username + " - Punteggio: " + score;
                            leaderboardList.add(entry);
                        }
                    }

                    adapter.notifyDataSetChanged(); // Aggiorna la ListView con i nuovi dati
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Gestisci eventuali errori di lettura dal database
                }
            });
        }
    }
    public class Bimbo {
        private String username;
        private int score;

        // Costruttore, getter e setter
        public Bimbo() {

        }

        public Bimbo(String username, int score) {
            this.username = username;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

}