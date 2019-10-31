package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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

    }
//    https://developer.android.com/guide/topics/ui/controls/radiobutton#java
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.team1:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.team2:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.team3:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
}


