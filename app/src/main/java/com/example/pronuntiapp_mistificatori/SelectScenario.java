
package com.example.pronuntiapp_mistificatori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectScenario extends AppCompatActivity {

    private String codiceBimbo;
    private Button bottom;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_background, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_background1) {
            setBackground(R.drawable.background1);
            return true;
        } else if (id == R.id.action_background2) {
            setBackground(R.drawable.background2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackground(int resId) {

        ImageView backgroundImageView = findViewById(R.id.backgroundImageView);
        backgroundImageView.setImageResource(resId);
    }
}

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scenario);

        codiceBimbo = getIntent().getStringExtra("codice");
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        DatabaseReference databaseReference = database.getReference("logopedisti/ABC/Pazienti/" + codiceBimbo + "/scenario");
        ViewPager2 viewPager = findViewById(R.id.);

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
}
*/