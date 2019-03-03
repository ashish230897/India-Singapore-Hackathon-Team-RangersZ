package com.example.scoco.facedetection;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    ListView eventsList;
    ArrayList<String> events;
    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        db = new SQLiteDatabaseHandler(this);
        eventsList = (ListView) findViewById(R.id.eventsList);
        Log.d("Inside Events", "Hell yeah!");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    events = new ArrayList<>();

                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        events.add(String.valueOf(ds.getKey()));
                    }
                }
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, events);
                eventsList.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EventsActivity.this, EventRegistration.class);
                intent.putExtra(EventRegistration.EXTRA_EVENTNAME, String.valueOf(events.get((int) id)));
                startActivity(intent);
            }
        };

        eventsList.setOnItemClickListener(itemClickListener);

    }
}