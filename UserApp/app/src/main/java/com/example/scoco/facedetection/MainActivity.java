package com.example.scoco.facedetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scoco.facedetection.models.UserModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //-------WEB CRAWL
    public WebView wv;
    public String url;
    Button registerButton;
    Button Back;

    private TextView title;
    private TextView subtitle;
    private String userEmail;
    private String EventUrl = " ";

    private GoogleSignInClient mGoogleSignInClient;
    private SQLiteDatabaseHandler db;

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //-----------WEBCRAWL
        registerButton = (Button) findViewById(R.id.registerButton);
        Back = (Button) findViewById(R.id.Back);
        wv = (WebView) findViewById(R.id.webview);
        url = "http://www.ntu.edu.sg/CampusLife/Pages/default.aspx";
        callWebClient(url);

        db = new SQLiteDatabaseHandler(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        title = (TextView) headerView.findViewById(R.id.nav_header_title);
        subtitle = (TextView) headerView.findViewById(R.id.nav_header_subtitle);

        title.setText(db.getUser().firstName + " " + db.getUser().lastName);

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void back(View v)
    {
        url = "http://www.ntu.edu.sg/CampusLife/Pages/default.aspx";
        callWebClient(url);
    }

    public void register(View v) {
        //TODO Take the Id of user form the sqlite database and send it to server
        Toast.makeText(this, "In register!", Toast.LENGTH_SHORT).show();
        final String Userid = db.getUser().id;
        //Address of the flask server
        String URL ="http://192.168.0.40:8080/image_recog";

        //sending id to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                // TODO Receive the unique id of user and store it in sql db, also receive data of user if already created before
                try {
                    JSONObject obj = new JSONObject(s);
                    if(obj.getString("status").equals("success"))
                    {
                        Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Error, Please try again in some time! " + obj.getString("status"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("name", "AddEvent");
                parameters.put("uniqueId", Userid);
                parameters.put("eventName", EventUrl);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
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

    private void callWebClient(String url) {
        wv.setWebViewClient(new myWebViewClient());
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.loadUrl(url);
    }

    public class myWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("new_url", "gokhale " + url);
            view.loadUrl(url);
            EventUrl = url;
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            Log.d(TAG, "Profile Tab Clicked");
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);

        }
        else if (id == R.id.qr) {
            // There is error in FaceActivity due to QR's dependency
            Log.d(TAG, "QR Clicked");
            Intent i = new Intent(MainActivity.this, QrActivity.class);
            startActivity(i);
        }
        else if (id == R.id.sign_out)
        {
            Log.d(TAG, "Sign Out Clicked");
            signout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signout(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        changeActivity();
                    }
                });
        LoginManager.getInstance().logOut();
    }

    private void changeActivity() {
        Intent signInActivity = new Intent(MainActivity.this, SignInActivity.class);
        finishAffinity();
        startActivity(signInActivity);
    }
}
