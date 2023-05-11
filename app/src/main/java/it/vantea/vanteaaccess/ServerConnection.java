package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerConnection extends AppCompatActivity {

    private ProgressBar pb;
    private String qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);
        pb=findViewById(R.id.progress);
        qrId = getIntent().getStringExtra("code");
        try {
            auth();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    private void auth() throws JSONException {
        String url = "http://openam.docker.com:8080/openam/json/authenticate";


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
                HashMap<String, String> headers = new HashMap<String , String>();
                headers.put("X-OpenAM-Username","flavio");
                headers.put("X-OpenAM-Password","password");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }



    private void connection(String id) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://openam.docker.com:8080/openam/json/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;

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
        String url = "http://openam.docker.com:8080/openam/json/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;



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
            }
        });
    }
}