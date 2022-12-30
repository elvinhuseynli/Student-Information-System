package com.example.sis.mainact.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sis.R;
import com.example.sis.mainact.Constants;
import com.example.sis.mainact.login.LoginScreen;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.Arrays;
import java.util.Calendar;

public class RegistrationScreenFirstStep extends AppCompatActivity {

    EditText firstName, lastName, birthDate, password, rePassword;
    TextView signIn;
    Button signUp;
    ImageView datePicker, visibilityOn1, visibilityOn2, visibilityOff1, visibilityOff2;
    String[] listOfCountries;
    boolean errorExists = false;
    RegistrationDataStore registrationDataStore;
    AutoCompleteTextView nationality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen_first_step);

        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        Constants.autoIncrementData();
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        birthDate = (EditText) findViewById(R.id.birthDate);
        password = (EditText) findViewById(R.id.password);
        rePassword = (EditText) findViewById(R.id.rePassword);
        signIn = (TextView) findViewById(R.id.signInButton);
        signUp = (Button) findViewById(R.id.continueButton);
        visibilityOn1 = (ImageView) findViewById(R.id.visibilityOn1);
        visibilityOn2 = (ImageView) findViewById(R.id.visibilityOn2);
        visibilityOff1 = (ImageView) findViewById(R.id.visibilityOff1);
        visibilityOff2 = (ImageView) findViewById(R.id.visibilityOff2);
        nationality = (AutoCompleteTextView) findViewById(R.id.nationality);
        datePicker = (ImageView) findViewById(R.id.datePicker);
        registrationDataStore = new RegistrationDataStore();
        listOfCountries = getResources().getStringArray(R.array.countries_list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfCountries);
        nationality.setAdapter(adapter);
        new DateInputMask(birthDate);
    }

    public void setListeners() {
        datePicker.setOnClickListener(view -> setDate(birthDate));
        visibilityOn1.setOnClickListener(view -> setVisibility(0, false));
        visibilityOff1.setOnClickListener(view -> setVisibility(0, true));
        visibilityOn2.setOnClickListener(view -> setVisibility(1, false));
        visibilityOff2.setOnClickListener(view -> setVisibility(1, true));
        signIn.setOnClickListener(view -> changeIntent());
        signIn.setOnClickListener(view -> changeIntent());
        signUp.setOnClickListener(view -> checkValidity());
    }

    public void setDate(EditText dateOfBirth) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dayM = String.format("%02d", dayOfMonth);
                    String monthM = String.format("%02d", monthOfYear+1);
                    String currentDate = dayM + "/" + monthM + "/" + year1;
                    dateOfBirth.setText(currentDate);
                },year, month, day);

        datePickerDialog.show();
    }

    public void setVisibility(int index, boolean makeVisible) {
        if(makeVisible) {
            if(index == 0) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setSelection(password.length());
                visibilityOn1.setVisibility(View.VISIBLE);
                visibilityOff1.setVisibility(View.INVISIBLE);
            }
            else {
                rePassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                rePassword.setSelection(rePassword.length());
                visibilityOn2.setVisibility(View.VISIBLE);
                visibilityOff2.setVisibility(View.INVISIBLE);
            }
        } else {
            if(index == 0) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setSelection(password.length());
                visibilityOn1.setVisibility(View.INVISIBLE);
                visibilityOff1.setVisibility(View.VISIBLE);
            }
            else {
                rePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                rePassword.setSelection(rePassword.length());
                visibilityOn2.setVisibility(View.INVISIBLE);
                visibilityOff2.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean checkFieldsValidation() {
        errorExists = false;

        if(firstName.getText().length() == 0){
            firstName.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!firstName.getText().toString().matches(getString(R.string.name))) {
            firstName.setError("First name should contain only letters!");
            errorExists = true;
        }

        if(lastName.getText().length() == 0){
            lastName.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!lastName.getText().toString().matches(getString(R.string.name))) {
            lastName.setError("Last name should contain only letters!");
            errorExists = true;
        }

        if(nationality.getText().length() == 0) {
            nationality.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!ArrayUtils.contains(listOfCountries, nationality.getText().toString())) {
            nationality.setError("Please select a correct nationality!");
            errorExists = true;
        }

        if(!checkDate(birthDate)) {
            birthDate.setError("Birth date is not valid!");
            errorExists = true;
        }

        if(password.getText().toString().length() < 8 || password.getText().toString().length() >20) {
            password.setError("Password length should be between 8 and 20!");
            errorExists = true;
        } else if(!isValid(password.getText().toString())) {
            password.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter!");
            errorExists = true;
        }

        if(rePassword.getText().toString().length() < 8 || rePassword.getText().toString().length() >20) {
            rePassword.setError("Password length should be between 8 and 20!");
            errorExists = true;
        } else if(!isValid(rePassword.getText().toString())) {
            rePassword.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter!");
            errorExists = true;
        }

        return errorExists;
    }

    public boolean checkDate(EditText birthDate) {
        int counter = 0;
        for(int i=0; i< birthDate.getText().length(); i++)
            if(birthDate.getText().charAt(i) >= 48 && birthDate.getText().charAt(i) <= 57)
                counter++;

        return counter == 8;
    }

    public void checkValidity() {
        if(!checkFieldsValidation()) {
            if(!password.getText().toString().equals(rePassword.getText().toString())) {
                setAlertDialog();
            } else {
                setRegistrationData();
                nextStep();
            }
        }
    }

    public void setRegistrationData() {
        registrationDataStore.setFirstName(firstName.getText().toString());
        registrationDataStore.setLastName(lastName.getText().toString());
        registrationDataStore.setBirthDate(birthDate.getText().toString());
        registrationDataStore.setNationality(nationality.getText().toString());
        registrationDataStore.setPassword(password.getText().toString());
    }

    public void setAlertDialog() {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(this);
        regDialog.setMessage("Please make sure your passwords match!");
        regDialog.setTitle("Error");
        regDialog.setCancelable(false);
        regDialog.setPositiveButton("OK",
                (dialog, nom) -> dialog.cancel());
        AlertDialog dialog = regDialog.create();
        dialog.show();
    }

    public void nextStep() {
        Intent mainIntent = new Intent(this, RegistrationScreenSecondStep.class);

        mainIntent.putExtra("firstStep", registrationDataStore);
        startActivity(mainIntent);
    }

    public void changeIntent() {
        Intent mainIntent = new Intent(this, LoginScreen.class);
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

    @Override
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
    }
}