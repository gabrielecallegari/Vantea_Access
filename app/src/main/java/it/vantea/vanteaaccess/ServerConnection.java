package it.vantea.vanteaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String id2 = "122.122.122.122";
        String url = "http://192.168.0.107:9080/openam/json/qr_example/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;


        String url2 = "http://192.168.0.107:9081/json/serverinfo/*";

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
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.e("VOLLEY", "Errore, autenticazione fallita");
                    return null;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        Log.e("TAG", "parseNetworkResponse: " );
                        // can get more details such as response.headers
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
        }


    }
}