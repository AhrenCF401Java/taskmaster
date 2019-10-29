package com.example.taskmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener{

    protected LinkedList<Task> tasks;
    public AppDatabase db;
    public static final String DATABASE_NAME = "task_to_do";

    @Override
    protected void onResume() {
        super.onResume();
        // grab username from sharedprefs and use it to update the label
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = prefs.getString("user","user");
        TextView nameTextView = findViewById(R.id.welcome);
        nameTextView.setText("Hi, " + username + "!");
//
        renderDatabaseOnRecycledView();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//  sets up the recycler view.
        renderDatabaseOnRecycledView();

        Button addTask = findViewById(R.id.addTask);
//  setup an event listener for addtask
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









    private final OkHttpClient client = new OkHttpClient();

    public void getData() throws Exception {
        Request request = new Request.Builder()
                .url("https://taskmaster-api.herokuapp.com/tasks")
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }

                        String responseB = response.body().string();
                        Gson gson = new Gson();
                        Type taskBag = new TypeToken<List<Task>>(){}.getType();
                        tasks = gson.fromJson(responseB,taskBag);
                    }
                }
            });
        }
    }


    private void renderDatabaseOnRecycledView(){
//        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        this.tasks = new LinkedList<>();
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.tasks.addAll(db.taskDao().getAll());

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
