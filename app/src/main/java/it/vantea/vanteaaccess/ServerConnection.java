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
        String url = "http://10.24.64.128:9080/openam/json/realm=google-totp/authenticate?&authIndexType=service&authIndexValue=qr&ForceAuth=true&sessionUpgradeSSOTokenId="+id;

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("VOLLEY", response);
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
                    return null;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
        }catch(Exception e){
            Log.e("ERRORE COMUNICAZIONE","ERRORE");
        }


    }
}