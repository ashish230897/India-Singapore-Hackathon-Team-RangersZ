package com.example.scoco.facedetection;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.scoco.facedetection.models.UserModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private SQLiteDatabaseHandler db;

    private GoogleSignInClient mGoogleSignInClient;

    private Collection<String> permissions = Arrays.asList("public_profile ", "email", "user_birthday", "user_location");
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        db = new SQLiteDatabaseHandler(this);
        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions((List<String>) permissions);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (loginResult != null) {
                            Log.d(TAG, "onSuccess FB");
                            callGraphAPI(loginResult.getAccessToken());
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel FB");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "onError FB Listener");
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
    }

    private void callGraphAPI(AccessToken accessToken) {
        Log.d(TAG,"call Graph Api invoked");
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        OnFbSuccess(response);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,birthday,email,first_name,gender,last_name,link,location,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else if (callbackManager != null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account, false);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null, false);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(@Nullable GoogleSignInAccount account, boolean previouslySignedIn) {

        if (account != null) {
            // Using Usermodel class here
            UserModel userModel = new UserModel();
            userModel.userName = account.getDisplayName();
            userModel.userEmail = account.getEmail();
            userModel.profilePic = String.valueOf(account.getPhotoUrl());

            Bundle sendBundle = new Bundle();
            sendBundle.putParcelable(UserModel.class.getSimpleName(), userModel);

            if(previouslySignedIn == false ) {
                //TODO: Put this data in MySqlite and remove passing of data from Intents
                chooseActivity(sendBundle, "FaceActivity");

            }else{
                //TODO: Put this data in MySqlite and remove passing of data from Intents
                chooseActivity(sendBundle, "MainActivity");
            }
        } else {

        }
    }

    private void chooseActivity(Bundle sendBundle, String activityName){

        switch (activityName) {
            case "MainActivity":
                Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainActivity.putExtras(sendBundle);
                startActivity(mainActivity);
                finishAffinity();
                break;
            case "FaceActivity":
                Intent faceActivity = new Intent(SignInActivity.this, FaceActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                faceActivity.putExtras(sendBundle);
                startActivity(faceActivity);
                finishAffinity();
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
        }
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void OnFbSuccess(GraphResponse graphResponse) {
        Log.d(TAG, "Fb Login Success ");

        UserModel userModel = getUserModelFromGraphResponse(graphResponse);
        Bundle sendBundle = new Bundle();
        sendBundle.putParcelable(UserModel.class.getSimpleName(), userModel);
        if( isLoggedIn() ) {
            //TODO: Put this data in MySqlite and remove passing of data from Intents
            chooseActivity(sendBundle, "FaceActivity");

        }else{
            //TODO: Put this data in MySqlite and remove passing of data from Intents
            chooseActivity(sendBundle, "MainActivity");
        }
    }

    private UserModel getUserModelFromGraphResponse(GraphResponse graphResponse)
    {
        Log.d(TAG, "getUserModelFromGraphResponse");
        UserModel userModel = new UserModel();
        try {
            JSONObject jsonObject = graphResponse.getJSONObject();
            userModel.userName = jsonObject.getString("name");
            userModel.userEmail = jsonObject.getString("email");
            String id = jsonObject.getString("id");
            String profileImg = "http://graph.facebook.com/" + id + "/picture?type=large";
            userModel.profilePic = profileImg;
            Log.i(TAG, profileImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userModel;
    }

}
