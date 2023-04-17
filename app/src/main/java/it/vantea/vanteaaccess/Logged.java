package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;


public class Logged extends AppCompatActivity {

    private CardView qr;
    private CardView nuovaPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        qr = findViewById(R.id.card);
        nuovaPassword = findViewById(R.id.modificapassword);

        if (ContextCompat.checkSelfPermission(Logged.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }

        qr.setOnClickListener( l -> {
            //Fase definitiva usare questo
            //Intent intent = new Intent(Logged.this, Scanner.class);

            //In testing usare questo
            Intent intent = new Intent(Logged.this, ServerConnection.class);
            intent.putExtra("code","");
            startActivity(intent);
        });

        nuovaPassword.setOnClickListener( l -> {
            Intent intent = new Intent(Logged.this, MainActivity.class);
            intent.putExtra("message","Registra la nuova password");
            startActivity(intent);
        });
    }
}