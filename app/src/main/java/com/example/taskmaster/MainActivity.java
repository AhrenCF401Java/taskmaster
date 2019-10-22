package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button addTask = findViewById(R.id.addTask);
        //        setup an eventlistener for addtask
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                // go to the other activity
                // Create the intent to go to that activity and start it!
                Log.i()
                Intent goToAddTask = new Intent(MainActivity.this, AddATask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

        Button allTasks = findViewById(R.id.allTasks);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);

            }
        });

    }
}
