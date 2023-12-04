package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private float dX, dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView img;
        img = findViewById(R.id.imageView);

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Salva le coordinate iniziali del tocco
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        dX=0;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // Aggiorna la posizione dell'ImageView durante lo spostamento
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

}