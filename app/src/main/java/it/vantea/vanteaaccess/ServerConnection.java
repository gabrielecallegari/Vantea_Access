package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerConnection extends AppCompatActivity {

    private ProgressBar pb;
    private String qrId;
    private ImageView img;
    private Button btn;
    private TextView txt;
    private String ip = "openam.docker.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);
        pb=findViewById(R.id.progress);
        txt = findViewById(R.id.messaggioAvvenuto);
        btn = findViewById(R.id.backhome);
        img = findViewById(R.id.checkImage);

        img.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        txt.setVisibility(View.GONE);


        qrId = getIntent().getStringExtra("code");
        try {
            auth();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        btn.setOnClickListener( l -> {
            Intent intent = new Intent(ServerConnection.this, Logged.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }


    private void auth() throws JSONException {
        String url = "http://"+ip+":8080/openam/json/authenticate";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RISPOSTA", "onResponse: "+response.toString());
                String split [] = response.toString().split(",");
                String split2 [] = split[0].split(":");
                String token = split2[1];
                token = token.substring(1, token.length()-1);
                Log.e("TOKEN", "onResponse: "+token);
                try {
                    connection(token);
                } catch (JSONException e) {
                    Log.e("ERRORE", "Connessione seconda parte");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertErrore();
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
                try {
                    headers.put("X-OpenAM-Username",obj.get("username").toString());
                    headers.put("X-OpenAM-Password",obj.get("password").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }



    private void connection(String id) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://"+ip+":8080/openam/json/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RISPOSTA 2", "onResponse: "+response.toString());
                try {
                    finalSend(id, response.toString());
                } catch (JSONException e) {
                    Log.e("ERRORE", "Errore terzo invio");
                }
                stopProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertErrore();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String , String>();
                headers.put("Content-Type","application/json;charset=UTF-8");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    private void finalSend(String id, String body) throws JSONException {
        String url = "http://"+ip+":8080/openam/json/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RISPOSTA", "onResponse: "+response.toString());
                stopProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertErrore();
                Log.e("ERRORE NELLA CONNESSIONE", "onErrorResponse: "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String , String>();
                headers.put("Content-Type","application/json;charset=UTF-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                //Elaborazione Body (aggiunta codice del QR)
                String myBody = body.substring(0, body.length()-1-5);
                myBody = myBody + ""+qrId+"\"}]}]}";
                Log.e("BODY", "Body Mandato: "+myBody);
                try {
                    return myBody.getBytes(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", "utf-8");
                    Log.e("ECCEZIONE GET BODY", "Errore" );
                    return null;
                }
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void alertErrore(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='red'>ERRORE</font>"));
        builder.setMessage("Si è verificato un errore nella connessione al server");
        builder.setPositiveButton(Html.fromHtml("<font color='black'>TORNA ALLA HOME</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(ServerConnection.this, Logged.class);
                startActivity(intent);
                finish();
            }
        });;
        builder.create();
        builder.show();
    }

    private void stopProgressBar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
                txt.setVisibility(View.VISIBLE);
            }
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
}