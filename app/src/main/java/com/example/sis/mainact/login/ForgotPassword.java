package com.example.sis.mainact.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chaos.view.PinView;
import com.example.sis.R;
import com.example.sis.mainact.Constants;
import com.example.sis.mainact.registration.JavaMailAPI;
import com.example.sis.mainact.registration.RegistrationDataStore;
import com.example.sis.mainact.registration.RegistrationScreenFourthStep;
import com.example.sis.mainact.registration.RegistrationScreenThirdStep;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Random;

public class ForgotPassword extends AppCompatActivity {

    EditText emailAddress;
    String email;
    PinView otpCode;
    ConstraintLayout layout;
    Button verifyEmailButton, verifyOTPCode;
    FirebaseFirestore database;
    JavaMailAPI javaMailAPI;
    Constants constants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        Intent intent = getIntent();
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        verifyEmailButton = (Button) findViewById(R.id.verifyEmailButton);
        verifyOTPCode = (Button) findViewById(R.id.verifyOTPButton);
        otpCode = (PinView) findViewById(R.id.otpCode);
        layout = (ConstraintLayout) findViewById(R.id.constraintLayout2);
        database = FirebaseFirestore.getInstance();
        constants = new Constants();
    }

    public void setListeners() {
        verifyEmailButton.setOnClickListener(view -> checkEmailValidity());
        verifyOTPCode.setOnClickListener(view -> checkOTPValidity());
    }

    public void checkOTPValidity() {
        if(otpCode.getText().length() <6) {
            otpCode.setError("OTP code you entered is invalid!");
        } else {
            String code = javaMailAPI.getOtpCode();
            if(otpCode.getText().toString().equals(code)) {
                changeIntent();
            } else {
                showAlertDialog("OTP code you entered doesn't match with sent one!");
            }
        }
    }

    public void checkEmailValidity() {
        email = emailAddress.getText().toString();
        if(email.length() == 0) {
            emailAddress.setError("This field cannot be left blank");
            return;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddress.setError("Email address is invalid");
        } else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Query query = database.collection("students")
                    .whereEqualTo(Constants.EMAIL_ADDRESS, email)
                    .whereEqualTo(Constants.REGISTRATION_ACCEPTED, "true");
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().size()!=0) {
                        String gtp = new DecimalFormat("000000").format(new Random().nextInt(999999));
                        javaMailAPI = new JavaMailAPI(ForgotPassword.this, email, constants.subject, constants.message, gtp);
                        javaMailAPI.execute();
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        showAlertDialog("Email address is not registered in the system");
                    }
                } else{/*Ignore*/}
            });
        }
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(ForgotPassword.this);
        regDialog.setMessage(message);
        regDialog.setTitle("Alert");
        regDialog.setCancelable(false);
        regDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int nom) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = regDialog.create();
        dialog.show();
    }

    public void changeIntent() {
        Intent mainIntent = new Intent(this, PasswordRecovery.class);
        mainIntent.putExtra("email", email);
        startActivity(mainIntent);
    }
}