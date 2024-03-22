package com.example.pronuntiapp_mistificatori;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class appointment extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<appointment> appointmentList;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        recyclerView.setAdapter(appointmentAdapter);

        // Retrieve data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    appointment appointment = snapshot.getValue(com.example.pronuntiapp_mistificatori.appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error getting data from Firebase", databaseError.toException());
            }
        });
    }
}

public class Appointment {
    private String date;

    public Appointment() {
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    }

    public Appointment(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
