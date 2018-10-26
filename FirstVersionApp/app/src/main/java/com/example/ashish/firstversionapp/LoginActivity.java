package com.example.ashish.firstversionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button buttonLogin;
    private EditText emailId;
    private EditText PassKey;
    private TextView SignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        emailId = (EditText) findViewById(R.id.emailId);
        PassKey = (EditText) findViewById(R.id.PassKey);
        SignUp = (TextView) findViewById(R.id.SignUp);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null)
        {
            //Launch other activity
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        buttonLogin.setOnClickListener(this);
        SignUp.setOnClickListener(this);
    }

    private void loginUser()
    {
        String _emailId = emailId.getText().toString().trim();
        String _PassKey = PassKey.getText().toString().trim();

        if(TextUtils.isEmpty(_emailId) || TextUtils.isEmpty(_PassKey))
        {
            Toast.makeText(this, "Please enter all the information!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(_emailId,_PassKey).addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Could not register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    @Override
    public void onClick(View view)
    {
        if(view == buttonLogin)
        {
            loginUser();
        }
        if(view == SignUp)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
