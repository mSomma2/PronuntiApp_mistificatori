package com.example.pronuntiapp_mistificatori;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Parentmenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parentmenu);
        TextView name, code;
        String nome = getIntent().getStringExtra("nome");
        String codice = getIntent().getStringExtra("code");

        name = findViewById(R.id.kidName);
        code = findViewById(R.id.kidCode);
        name.setText(nome);
        code.setText(codice);

        //bottone Inizia Gioco
        Button iniziaGiocoButton = findViewById(R.id.iniziaGiocoButton);
        iniziaGiocoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Parentmenu.this, bambino_activity.class);
                startActivity(intent);
            }
        });

    }

    public void background(View view) {

    }

    public void start(View view) {
    }

    public void therapy(View view) {
    }

    public void correction(View view) {
    }
}