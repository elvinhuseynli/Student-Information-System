package com.example.sis.mainact.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sis.R;
import com.google.android.gms.common.util.ArrayUtils;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RegistrationScreenSecondStep extends AppCompatActivity {

    EditText placeOfBirth, contactAddress, city;
    Button uploadButton, continueButton;
    AutoCompleteTextView countryOfBirth, genderSelection, country, department;
    String[] listOfCountries, genders, listOfDepartments;
    ProgressBar progressBar;
    TextView imageUpload;
    boolean errorExists = false;
    byte[] byteArray;
    Bitmap bitmap;
    private final int GET_FROM_GALLERY = 3;
    RegistrationDataStore registrationDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen_second_step);

        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        Intent intent = getIntent();
        registrationDataStore = (RegistrationDataStore) intent.getSerializableExtra("firstStep");
        placeOfBirth = (EditText) findViewById(R.id.placeOfBirth);
        contactAddress = (EditText) findViewById(R.id.contactAddress);
        city = (EditText) findViewById(R.id.city);
        countryOfBirth = (AutoCompleteTextView) findViewById(R.id.countryOfBirth);
        genderSelection = (AutoCompleteTextView) findViewById(R.id.genderSelection);
        country = (AutoCompleteTextView) findViewById(R.id.country);
        department = (AutoCompleteTextView) findViewById(R.id.department);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        continueButton = (Button) findViewById(R.id.continueButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageUpload = (TextView) findViewById(R.id.imageUpload);

        listOfCountries = getResources().getStringArray(R.array.countries_list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfCountries);
        countryOfBirth.setAdapter(adapter);
        country.setAdapter(adapter);

        listOfDepartments = getResources().getStringArray(R.array.departments_list);
        ArrayAdapter adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfDepartments);
        department.setAdapter(adapter1);

        genders = getResources().getStringArray(R.array.genders);
        ArrayAdapter adapter2 = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, genders);
        genderSelection.setAdapter(adapter2);
    }

    public void setListeners() {
        uploadButton.setOnClickListener(view -> startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY));
        continueButton.setOnClickListener(view -> checkValidity());
    }

    @Override
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    public void checkValidity() {
        continueButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        if(!checkFieldValidation()) {
            continueButton.setText("Continue");
            progressBar.setVisibility(View.INVISIBLE);
            setRegistrationData();
            nextStep();
        } else {
            continueButton.setText("Continue");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public boolean checkFieldValidation() {
        errorExists = false;

        if(countryOfBirth.getText().length() == 0) {
            countryOfBirth.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(placeOfBirth.getText().length() == 0) {
            placeOfBirth.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(genderSelection.getText().length() == 0) {
            genderSelection.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!ArrayUtils.contains(genders, genderSelection.getText().toString())) {
            genderSelection.setError("Please select a valid gender!");
            errorExists = true;
        }

        if(contactAddress.getText().length() == 0) {
            contactAddress.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(city.getText().length() == 0) {
            city.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(country.getText().length() == 0) {
            country.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!ArrayUtils.contains(listOfCountries, country.getText().toString())) {
            country.setError("Please select a valid country!");
            errorExists = true;
        }

        if(department.getText().length() == 0) {
            department.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!ArrayUtils.contains(listOfDepartments, department.getText().toString())) {
            department.setError("Please select a valid department!");
            errorExists = true;
        }

        if(bitmap == null) {
            imageUpload.setError("You should upload an image to continue");
            errorExists = true;
        } else if(byteArray.length>254229) {
            imageUpload.setError("Image size is exceeding the limit");
            errorExists = true;
        }

        return errorExists;
    }

    public void setRegistrationData() {
        registrationDataStore.setCity(city.getText().toString());
        registrationDataStore.setCountry(country.getText().toString());
        registrationDataStore.setCountryOfBirth(countryOfBirth.getText().toString());
        registrationDataStore.setPlaceOfBirth(placeOfBirth.getText().toString());
        registrationDataStore.setContactAddress(contactAddress.getText().toString());
        registrationDataStore.setDepartment(department.getText().toString());
        registrationDataStore.setGenderSelection(genderSelection.getText().toString());
        registrationDataStore.setByteArray(byteArray);
    }

    public void nextStep() {
        Intent mainIntent = new Intent(this, RegistrationScreenThirdStep.class);
        mainIntent.putExtra("secondStep", registrationDataStore);
        mainIntent.putExtra("bitmap", byteArray);
        startActivity(mainIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } catch (Exception exception) {/*Do nothing*/}
        }
    }
}