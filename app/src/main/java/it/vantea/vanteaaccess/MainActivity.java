package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private CardView qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qr=findViewById(R.id.card);

        qr.setOnClickListener(l -> {
            Log.d("TAG", "Clicked");
        });
    }
}