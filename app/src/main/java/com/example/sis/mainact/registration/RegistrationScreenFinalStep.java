package com.example.sis.mainact.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sis.R;
import com.example.sis.mainact.login.LoginScreen;

public class RegistrationScreenFinalStep extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen_final_step);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(RegistrationScreenFinalStep.this, LoginScreen.class);
                startActivity(mainIntent);
            }
        });
    }
}