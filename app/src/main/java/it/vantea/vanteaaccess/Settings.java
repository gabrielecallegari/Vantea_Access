package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class Settings extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button modifica;
    private Button annulla;
    private int mod = 0;
    private String ip = "openam.docker.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        username = findViewById(R.id.usernameModifica);
        password = findViewById(R.id.passwordModifica);
        modifica = findViewById(R.id.modificaDati);
        annulla = findViewById(R.id.annulla);
        annulla.setVisibility(View.GONE);

        JSONObject obj = null;

        try {
            obj = new JSONObject(leggi());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String usr = null;
        String pwd = null;
        try {
            usr = obj.getString("username");
            pwd = obj.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        username.setText(usr);
        password.setText(pwd);
        username.setEnabled(false);
        password.setEnabled(false);

        annulla.setOnClickListener( l -> {
            mod = 0;
            username.setEnabled(false);
            password.setEnabled(false);
            modifica.setText("Modifica i dati");
            modifica.setBackgroundColor(Color.BLACK);
            annulla.setVisibility(View.GONE);
        });

        modifica.setOnClickListener( l -> {
            mod ++ ;
            modifica.setText("Salva i dati");
            modifica.setBackgroundColor(Color.RED);
            annulla.setVisibility(View.VISIBLE);
            username.setEnabled(true);
            password.setEnabled(true);

            if(mod > 1){
                try {
                    auth();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void auth() throws JSONException {
        String url = "http://"+ip+":8080/openam/json/authenticate";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject obj = null;
                JSONObject save = null;
                try {
                    obj = new JSONObject(leggi());
                    save = new JSONObject();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    save.put("passwordAccesso", obj.get("passwordAccesso").toString());
                    save.put("username", username.getText().toString());
                    save.put("password", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                scrivi(save.toString());
                alertErrore("Le nuove credenziali sono state salvate con successo", "Ok","Successo","#005c00");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                username.setInputType(InputType.TYPE_NULL);
                alertErrore("Le credenziali non sono corrette!","Riprova", "ERRORE", "red");
                username.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                Log.e("ERRORE NELLA CONNESSIONE", "onErrorResponse: "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(leggi());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HashMap<String, String> headers = new HashMap<String , String>();
                headers.put("X-OpenAM-Username",username.getText().toString());
                headers.put("X-OpenAM-Password",password.getText().toString());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void alertErrore(String msg, String ok, String title, String color){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='"+color+"'>"+title+"</font>"));
        builder.setMessage(msg);
        builder.setPositiveButton(Html.fromHtml("<font color='black'>"+ok+"</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });;
        builder.create();
        builder.show();
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