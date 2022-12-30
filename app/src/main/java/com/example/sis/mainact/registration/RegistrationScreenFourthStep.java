package com.example.sis.mainact.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chaos.view.PinView;
import com.example.sis.R;
import com.example.sis.mainact.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegistrationScreenFourthStep extends AppCompatActivity {

    CountryCodePicker codePicker;
    Button verifyPhoneButton, verifyOTPButton;
    PinView otpCode;
    EditText phoneNumber;
    Date date;
    ProgressDialog progressDialog;
    ConstraintLayout layout;
    String phoneNumberComplete, otpResult, mVerificationId;
    FirebaseAuth phoneAuthentication;
    FirebaseFirestore database;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    RegistrationDataStore registrationDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen_fourth_step);

        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        Intent mainIntent = getIntent();
        registrationDataStore = (RegistrationDataStore) mainIntent.getSerializableExtra("thirdStep");
        codePicker = (CountryCodePicker) findViewById(R.id.countryCode);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        verifyOTPButton = (Button) findViewById(R.id.verifyOTPButton);
        verifyPhoneButton = (Button) findViewById(R.id.verifyPhoneButton);
        otpCode = (PinView) findViewById(R.id.otpCode);
        layout = (ConstraintLayout) findViewById(R.id.constraintLayout2);

        database = FirebaseFirestore.getInstance();
        phoneAuthentication = FirebaseAuth.getInstance();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                otpResult = credential.getSmsCode();
                if(otpResult != null) {
                    otpCode.setText(otpResult);
                    verifyCode(otpResult);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {}

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
                progressDialog.dismiss();
                layout.setVisibility(View.VISIBLE);
            }
        };

    }

    public void setListeners() {
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1 && editable.toString().startsWith("0")) {
                    editable.clear();
                }
            }
        });
        verifyPhoneButton.setOnClickListener(view -> checkPhoneValidity());
        verifyOTPButton.setOnClickListener(view -> checkOTPValidity());
    }

    public void checkPhoneValidity() {
        phoneNumberComplete = (new StringBuilder().append(codePicker.getSelectedCountryCodeWithPlus()).append(phoneNumber.getText().toString()).toString());
        Query query = database.collection("students").whereEqualTo("phoneNumber", phoneNumberComplete);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size() == 0) {
                    sendOTPCode();
                } else {
                    showAlertDialog("Phone number is already registered in the system");
                }
            } else {/*Ignore this case*/}
        });
    }

    public void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        phoneAuthentication.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addRegistrationData();
                    addDataToDatabase();
                    finish();
                } else{
                    showAlertDialog("Failed to verify OTP code!");
                }
            }
        });
    }

    public void addRegistrationData() {
        registrationDataStore.setPhoneNumber(phoneNumberComplete);
        registrationDataStore.setStudentId(Integer.toString(Constants.autoIncrementation));
    }

    public void addDataToDatabase() {
        HashMap<String, Object> data = new HashMap<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        String parentDir = "images/";
        String childDir = registrationDataStore.getStudentId() +".png";
        StorageReference storageReference = reference.child(parentDir+childDir);

        date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String dateObject = sdf.format(date);

        data.put(Constants.STUDENT_ID, registrationDataStore.getStudentId());
        data.put(Constants.FIRST_NAME, registrationDataStore.getFirstName());
        data.put(Constants.LAST_NAME, registrationDataStore.getLastName());
        data.put(Constants.BIRTH_DATE, registrationDataStore.getBirthDate());
        data.put(Constants.NATIONALITY, registrationDataStore.getNationality());
        data.put(Constants.PASSWORD, registrationDataStore.getPassword());
        data.put(Constants.COUNTRY, registrationDataStore.getCountry());
        data.put(Constants.CITY, registrationDataStore.getCity());
        data.put(Constants.COUNTRY_OF_BIRTH, registrationDataStore.getCountryOfBirth());
        data.put(Constants.PLACE_OF_BIRTH, registrationDataStore.getPlaceOfBirth());
        data.put(Constants.CONTACT_ADDRESS, registrationDataStore.getContactAddress());
        data.put(Constants.GENDER_SELECTION, registrationDataStore.getGenderSelection());
        data.put(Constants.DEPARTMENT, registrationDataStore.getDepartment());
        data.put(Constants.REGISTRATION_ACCEPTED, "false");
        data.put(Constants.TIMESTAMP, date);
        data.put(Constants.REGISTRATION_DATE, dateObject);
        data.put(Constants.EMAIL_ADDRESS, registrationDataStore.getEmailAddress());
        data.put(Constants.PHONE_NUMBER, registrationDataStore.getPhoneNumber());

        UploadTask uploadTask = storageReference.putBytes(registrationDataStore.getByteArray());
        uploadTask.addOnFailureListener(e -> {
            showAlertDialog("Unable to upload image");
            return;
        }).addOnSuccessListener(taskSnapshot -> {
            Uri downloadUri = taskSnapshot.getUploadSessionUri();
        });

        database = FirebaseFirestore.getInstance();
        database.collection("students")
                .document(registrationDataStore.getStudentId())
                .set(data).addOnSuccessListener(unused -> {
                    setIncrementationData();
                });
    }

    public void setIncrementationData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("studentId", registrationDataStore.getStudentId());
        database = FirebaseFirestore.getInstance();
        database.collection("constants")
                .document("autoIncrementStudent")
                .set(data).addOnSuccessListener(unused -> {
                    Intent mainIntent = new Intent(RegistrationScreenFourthStep.this, RegistrationScreenFinalStep.class);
                    startActivity(mainIntent);
                });
    }

    public void checkOTPValidity() {
        String enteredCode = otpCode.getText().toString();
        System.out.println(enteredCode);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,enteredCode);
        signInWithCredential(credential);
    }

    public void sendOTPCode() {
        progressDialog = ProgressDialog.show(this,"Sending OTP code", "Please wait...",false,false);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(phoneAuthentication)
                        .setPhoneNumber(phoneNumberComplete).setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this).setCallbacks(callbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(this);
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

    @Override
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
    }
}