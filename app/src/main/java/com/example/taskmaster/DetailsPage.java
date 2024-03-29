package com.example.taskmaster;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Chooses the page to display the following content
        setContentView(R.layout.activity_details_page);
//        Get the task name associated with the intent


//        set the title view to equal the task
        String task = getIntent().getStringExtra("task");
        TextView title = findViewById(R.id.detailsTitle);
        title.setText(task);

        String details = getIntent().getStringExtra("details");
        TextView body = findViewById(R.id.detailsTextBody);
        body.setText(details);

        String loc = getIntent().getStringExtra("location");
        TextView taskloc = findViewById(R.id.locationData);
        taskloc.setText(loc);


    }
}