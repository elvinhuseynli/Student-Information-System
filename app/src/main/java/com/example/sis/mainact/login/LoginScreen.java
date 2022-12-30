package com.example.sis.mainact.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sis.R;
import com.example.sis.admin.AdminMainActivity;
import com.example.sis.mainact.registration.RegistrationScreenFirstStep;
import com.example.sis.mainact.registration.RegistrationScreenSecondStep;
import com.example.sis.student.StudentMainActivity;
import com.example.sis.teacher.TeacherMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LoginScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText username, password;
    TextView forgotPassword;
    SwitchCompat switchButton;
    Button signInButton, signUpButton;
    Spinner userType;
    String[] usersList;
    ProgressBar progressBar;
    ArrayAdapter adapter;
    boolean rememberMe=true, errorExists=false;
    RemoteDatabase remoteDatabase;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeListeners();
    }

    public void initializeViews() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        switchButton = (SwitchCompat) findViewById(R.id.switchButton);
        signInButton = (Button) findViewById(R.id.signInButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        userType = (Spinner) findViewById(R.id.userType);
        userType.setOnItemSelectedListener(this);
        usersList = getResources().getStringArray(R.array.userType);
        adapter = new ArrayAdapter(this, R.layout.spinner, usersList);
        adapter.setDropDownViewResource(R.layout.spinner);
        userType.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        remoteDatabase = new RemoteDatabase(this);
        checkRemoteDatabase();
    }

    public void checkRemoteDatabase() {
        if(remoteDatabase.checkDatabase()) {
            Cursor result = remoteDatabase.getData();
            if(result.getCount()>0) {
                result.moveToNext();
                rememberMe = Boolean.parseBoolean(result.getString(3));
                if(rememberMe) {
                    username.setText(result.getString(0));
                    password.setText(result.getString(1));
                    userType.setSelection(adapter.getPosition(result.getString(2)));
                }
            }
        }
    }

    public void initializeListeners() {
        switchButton.setOnCheckedChangeListener((compoundButton, isChecked) -> rememberMe = isChecked);

        signUpButton.setOnClickListener(view -> switchToSignUpScreen());

        signInButton.setOnClickListener(view -> checkValidity());

        forgotPassword.setOnClickListener(view -> {openOTPScreen();});
    }

    public void checkValidity() {
        signInButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        errorExists = false;

        if(username.getText().length()==0) {
            username.setError("This field cannot be left blank");
            errorExists = true;
        }

        if(password.getText().toString().length() < 8 || password.getText().toString().length() >20) {
            password.setError("Password length should be between 8 and 20!");
            errorExists = true;
        } else if(!isValid(password.getText().toString())) {
            password.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter!");
            errorExists = true;
        }

        if(!errorExists)
            checkDatabase();
        else {
            signInButton.setText("SIGN IN");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void checkDatabase() {
        String userTypeS = userType.getSelectedItem().toString();
        String idNumber = username.getText().toString();
        String pass = password.getText().toString();
        switch (userTypeS) {
            case "Student":
                Query query = firebaseFirestore.collection("students")
                        .whereEqualTo("studentId", idNumber)
                        .whereEqualTo("password", pass)
                        .whereEqualTo("registrationAccepted", "true");
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() != 0) {
                            String target = "Student";
                            String signedIn = Boolean.toString(rememberMe);
                            remoteDatabase.deleteData();
                            remoteDatabase.insertData(idNumber, pass, target, signedIn);
                            changeActivity(idNumber, target);
                        } else {
                            setAlertDialog("Either identification number or password is invalid");
                            signInButton.setText("SIGN IN");
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                break;
            case "Teacher":
                Query query1 = firebaseFirestore.collection("teachers")
                        .whereEqualTo("teacherId", idNumber)
                        .whereEqualTo("password", pass);
                query1.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() != 0) {
                            String target = "Teacher";
                            String signedIn = Boolean.toString(rememberMe);
                            remoteDatabase.deleteData();
                            remoteDatabase.insertData(idNumber, pass, target, signedIn);
                            changeActivity(idNumber, target);
                        } else {
                            setAlertDialog("Either identification number or password is invalid");
                            signInButton.setText("SIGN IN");
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                break;
            case "Admin":
                Query query2 = firebaseFirestore.collection("admins")
                        .whereEqualTo("adminId", idNumber)
                        .whereEqualTo("password", pass);
                query2.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() != 0) {
                            String target = "Admin";
                            String signedIn = Boolean.toString(rememberMe);
                            remoteDatabase.deleteData();
                            remoteDatabase.insertData(idNumber, pass, target, signedIn);
                            changeActivity(idNumber, target);
                        } else {
                            setAlertDialog("Either identification number or password is invalid");
                            signInButton.setText("SIGN IN");
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                break;
        }
    }

    public void setAlertDialog(String message) {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(this);
        regDialog.setMessage(message);
        regDialog.setTitle("Error");
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

    public void changeActivity(String idNumber, String position) {
        Intent mainIntent;
        if(position.equals("Student")) {
            mainIntent = new Intent(this, StudentMainActivity.class);
        } else if(position.equals("Teacher")) {
            mainIntent = new Intent(this, TeacherMainActivity.class);
        } else {
            mainIntent = new Intent(this, AdminMainActivity.class);
        }

        mainIntent.putExtra("idNumber", idNumber);
        startActivity(mainIntent);

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

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back button again in order to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }


    public void switchToSignUpScreen() {
        Intent registerIntent = new Intent(this, RegistrationScreenFirstStep.class);
        startActivity(registerIntent);
    }

    public void openOTPScreen() {
        Intent mainIntent = new Intent(this, ForgotPassword.class);
        startActivity(mainIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}