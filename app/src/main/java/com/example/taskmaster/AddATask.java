package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

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
                // set the text of the thing to be that text

//        TextView textView = findViewById(R.id.submittedConfirmationText);
//        textView.setText(getResources().getString(R.string.submitConfimation));
                
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.submitConfimation), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
