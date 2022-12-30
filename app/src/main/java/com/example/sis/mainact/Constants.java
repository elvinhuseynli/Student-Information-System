package com.example.sis.mainact;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Constants {
    public static String FIRST_NAME="firstName", LAST_NAME="lastName", BIRTH_DATE="birthDate", NATIONALITY="nationality";
    public static String PASSWORD="password", COUNTRY="country", CITY="city", COUNTRY_OF_BIRTH="countryOfBirth";
    public static String PLACE_OF_BIRTH="placeOfBirth", CONTACT_ADDRESS="contactAddress", GENDER_SELECTION="genderSelection";
    public static String DEPARTMENT="department", EMAIL_ADDRESS="emailAddress", PHONE_NUMBER="phoneNumber";
    public static String STUDENT_ID="studentId", REGISTRATION_ACCEPTED="registrationAccepted";
    public static String REGISTRATION_DATE="registrationDate", TIMESTAMP="timestamp";
    public static String GS_REFERENCE="images/";

    public static int autoIncrementation;

    public static String message = "Hi. Hope you are having a good day! \nBelow code is your verification code.\nDon't share this password with anyone else.\nIf you are not the one requesting OTP code, you can ignore this email.\n\n", subject = "Verification Message";
    public static String message_accept = "Hi. Hope you are having a good day! \nWe are pleased to inform you that your request has been validated. \nNow you can login your account! \nHave a nice day\n",subject_msg="Request Status";
    public static String message_reject = "Hi. Hope you are having a good day! \nWe are sorry to inform you that your request has been rejected. \nTry sending a new request with new data! \nHave a nice day\n";

    public static void autoIncrementData() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Query query = database.collection("constants")
                .whereEqualTo(FieldPath.documentId(),"autoIncrementStudent");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().size()!=0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        autoIncrementation = Integer.parseInt(snapshot.getString("studentId"));
                        autoIncrementation++;
                    }
                } else {/*Ignore case*/}
            }
        });
    }
}
