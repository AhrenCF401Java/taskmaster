package com.example.taskmaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import type.CreateTaskInput;

public class AddATask extends AppCompatActivity {

    private static final String TAG = "ahren.AddATask";
    private AppDatabase db;
    AWSAppSyncClient mAWSAppSyncClient;
    private String picturePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_atask);


        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,MainActivity.DATABASE_NAME).allowMainThreadQueries().build();

        Button submitTask = findViewById(R.id.submitTaskButton);
        submitTask.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                EditText taskTitleInput = findViewById(R.id.taskTitleInput);
                final String title = taskTitleInput.getText().toString();
                EditText taskBodyInput = findViewById(R.id.taskBodyInput);
                final String taskBody = taskBodyInput.getText().toString();
//                store in database
//                db.taskDao().addTask(new Task(title,taskBody));
////
////                store to remote DB
//                try {
//                    run(title,taskBody);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.failedToPost), Toast.LENGTH_SHORT).show();
//                }
//
//                Toast.makeText(getApplicationContext(),getResources().getString(R.string.submitConfimation), Toast.LENGTH_SHORT).show();


//                Store in AWS
                if(picturePath == null) {
                    taskMutation(title, taskBody);
                }else{
                    TransferUtility transferUtility =
                            TransferUtility.builder()
                                    .context(getApplicationContext())
                                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                                    .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                                    .build();
                    final TransferObserver uploadObserver =
                            transferUtility.upload(
                                    // filename in the
                                    "public/" + picturePath.substring(picturePath.lastIndexOf("/") + 1),
                                    new File(picturePath));
                    picturePath = null;


                    // Attach a listener to the observer to get state update and progress notifications
                    uploadObserver.setTransferListener(new TransferListener() {


                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (TransferState.COMPLETED == state) {
                                System.out.println("************************************************************\n"
                                        + uploadObserver.getBucket() + "\n"
                                        + uploadObserver.getId() + "\n"
                                        + uploadObserver.getKey() + "\n"
                                        + "hello, its me your looking for *********************************************************************");
                                taskMutation(title,taskBody);
                            }
                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                            int percentDone = (int) percentDonef;

                            Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                                    + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            // Handle errors
                        }
                    });
                }
            }
        });

    }

    public void taskMutation(String title, String body){
        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(title)
                .body(body )
                .state("New")
                .build();
//gets data from Dynamo db
        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(new GraphQLCall.Callback<CreateTaskMutation.Data>() {
                    @Override
//                    what happens on response
                    public void onResponse(@Nonnull com.apollographql.apollo.api.Response<CreateTaskMutation.Data> response) {
                        Log.i("Add Task", "Posted");
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }



    private static final int READ_REQUEST_CODE = 42;
    public void pickAFile(View v){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, READ_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                Uri selectedImage = uri;

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

            }
        }
    }


//    public String rename()
    


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
