package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button addTask = findViewById(R.id.addTask);
        //        setup an eventlistener for addtask
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View event) {
                Intent goToAddTask = new Intent(MainActivity.this, AddATask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

        Button allTasks = findViewById(R.id.allTasks);
        allTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
                MainActivity.this.startActivity(goToAllTasks);

            }
        });

        Button settings = findViewById(R.id.goToSettingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(MainActivity.this, SettingsPage.class);
                MainActivity.this.startActivity(goToSettings);
            }
        });
    }

    public void onTaskSelection(View view){
        Button taskButton = findViewById(view.getId());
        String buttonText = taskButton.getText().toString();
        System.out.println("String is " + buttonText );
        Intent goToDetailsPage = new Intent(MainActivity.this,DetailsPage.class);
        goToDetailsPage.putExtra("task", buttonText);
        MainActivity.this.startActivity(goToDetailsPage);
    }

}
