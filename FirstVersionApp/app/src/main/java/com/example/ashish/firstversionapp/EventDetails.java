package com.example.ashish.firstversionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetails extends Activity implements View.OnClickListener {

    public static final String EXTRA_EVENTNO = "eventid";
    private ImageView photo;
    private TextView name;
    private TextView description;
    private Button buttonRegister;
    private Button buttonUnRegister;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        photo = (ImageView) findViewById(R.id.photo);
        name = (TextView) findViewById(R.id.name);
        description = (TextView) findViewById(R.id.description);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonUnRegister = (Button) findViewById(R.id.buttonUnRegister);

        int eventid = (Integer)getIntent().getExtras().get(EXTRA_EVENTNO);
        Events event = Events.events[eventid];

        name.setText(event.getName());
        description.setText(event.getDescription());
        photo.setImageResource(event.getImageResourceId());
        photo.setContentDescription(event.getName());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(event.getName()).child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    buttonRegister.setEnabled(false);
                    buttonUnRegister.setEnabled(true);
                }
                else
                {
                    buttonRegister.setEnabled(true);
                    buttonUnRegister.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buttonRegister.setOnClickListener(this);
        buttonUnRegister.setOnClickListener(this);
    }

    public void registerUser()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String info = user.getEmail().toString();
        databaseReference.setValue(info);
        buttonRegister.setEnabled(false);
        buttonUnRegister.setEnabled(true);
    }

    public void unRegisterUser()
    {
        databaseReference.removeValue();
        buttonRegister.setEnabled(true);
        buttonUnRegister.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister)
        {
            registerUser();
        }

        if(view == buttonUnRegister)
        {
            unRegisterUser();
        }

    }
}
