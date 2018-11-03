package com.sih.justonce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Events extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ArrayAdapter<Event> listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Event.getEvents());
        ListView eventList = (ListView)findViewById(R.id.events);
        eventList.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent to migrate to event details activity
                Intent intent = new Intent(Events.this, EventDetails.class);
                intent.putExtra(EventDetails.EXTRA_NAME,Event.events[position].getName());
                intent.putExtra(EventDetails.EXTRA_DESC,Event.events[position].getDescription());
                startActivity(intent);
            }
        };

        eventList.setOnItemClickListener(itemClickListener);

    }
}
