package com.example.pronuntiapp_mistificatori;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class OverlapPageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();

        if (position < -1) { // Fuori dalla schermata a sinistra
            page.setAlpha(0);
        } else if (position <= 0) { // In transizione dalla sinistra alla schermata attiva
            page.setAlpha(1 + position);
            page.setTranslationX(-position * pageWidth);
            page.setScaleX(1 + position);
            page.setScaleY(1 + position);
        } else if (position <= 1) { // In transizione dalla schermata attiva a destra
            page.setAlpha(1 - position);
            page.setTranslationX(-position * pageWidth);
            page.setScaleX(1 - position);
            page.setScaleY(1 - position);
        } else { // Fuori dalla schermata a destra
            page.setAlpha(0);
        }
    }
}
