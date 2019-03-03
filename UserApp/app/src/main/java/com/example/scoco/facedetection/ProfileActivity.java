package com.example.scoco.facedetection;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    TextView digitalIdentitiesTextView;
    TextView contactTextView;
    TextView unameTextView;
    Button updatedetailsButton;

    private SQLiteDatabaseHandler db;

    @Override
    protected void onStart() {
        super.onStart();
        fetchDetailsFromChain();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new SQLiteDatabaseHandler(this);
        Log.d(TAG, "Activity Started");

        digitalIdentitiesTextView = (TextView) findViewById(R.id.email);
        contactTextView = (TextView) findViewById(R.id.contact);
        unameTextView = (TextView) findViewById(R.id.uname);
        updatedetailsButton = (Button) findViewById(R.id.updateDetails);



        updatedetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Update Detail button pressed");
                Intent i = new Intent(ProfileActivity.this, UpdateDetails.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

    }

    protected void fetchDetailsFromChain(){
        digitalIdentitiesTextView.setText(db.getUser().email);
        contactTextView.setText(db.getUser().contact);
        unameTextView.setText(db.getUser().firstName + " " + db.getUser().lastName);
    }
}
