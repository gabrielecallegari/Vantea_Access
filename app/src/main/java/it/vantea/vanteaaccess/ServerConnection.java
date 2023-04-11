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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ServerConnection extends AppCompatActivity {

    private ProgressBar pb;

    private boolean errorAlert = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);
        pb=findViewById(R.id.progress);
        String id = getIntent().getStringExtra("code");
         // url http://mioproxy.com:9080/openam/json/realm=google-totp/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId=

        try {
            connection(id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.e("TAG", "onCreate: " );





    }

    private void connection(String id) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.0.107:9080/openam/json/qr_example/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;


        String url2 = "http://192.168.0.107:9081/json/serverinfo/*";


        JSONObject jsonBody = new JSONObject();
        jsonBody.put("Title", "Android Volley Demo");
        jsonBody.put("Author", "BNK");
        final String requestBody = jsonBody.toString();


        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("VOLLEY", response);
                    pb.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    alertErrore();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        Log.e("TAG", "parseNetworkResponse: " );
                        pb.setVisibility(View.GONE);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/json;charset=UTF-8");
                    return params;
                }
            };

            requestQueue.add(stringRequest);
        }catch(Exception e){
            Log.e("ERRORE COMUNICAZIONE","ERRORE");
            alertErrore();
        }

    }

    private void alertErrore(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='red'>ERRORE</font>"));
        builder.setMessage("Si è verificato un errore nella connessione al server");
        builder.setPositiveButton(Html.fromHtml("<font color='#039221'>>TORNA ALLA HOME</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(ServerConnection.this, Logged.class);
                startActivity(intent);
                finish();
            }
        });;
        builder.create();
        builder.show();

    }
}