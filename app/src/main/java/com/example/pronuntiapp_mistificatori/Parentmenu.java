package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Parentmenu extends AppCompatActivity {
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parentmenu);

        String nome=getIntent().getStringExtra("nome");
        String code=getIntent().getStringExtra("code");
        TextView name=  (TextView)findViewById(R.id.kidname);
        name.setText(""+nome);
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