package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
    }
    public void onSubmitButton(View view){
//        when button press save username into shared prefs
//        get the text
        EditText usernameText = findViewById(R.id.usernameInputText);
//                turn it into a string
        String username = usernameText.getText().toString();

//        grab the correct shared pref folder
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        store it in shared prefs
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", username);
        editor.apply();
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.submitConfimation), Toast.LENGTH_SHORT).show();
        finish();

//        display a toast
//        return to home


    }
}


