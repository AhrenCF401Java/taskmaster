package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddATask extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_atask);

        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,MainActivity.DATABASE_NAME).allowMainThreadQueries().build();

        Button submitTask = findViewById(R.id.submitTaskButton);
        submitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText taskTitleInput = findViewById(R.id.taskTitleInput);
                String title = taskTitleInput.getText().toString();
                EditText taskBodyInput = findViewById(R.id.taskBodyInput);
                String taskBody = taskBodyInput.getText().toString();
//                store in database
                db.taskDao().addTask(new Task(title,taskBody));
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.submitConfimation), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
