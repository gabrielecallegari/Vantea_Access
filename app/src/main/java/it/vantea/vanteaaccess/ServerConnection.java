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
import java.util.HashMap;
import java.util.Map;

public class ServerConnection extends AppCompatActivity {

    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);
        pb=findViewById(R.id.progress);
        String id = getIntent().getStringExtra("code");
         // url http://mioproxy.com:9080/openam/json/realm=google-totp/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId=

        try {
            auth();
            //connection(id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }





    }

    private void auth() throws JSONException {
        String url = "http://mioproxy.com:9080/openam/json/authenticate";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RISPOSTA", "onResponse: "+response.toString());
                String risposta [] = response.toString().split(",");
                String t[] = risposta[0].split(":");
                String token = t[1];
                token = token.replace("\"","");
                Log.e("TOKEN", token);
                try {
                    connection(token);
                } catch (JSONException e) {
                    alertErrore();
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
                headers.put("X-OpenAM-Username","amadmin");
                headers.put("X-OpenAM-Password","password");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void connection(String id) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://mioproxy.com:9080/openam/json/qr/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("Title", "Android Volley Demo");
        jsonBody.put("Author", "BNK");
        final String requestBody = jsonBody.toString();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RISPOSTA 2", "onResponse: "+response.toString());
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