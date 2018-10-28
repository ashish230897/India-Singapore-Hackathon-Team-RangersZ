package com.sih.justonce;

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

public class SignUp extends AppCompatActivity {

    private EditText name;
    private EditText age;
    private EditText number;
    private EditText email;
    private EditText password;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


    }

    @Override
    protected void onStart(){
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){

            finish();
            startActivity(new Intent(getApplicationContext(), EventCategories.class));
        }
    }

    public void registerNewUser(View view){

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        number = (EditText) findViewById(R.id.number);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        String _email = email.getText().toString().trim();
        String _password = password.getText().toString().trim();


        boolean isEmpty = TextUtils.isEmpty(_email) || TextUtils.isEmpty(_password);
        if(isEmpty)
        {
            Toast.makeText(this, "Please enter all the details!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(_email,_password).addOnCompleteListener(this, new
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
                            startActivity(new Intent(getApplicationContext(), EventCategories.class));
                        }
                        else
                        {
                            Toast.makeText(SignUp.this, "Could not register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
