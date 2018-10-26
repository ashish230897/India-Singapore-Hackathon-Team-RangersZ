package com.example.ashish.firstversionapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText age;
    private EditText number;
    private EditText emailAddress;
    private EditText Password;

    private Button buttonRegister;
    private TextView signin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        number = (EditText) findViewById(R.id.number);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        Password = (EditText) findViewById(R.id.Password);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        signin = (TextView) findViewById(R.id.signin);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null)
        {
            //Launch other activity
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        buttonRegister.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    private void registerUser()
    {
        String _emailAddress = emailAddress.getText().toString().trim();
        String _Password = Password.getText().toString().trim();


        boolean isEmpty = TextUtils.isEmpty(_emailAddress) || TextUtils.isEmpty(_Password);
        if(isEmpty)
        {
            Toast.makeText(this, "Please enter all the details!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(_emailAddress,_Password).addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String _name = name.getText().toString().trim();
                            String _age = age.getText().toString().trim();
                            String _number = number.getText().toString().trim();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                            UserInfo userInfo = new UserInfo(_name, _age, _number);
                            databaseReference.setValue(userInfo);
                            Toast.makeText(getApplicationContext(), "Information saved", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Could not register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    @Override
    public void onClick(View view)
    {
        if(view == buttonRegister)
        {
            registerUser();
        }
        if(view == signin)
        {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
