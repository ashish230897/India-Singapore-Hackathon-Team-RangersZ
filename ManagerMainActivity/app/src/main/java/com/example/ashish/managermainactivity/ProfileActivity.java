package com.example.ashish.managermainactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends Activity implements View.OnClickListener{

    private Button buttonLogout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter<Events> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                Events.events);
        ListView eventsList = (ListView) findViewById(R.id.eventsList);
        eventsList.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProfileActivity.this, UserNumber.class);
                intent.putExtra(UserNumber.EXTRA_EVENTNO, (int) id);
                startActivity(intent);
            }
        };
        eventsList.setOnItemClickListener(itemClickListener);
        buttonLogout.setOnClickListener(this);
    }

    public void logout()
    {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout)
        {
            logout();
        }
    }
}
