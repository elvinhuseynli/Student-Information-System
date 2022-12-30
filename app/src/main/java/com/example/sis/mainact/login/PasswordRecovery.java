package com.example.sis.mainact.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PasswordRecovery extends AppCompatActivity {

    EditText password, rePassword;
    Button confirmButton;
    ProgressBar progressBar;
    boolean errorExists;
    String emailAddress;
    ImageView visibilityOn1, visibilityOn2, visibilityOff1, visibilityOff2;
    FirebaseFirestore database;
    RemoteDatabase remoteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        Intent mainIntent = getIntent();
        emailAddress = mainIntent.getStringExtra("email");

        password = (EditText) findViewById(R.id.password);
        rePassword = (EditText) findViewById(R.id.rePassword);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        visibilityOn1 = (ImageView) findViewById(R.id.visibilityOn1);
        visibilityOn2 = (ImageView) findViewById(R.id.visibilityOn2);
        visibilityOff1 = (ImageView) findViewById(R.id.visibilityOff1);
        visibilityOff2 = (ImageView) findViewById(R.id.visibilityOff2);

        database = FirebaseFirestore.getInstance();
        remoteDatabase = new RemoteDatabase(this);
    }

    public void setListeners() {
        confirmButton.setOnClickListener(view -> checkValidity());
        visibilityOn1.setOnClickListener(view -> setVisibility(0, false));
        visibilityOff1.setOnClickListener(view -> setVisibility(0, true));
        visibilityOn2.setOnClickListener(view -> setVisibility(1, false));
        visibilityOff2.setOnClickListener(view -> setVisibility(1, true));
    }

    public void checkValidity() {
        errorExists = false;
        confirmButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        if (password.getText().toString().length() < 8 || password.getText().toString().length() > 20) {
            password.setError("Password length should be between 8 and 20!");
            errorExists = true;
        } else if (!isValid(password.getText().toString())) {
            password.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter!");
            errorExists = true;
        }

        if (rePassword.getText().toString().length() < 8 || rePassword.getText().toString().length() > 20) {
            rePassword.setError("Password length should be between 8 and 20!");
            errorExists = true;
        } else if (!isValid(rePassword.getText().toString())) {
            rePassword.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter!");
            errorExists = true;
        }

        if(!errorExists) {
            if(password.getText().toString().equals(rePassword.getText().toString())) {
                checkDatabase();
            } else {
                confirmButton.setText("Recover");
                progressBar.setVisibility(View.INVISIBLE);
                setAlertDialog("Your passwords don't match!");
            }
        } else {
            confirmButton.setText("Recover");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void checkDatabase() {
        Query query = database.collection("students")
                .whereEqualTo("emailAddress", emailAddress);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() != 0) {
                    DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                    String id = snapshot.getId();
                    String pass = snapshot.getString("password");
                    if (!password.getText().toString().equals(pass)) {
                        database.collection("students")
                                .document(id).update("password", password.getText().toString())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(PasswordRecovery.this, "Your password has been updated", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    confirmButton.setText("Recover");
                                    changeIntent();
                                });
                    } else {
                        setAlertDialog("Your new password cannot be same as old password!");
                        progressBar.setVisibility(View.INVISIBLE);
                        confirmButton.setText("Recover");
                    }
                } else {
                    setAlertDialog("Email address is not registered in the system!");
                    progressBar.setVisibility(View.INVISIBLE);
                    confirmButton.setText("Recover");
                }
            }
        });
    }

    public void setAlertDialog(String message) {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(this);
        regDialog.setMessage(message);
        regDialog.setTitle("Error");
        regDialog.setCancelable(false);
        regDialog.setPositiveButton("OK",
                (dialog, nom) -> dialog.cancel());
        AlertDialog dialog = regDialog.create();
        dialog.show();
    }

    public static boolean isValid(String password) {
        boolean digit = false;
        boolean lcase = false;
        boolean ucase = false;

        for (char ch : password.toCharArray()) {
            digit = digit || Character.isDigit(ch);
            ucase = ucase || Character.isUpperCase(ch);
            lcase = lcase || Character.isLowerCase(ch);
        }
        return digit && lcase && ucase;
    }

    public void setVisibility(int index, boolean makeVisible) {
        if (makeVisible) {
            if (index == 0) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setSelection(password.length());
                visibilityOn1.setVisibility(View.VISIBLE);
                visibilityOff1.setVisibility(View.INVISIBLE);
            } else {
                rePassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                rePassword.setSelection(rePassword.length());
                visibilityOn2.setVisibility(View.VISIBLE);
                visibilityOff2.setVisibility(View.INVISIBLE);
            }
        } else {
            if (index == 0) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setSelection(password.length());
                visibilityOn1.setVisibility(View.INVISIBLE);
                visibilityOff1.setVisibility(View.VISIBLE);
            } else {
                rePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                rePassword.setSelection(rePassword.length());
                visibilityOn2.setVisibility(View.INVISIBLE);
                visibilityOff2.setVisibility(View.VISIBLE);
            }
        }
    }

    public void changeIntent() {
        Intent mainIntent = new Intent(this, LoginScreen.class);
        remoteDatabase.deleteData();
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