package com.example.scoco.facedetection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventRegistration extends AppCompatActivity implements View.OnClickListener {

    Button Register;
    public static final String EXTRA_EVENTNAME = "event_name";
    String eventName;
    private SQLiteDatabaseHandler db;
    String Userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        db = new SQLiteDatabaseHandler(this);

        eventName = getIntent().getStringExtra(EXTRA_EVENTNAME);
        Register = (Button) findViewById(R.id.Register);
        Register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //TODO Take the Id of user form the sqlite database and send it to server
        Userid = db.getUser().id;
        String URL ="http://192.168.43.248:8080/image_recog";

        //sending id to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                // TODO Receive the unique id of user and store it in sql db, also receive data of user if already created before
                try {
                    JSONObject obj = new JSONObject(s);
                    if(obj.getString("status").equals("success"))
                    {
                        Toast.makeText(EventRegistration.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                        Register.setEnabled(false);

                    }
                    else
                    {
                        Toast.makeText(EventRegistration.this, "Error, Please try again in some time! " + obj.getString("status"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(EventRegistration.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("name", "AddEvent");
                parameters.put("uniqueId", Userid);
                parameters.put("eventName", eventName);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(EventRegistration.this);
        rQueue.add(request).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 0; //retry turn off
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {}
        });

    }
}
