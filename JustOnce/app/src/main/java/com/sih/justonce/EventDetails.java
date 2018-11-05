package com.sih.justonce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EventDetails extends AppCompatActivity {

    public static final String EXTRA_NAME = "event_name";
    public static final String EXTRA_DESC = "event_desc";
    public static final String EXTRA_START = "start_date";
    public static final String EXTRA_END = "end_date";
    private String eventName;
    private String eventDescription;
    private String startDate;
    private String endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        eventName = (String)getIntent().getExtras().get(EXTRA_NAME);
        eventDescription = (String)getIntent().getExtras().get(EXTRA_DESC);

        TextView nameView = (TextView)findViewById(R.id.event_name);
        TextView descView = (TextView)findViewById(R.id.event_description);

        nameView.setText(eventName);
        descView.setText(eventDescription);
    }
}
