package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddATask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_atask);


        Button submitTask = findViewById(R.id.submitTaskButton);
        submitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText taskTitleInput = findViewById(R.id.taskTitleInput);
                String title = taskTitleInput.getText().toString();
                EditText taskBodyInput = findViewById(R.id.taskBodyInput);
                String taskBody = taskBodyInput.getText().toString();

//                store the things in sharedPreferances
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this)
//
//                SharedPreferences.Editor editor = prefs;

//                grab the shared preferance
//
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.submitConfimation), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
