package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener{

    protected List<Task> tasks;
    public AppDatabase db;
    public static final String DATABASE_NAME = "task_to_do";

    @Override
    protected void onResume() {
        super.onResume();
        // grab username from sharedprefs and use it to update the label
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "user");
//        TextView nameTextView = findViewById(R.id.);
//        nameTextView.setText("Hi, " + username + "!");
//
        renderDatabaseOnRecycledView();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        renderDatabaseOnRecycledView();

        Button addTask = findViewById(R.id.addTask);
        //        setup an event listener for addtask
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


    private void renderDatabaseOnRecycledView(){
        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        this.tasks = new LinkedList<>();
        this.tasks.addAll(db.taskDao().getAll());

        final RecyclerView  taskRecycler = findViewById(R.id.taskList);
//        taskRecycler manager
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
//        set adapter
        taskRecycler.setAdapter(new TaskAdapter(tasks,  this));
    }


    public void onTaskSelection(View view){
        Button taskButton = findViewById(view.getId());
        String buttonText = taskButton.getText().toString();
        System.out.println("String is " + buttonText );
        Intent goToDetailsPage = new Intent(MainActivity.this,DetailsPage.class);
        goToDetailsPage.putExtra("task", buttonText);
        MainActivity.this.startActivity(goToDetailsPage);
    }

    @Override
    public void taskCommand(Task task) {
        Intent goToDetailsPage = new Intent(MainActivity.this,DetailsPage.class);
        goToDetailsPage.putExtra("task", task.getTitle());
        goToDetailsPage.putExtra("task", task.getState());
        goToDetailsPage.putExtra("task", task.getBody());
        MainActivity.this.startActivity(goToDetailsPage);
    }
}
