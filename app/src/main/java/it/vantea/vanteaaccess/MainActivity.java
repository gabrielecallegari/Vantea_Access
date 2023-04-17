package it.vantea.vanteaaccess;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private CardView number1,number2,number3,number4,number5,number6,number7,number8,number9,number0, cancel, touchid;
    private TextView numberPassword, labelLog;
    private String password = "";
    private Button bottone;
    private boolean loggato = false;

    private String letto = "";

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        number0 = findViewById(R.id.number_0);
        number1 = findViewById(R.id.number_1);
        number2 = findViewById(R.id.number_2);
        number3 = findViewById(R.id.number_3);
        number4 = findViewById(R.id.number_4);
        number5 = findViewById(R.id.number_5);
        number6 = findViewById(R.id.number_6);
        number7 = findViewById(R.id.number_7);
        number8 = findViewById(R.id.number_8);
        number9 = findViewById(R.id.number_9);
        numberPassword = findViewById(R.id.numberPassword);
        bottone = findViewById(R.id.bottone);
        cancel = findViewById(R.id.cancel);
        labelLog = findViewById(R.id.labelStart);
        touchid = findViewById(R.id.touchid);
        touchid.setVisibility(View.GONE);

        String message = getIntent().getStringExtra("message");
        if(null != message) {
            Log.e("MESSAGGIO", message);
        }


        try{
            letto = leggi();
            if(letto.equals("")) loggato=false;
            else loggato = true;
        }catch(Exception e){}

        if(loggato == true && null == message){
            labelLog.setText("Bentornato/a, per favore immetti il codice oppure accedi tramite touch id");
            touchid.setVisibility(View.VISIBLE);
        }else{
            if(loggato == true){
                labelLog.setText(message);
                touchid.setVisibility(View.GONE);
                loggato = false;
            }
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Errore nell'autenticazione: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(MainActivity.this, Logged.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Vantea SMART")
                .setSubtitle("Log in con Impronta Digitale")
                .setNegativeButtonText("Cancella")
                .build();



        touchid.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });


        cancel.setOnClickListener(l -> {

            if(password.length() > 0 ){
                String[] splitted = password.split("");
                password = "";
                for (int i = 0; i < splitted.length-1; i++) {
                    password = password + splitted[i];
                }
                numberPassword.setText(password);
            }
        });

        bottone.setOnClickListener( l -> {
            if(password.length()==6){
                if(loggato == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(Html.fromHtml("<font color='black'>SET PASSWORD</font>"));
                    builder.setMessage("Confermi di voler usare la password " + password + " per accedere all'app? Sarà comunque possibile modificarla in seguito");
                    builder.setPositiveButton(Html.fromHtml("<font color='#039221'>CONFERMA</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            try {
                                scrivi(password);
                                Intent intent = new Intent(MainActivity.this, Logged.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.setNeutralButton(Html.fromHtml("<font color='red'>ANNULLA</font>"), null);
                    builder.create();
                    builder.show();
                }else{
                    if(letto.equals(password)){
                        Intent intent = new Intent(MainActivity.this, Logged.class);
                        startActivity(intent);
                        finish();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(Html.fromHtml("<font color='red'>ERRORE</font>"));
                        builder.setMessage("Password inserita non corretta");
                        builder.setPositiveButton(Html.fromHtml("<font color='black'>OK</font>"), null);
                        builder.create();
                        builder.show();

                        password = "";
                        numberPassword.setText(password);
                    }
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(Html.fromHtml("<font color='red'>ERRORE</font>"));
                builder.setMessage("Devi inserire 6 numeri !");
                builder.setPositiveButton(Html.fromHtml("<font color='#000000'>OK</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        number0.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "0";
            numberPassword.setText(password);
            Log.d("PASSWORD", password);
        });

        number1.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "1";
            numberPassword.setText(password);
        });

        number2.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "2";
            numberPassword.setText(password);
        });

        number3.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "3";
            numberPassword.setText(password);
        });

        number4.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "4";
            numberPassword.setText(password);
        });

        number5.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "5";
            numberPassword.setText(password);
        });

        number6.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "6";
            numberPassword.setText(password);
        });

        number7.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "7";
            numberPassword.setText(password);
        });

        number8.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "8";
            numberPassword.setText(password);
        });

        number9.setOnClickListener( l -> {
            if(password.length() < 6) password = password + "9";
            numberPassword.setText(password);
        });
    }

    private boolean scrivi(String text){
        File file = new File(getFilesDir(),"password.pwd");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(text.getBytes());
            stream.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
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
}