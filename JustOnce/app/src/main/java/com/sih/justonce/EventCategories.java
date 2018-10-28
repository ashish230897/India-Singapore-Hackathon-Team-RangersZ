package com.sih.justonce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class EventCategories extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_categories);
    }

    @Override
    protected void onStart(){
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){

            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }

    public void logoutUser(View view){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, Login.class));
    }
}
