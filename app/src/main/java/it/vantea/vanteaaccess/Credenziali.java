package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Credenziali extends AppCompatActivity {
    private Button registra;
    private EditText us, pwd;
    private String ip = "openam.docker.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credenziali);
        registra = findViewById(R.id.registraCredenziali);
        us = findViewById(R.id.usernameInputCredenziali);
        pwd = findViewById(R.id.passwordInputCredenziali);

        registra.setOnClickListener( l -> {
            String username = us.getText().toString();
            String password = pwd.getText().toString();
            String url = "http://"+ip+":8080/openam/json/authenticate";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("CREDENZIALI", "Loggato");
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(leggi());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("LETTO", obj.toString() );
                    try {
                        obj.put("username",username);
                        obj.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    scrivi(obj.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(Credenziali.this);
                    builder.setTitle(Html.fromHtml("<font color='#005c00'>Operazione avvenuta con successo</font>"));
                    builder.setMessage("Le credenziali sono state salvate sul dispositivo");
                    builder.setPositiveButton(Html.fromHtml("<font color='#000000'>OK</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            Intent intent = new Intent(Credenziali.this, Logged.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Credenziali.this);
                    builder.setTitle(Html.fromHtml("<font color='red'>ERRORE</font>"));
                    builder.setMessage("Le credenziali inserite non sono corrette!");
                    builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Riprova</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {}
                    });
                    builder.create();
                    builder.show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String , String>();
                    headers.put("X-OpenAM-Username",username);
                    headers.put("X-OpenAM-Password",password);
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(jsonObjectRequest);
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