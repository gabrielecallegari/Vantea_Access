package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class Code extends AppCompatActivity {
    private Button accedi;
    private EditText codice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        accedi = findViewById(R.id.codiceAccedi);
        codice = findViewById(R.id.codiceImmesso);

        accedi.setOnClickListener(l -> {
            Intent intent = new Intent(Code.this, ServerConnection.class);
            intent.putExtra("code",codice.getText().toString());
            startActivity(intent);
            finish();
        });
    }
}