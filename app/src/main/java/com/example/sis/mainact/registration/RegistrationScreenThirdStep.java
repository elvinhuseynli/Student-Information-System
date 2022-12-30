package com.example.sis.mainact.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chaos.view.PinView;
import com.example.sis.R;
import com.example.sis.mainact.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Random;

public class RegistrationScreenThirdStep extends AppCompatActivity {

    EditText emailAddress;
    String email;
    PinView otpCode;
    ConstraintLayout layout;
    Button verifyEmailButton, verifyOTPCode;
    FirebaseFirestore database;
    JavaMailAPI javaMailAPI;
    Constants constants;
    RegistrationDataStore registrationDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen_third_step);

        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        Intent intent = getIntent();
        registrationDataStore = (RegistrationDataStore) intent.getSerializableExtra("secondStep");
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
                setRegistrationData();
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
            Query query = database.collection("students").whereEqualTo("emailAddress", email);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().size()==0) {
                            String gtp = new DecimalFormat("000000").format(new Random().nextInt(999999));
                            javaMailAPI = new JavaMailAPI(RegistrationScreenThirdStep.this, email, constants.subject, constants.message, gtp);
                            javaMailAPI.execute();
                            layout.setVisibility(View.VISIBLE);
                        } else {
                            showAlertDialog("Email address is already registered in the system");
                        }
                    } else{/*Ignore*/}
                }
            });
        }
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(RegistrationScreenThirdStep.this);
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

    public void setRegistrationData() {
        registrationDataStore.setEmailAddress(email);
    }

    public void changeIntent() {
        Intent mainIntent = new Intent(this, RegistrationScreenFourthStep.class);
        mainIntent.putExtra("thirdStep", registrationDataStore);
        startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
    }
}