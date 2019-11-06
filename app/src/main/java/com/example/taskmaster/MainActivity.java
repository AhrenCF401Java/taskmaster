package com.example.taskmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.CreateTeamMutation;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import type.CreateTaskInput;
import type.CreateTeamInput;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener{

    private static final String TAG = "ahren.taskmaster" ;
    AWSAppSyncClient mAWSAppSyncClient;
    protected List<Task> tasks;
    public AppDatabase db;
    public static final String DATABASE_NAME = "task_to_do";
    RecyclerView taskRecycler;

//      *************************************** On Create *****************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new com.amazonaws.mobile.client.Callback<UserStateDetails>() {

                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("INIT", "Initialization error.", e);
                    }
                }
        );
//        Init app sync client
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        setContentView(R.layout.activity_main);
//       sets up the recycler view.
        renderDatabaseOnRecycledView();

        Button addTask = findViewById(R.id.addTask);
//      setup an event listener for addtask
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

        final Button authButton = findViewById(R.id.authButton);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AWSMobileClient.getInstance().isSignedIn()) {
                    AWSMobileClient.getInstance().signOut();
                    authButton.setText("Sign In");
                }else{
                    AWSMobileClient.getInstance().showSignIn(MainActivity.this,
                            SignInUIOptions.builder().build(),
                            new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
                                @Override
                                public void onResult(UserStateDetails result) {

                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }
            }
        });
    }





//  *************************************** On Resume  ********************************************
    @Override
    protected void onResume() {
        final Button authButton = findViewById(R.id.authButton);
        final TextView username = findViewById(R.id.usernameDisplay);

        if(AWSMobileClient.getInstance().isSignedIn()){
            authButton.setText("Sign Out " + AWSMobileClient.getInstance().getUsername());
            username.setText(AWSMobileClient.getInstance().getUsername());
        }else{
            authButton.setText("Sign In");
            username.setText("");

        }

        super.onResume();
        // grab username from sharedprefs and use it to update the label
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        String username = prefs.getString("user", "user");
//        TextView nameTextView = findViewById(R.id.usernameDisplay);
//        nameTextView.setText("Hi, " + username + "!");
//
        getData();


//        getDataOkHTTP();
    }


//    add a n area in setting to add teams add a spinner to select team and add tasks for that team only
    public void teamMutation(final String team){
        CreateTeamInput createTeamInput = CreateTeamInput.builder()
                .name(team)
                .build();
//gets data from Dynamo db
        mAWSAppSyncClient.mutate(CreateTeamMutation.builder().input(createTeamInput).build())
                .enqueue(new GraphQLCall.Callback<CreateTeamMutation.Data>(){
                    @Override
                    public void onResponse(@Nonnull Response<CreateTeamMutation.Data> response) {

                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }





//    gets all data from AWS
    public void getData(){
        mAWSAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(new GraphQLCall.Callback<ListTasksQuery.Data>() {
                    @Override
//                    creates a list of tasks from the data that is received
                    public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {
                        tasks = new LinkedList<>();
                        System.out.println(response.data().toString() + "SAMARI");
                        if (response.data().listTasks() != null){
                            List<ListTasksQuery.Item> responseItems = response.data().listTasks().items();
                        for (ListTasksQuery.Item item : responseItems) {
                            Task task = new Task(item.title(), item.body());
                            tasks.add(task);
                        }
//                        Render it to the recycler view
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            public void handleMessage(Message inputMessage) {
                                taskRecycler.setAdapter(new TaskAdapter(tasks, MainActivity.this));
                            }

                        };
                        handler.obtainMessage().sendToTarget();
                      }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }




    private void renderDatabaseOnRecycledView(){
//        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        this.tasks = new LinkedList<>();
//        this.tasks.addAll(db.taskDao().getAll());
        taskRecycler = findViewById(R.id.taskList);
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





    private final OkHttpClient client = new OkHttpClient();
    public void getDataOkHTTP() {
        Request request = new Request.Builder()
                .url("https://taskmaster-api.herokuapp.com/tasks")
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        String responseB = responseBody.string();

                        System.out.println(responseB + "RESPONSE!!!");
                        Gson gson = new Gson();

                        Type taskBag = new TypeToken<LinkedList<Task>>(){}.getType();
                        tasks = gson.fromJson(responseB, taskBag);

                        Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message inputMessage) {
                                // grab data out of Message object and pass to actualMainActivityInstance
                                taskRecycler.setAdapter(new TaskAdapter(tasks, MainActivity.this));
                            }
//
                        };
                        handlerForMainThread.obtainMessage().sendToTarget();
                    }
                }
            });
        }
    }
}