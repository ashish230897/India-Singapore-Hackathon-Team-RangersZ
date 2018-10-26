package com.example.ashish.managermainactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserNumber extends Activity {

    public static final String EXTRA_EVENTNO = "eventid";
    private DatabaseReference databaseReference;
    ArrayList<String> users;
    private ListView eventusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_number);

        final int eventid = (Integer)getIntent().getExtras().get(EXTRA_EVENTNO);
        eventusers = (ListView) findViewById(R.id.eventusers);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    users = new ArrayList<>();
                    int temp = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if(temp == eventid) {
                            for (DataSnapshot ds1 : ds.getChildren()) {
                                users.add(String.valueOf(ds1.getValue(String.class)));
                            }
                            break;
                        }
                        temp += 1;
                    }

                }
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, users);
                eventusers.setAdapter(listAdapter);
                //users.clear();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
