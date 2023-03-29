package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;


public class Logged extends AppCompatActivity {

    private CardView carta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        carta = findViewById(R.id.card);

        if (ContextCompat.checkSelfPermission(Logged.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }

        carta.setOnClickListener( l -> {
            Intent intent = new Intent(Logged.this, Scanner.class);
            startActivity(intent);
        });
    }
}