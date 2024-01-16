package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class rank extends AppCompatActivity {

    private String codiceBimbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        codiceBimbo = getIntent().getStringExtra("codice");
    }
}