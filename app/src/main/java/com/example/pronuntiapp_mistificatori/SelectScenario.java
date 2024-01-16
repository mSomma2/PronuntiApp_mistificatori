package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SelectScenario extends AppCompatActivity {

    private String codiceBimbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scenario);

        codiceBimbo = getIntent().getStringExtra("codice");
    }
}