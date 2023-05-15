package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Logged extends AppCompatActivity {


    private CardView  qr, credenziali;
    private TextView testo;
    private ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        qr = findViewById(R.id.card);
        testo = findViewById(R.id.testolog);
        credenziali = findViewById(R.id.credenziali);
        settings = findViewById(R.id.settings);


        JSONObject letto = null;

        try {
            letto = new JSONObject(leggi());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.e("JSON", letto.getString("username"));
            setVisibilityAll(true);
        } catch (JSONException e) {
            setVisibilityAll(false);
            Log.e("ECCEZIONE JSON", "onCreate: ");
        }

        if (ContextCompat.checkSelfPermission(Logged.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }

        credenziali.setOnClickListener(l -> {
            Intent intent = new Intent(Logged.this, Credenziali.class);
            startActivity(intent);
            finish();
        });

        qr.setOnClickListener( l -> {
            //Fase definitiva usare questo
            Intent intent = new Intent(Logged.this, Scanner.class);
            startActivity(intent);
        });

        settings.setOnClickListener( l -> {
            Intent intent = new Intent(Logged.this, Settings.class);
            startActivity(intent);
        });

    }

    private String leggi(){
        File file = new File(getFilesDir(), "password.pwd");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        ;
        return text.toString();
    }

    private void setVisibilityAll(boolean vis){
            qr.setVisibility(vis == false ? View.GONE : View.VISIBLE);
            testo.setVisibility(vis == false ? View.GONE : View.VISIBLE);
            credenziali.setVisibility(vis == false ? View.VISIBLE : View.GONE);
    }
}