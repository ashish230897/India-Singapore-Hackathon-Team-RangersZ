package com.example.ashish.firstversionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends Activity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button logoutButton;
    private Button buttonEventRegister;
    private Button buttonEventUnRegister;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutButton = (Button) findViewById(R.id.logoutButton);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();

        ArrayAdapter<Events> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                                            Events.events);
        ListView eventsList = (ListView) findViewById(R.id.eventsList);
        eventsList.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProfileActivity.this, EventDetails.class);
                intent.putExtra(EventDetails.EXTRA_EVENTNO, (int) id);
                startActivity(intent);
            }
        };

        eventsList.setOnItemClickListener(itemClickListener);

        logoutButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view == logoutButton)
        {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
