package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;

public class Logged extends AppCompatActivity {

    private CardView carta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        carta = findViewById(R.id.card);

        carta.setOnClickListener( l -> {

        });
    }
}