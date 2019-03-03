package com.example.scoco.facedetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class UpdateDetails extends AppCompatActivity implements View.OnClickListener {

    private EditText FirstName;
    private EditText LastName;
    private EditText contact;
    private Button Update;
    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        db = new SQLiteDatabaseHandler(this);
        FirstName = (EditText) findViewById(R.id.FirstName);
        LastName = (EditText) findViewById(R.id.LastName);
        contact = (EditText) findViewById(R.id.contact);
        Update = (Button) findViewById(R.id.Update);

        FirstName.setText(db.getUser().firstName, TextView.BufferType.EDITABLE);
        LastName.setText(db.getUser().lastName, TextView.BufferType.EDITABLE);
        contact.setText(db.getUser().contact, TextView.BufferType.EDITABLE);

        Update.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // Address of the flask server
        String URL ="http://192.168.0.40:8080/image_recog";

        final String firstName = FirstName.getText().toString();
        final String lastName = LastName.getText().toString();
        final String contact_ = contact.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                // TODO Receive the unique id of user and store it in sql db, also receive data of user if already created before
                try {
                    JSONObject obj = new JSONObject(s);
                    if(obj.getString("status").equals("success"))
                    {
                        User user = new User(db.getUser().id, firstName, lastName, contact_, db.getUser().email);
                        db.addUser(user);

                        Intent i = new Intent(UpdateDetails.this, ProfileActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(UpdateDetails.this, "Error, Please try again in some time! " + obj.getString("status"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(UpdateDetails.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("name", "updateUserData");
                parameters.put("uniqueId", db.getUser().id);
                parameters.put("newFirstName", firstName);
                parameters.put("newLastName", lastName);
                parameters.put("newContact", contact_);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(UpdateDetails.this);
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
            public void retry(VolleyError error) throws VolleyError {

            }
        });


    }
}
