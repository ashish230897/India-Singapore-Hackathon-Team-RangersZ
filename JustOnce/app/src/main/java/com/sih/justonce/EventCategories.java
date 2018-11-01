package com.sih.justonce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class EventCategories extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_categories);

        ArrayAdapter<Category> listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Category.getCategories());
        ListView categoryList = (ListView)findViewById(R.id.categories);
        categoryList.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent to migrate to events activity
            }
        };

        categoryList.setOnItemClickListener(itemClickListener);

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
