package com.example.taskmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import type.CreateTaskInput;

public class AddATask extends AppCompatActivity {

    private AppDatabase db;
    AWSAppSyncClient mAWSAppSyncClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_atask);

        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,MainActivity.DATABASE_NAME).allowMainThreadQueries().build();

        Button submitTask = findViewById(R.id.submitTaskButton);
        submitTask.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                EditText taskTitleInput = findViewById(R.id.taskTitleInput);
                String title = taskTitleInput.getText().toString();
                EditText taskBodyInput = findViewById(R.id.taskBodyInput);
                String taskBody = taskBodyInput.getText().toString();
//                store in database
                db.taskDao().addTask(new Task(title,taskBody));
//
//                store to remote DB
                try {
                    run(title,taskBody);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.failedToPost), Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(getApplicationContext(),getResources().getString(R.string.submitConfimation), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void taskMutation(String title, String body){
        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(title)
                .body(body)
                .state("New")
                .build();

        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(mutationCallback);
    }



    private final OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void run(String title, String body) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("title", title)
                .add("body", body)
                .build();
        Request request = new Request.Builder()
                .url("https://taskmaster-api.herokuapp.com/tasks")
                .post(formBody)
                .build();
//        see what happend
        client.newCall(request).enqueue(new showTheResponseCallback());
    }


    class showTheResponseCallback implements Callback{

        private static final String TAG = "taskmaster.Callback";

        // OkHttp will call this if the request fails
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.e(TAG, "internet error");
            Log.e(TAG, e.getMessage());
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.storedInDB), Toast.LENGTH_SHORT).show();
                }
                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.failedToPost), Toast.LENGTH_SHORT).show();
                    throw new IOException("Unexpected code " + response);
                }
            }
        }
}
