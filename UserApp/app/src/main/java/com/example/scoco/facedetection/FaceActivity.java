package com.example.scoco.facedetection;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.scoco.facedetection.models.UserModel;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class FaceActivity extends AppCompatActivity {

    private static final String TAG = FaceActivity.class.getSimpleName();

    private Bitmap bitmap;
    private UserModel userModel;
    static boolean profileCreatedFlag;
    private SQLiteDatabaseHandler db;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        db = new SQLiteDatabaseHandler(this);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progressBar);

        profileCreatedFlag = false; // TODO FOR WHAT ?

        Bundle receiveBundle = this.getIntent().getExtras();
        userModel = receiveBundle.getParcelable(UserModel.class.getSimpleName());
        openDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(FaceActivity.this, "Image picker error", Toast.LENGTH_SHORT).show();
                Log.d("FaceActivity", "onImagePickerError: " + "Image picker error");
            }

            @Override
            public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {

                bitmap = new BitmapFactory().decodeFile(imageFile.getAbsolutePath());

                //TODO: send Data to Python Server
                sendDataToServer();
            }

        });
    }

    private void changeActivity(){
        Intent mainActivity = new Intent(FaceActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
    }

    private void openDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Disclaimer!");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        TextView msg = new TextView(this);
        // Message Properties
        msg.setText("\n Please allow camera to click your latest Picture! \n This will help in creating your unique identity");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Click a Pic!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
                Log.d(TAG, "Clicking Picture ");
                EasyImage.openCamera(FaceActivity.this, 100);
            }
        });


        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button camera = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) camera.getLayoutParams();
        neutralBtnLP.gravity = Gravity.CENTER;
        //okBT.setPadding(50, 10, 10, 10);   // Set Position
        camera.setTextColor(Color.BLUE);
        camera.setLayoutParams(neutralBtnLP);

    }

    private void sendDataToServer() {

        mLoadingIndicator.setVisibility(View.VISIBLE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        String URL ="http://192.168.0.40:8080/image_recog";
        //sending image to flask server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                // TODO Receive the unique id of user and store it in sql db, also receive data of user if already created before
                try {
                    JSONObject obj = new JSONObject(s);
                    if(obj.getString("status").equals("success"))
                    {

                        String firstName = obj.getString("firstName");
                        String lastName = obj.getString("lastName");
                        String contact = obj.getString("contact");
                        String id = obj.getString("uniqueId");
                        String email = userModel.userEmail;
                        User user = new User(id, firstName, lastName, contact, email);
                        db.addUser(user);
                        Toast.makeText(FaceActivity.this, "Thank you for patience!", Toast.LENGTH_LONG).show();
                        changeActivity();
                    }
                    else
                    {
                        Toast.makeText(FaceActivity.this, "Error, Please try again in some time! " + obj.getString("status"), Toast.LENGTH_LONG).show();
                        openDialog();
                    }
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(FaceActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imageString);
                parameters.put("name", "TakeImage");
                parameters.put("email", userModel.userEmail);
                parameters.put("userName", userModel.userName);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(FaceActivity.this);
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
