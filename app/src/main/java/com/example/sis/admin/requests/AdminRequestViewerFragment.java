package com.example.sis.admin.requests;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sis.R;
import com.example.sis.mainact.Constants;
import com.example.sis.mainact.login.ForgotPassword;
import com.example.sis.mainact.registration.JavaMailAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class AdminRequestViewerFragment extends Fragment {

    TextView fullName, department, studentId, genderSelection;
    TextView contactAddress, emailAddress, phoneNumber;
    TextView nationality, birthDate, placeOfBirth, registrationDate;
    Button validateButton, rejectButton;
    ImageView image, backButton;
    JavaMailAPI javaMailAPI;
    String studentIdText;
    ProgressBar progressBar;
    ConstraintLayout layout;
    FirebaseFirestore database;
    FirebaseStorage storage;

    public AdminRequestViewerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_request_viewer, container, false);

        Bundle bundle = this.getArguments();

        studentIdText = bundle.getString("studentId");

        initializeViews(view);
        setListeners();

        retrieveDataFromDatabase();

        return view;
    }

    public void initializeViews(View view) {
        fullName = (TextView) view.findViewById(R.id.fullName);
        department = (TextView) view.findViewById(R.id.department);
        studentId = (TextView) view.findViewById(R.id.studentId);
        nationality = (TextView) view.findViewById(R.id.nationality);
        genderSelection = (TextView) view.findViewById(R.id.gender);
        contactAddress = (TextView) view.findViewById(R.id.contactAddress);
        emailAddress = (TextView) view.findViewById(R.id.emailAddress);
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        birthDate = (TextView) view.findViewById(R.id.birthDate);
        placeOfBirth = (TextView) view.findViewById(R.id.placeOfBirth);
        registrationDate = (TextView) view.findViewById(R.id.registrationDate);
        image = (ImageView) view.findViewById(R.id.image);
        backButton = (ImageView) view.findViewById(R.id.backButton);
        validateButton = (Button) view.findViewById(R.id.validateButton);
        rejectButton = (Button) view.findViewById(R.id.rejectButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        layout = (ConstraintLayout) view.findViewById(R.id.layout);

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void setListeners() {
        validateButton.setOnClickListener(v -> {
            callAlertDialog(0, "Are you sure you want to validate the request for application?");
        });

        rejectButton.setOnClickListener(v -> {
            callAlertDialog(1, "Are you sure you want to reject the request for application?");
        });

        backButton.setOnClickListener(v -> {
            translateToPreviousPage();
        });
    }

    @SuppressLint("SetTextI18n")
    public void retrieveDataFromDatabase() {
        Query query = database.collection("students")
                .whereEqualTo(FieldPath.documentId(), studentIdText);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size()!=0) {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    fullName.setText(doc.getString(Constants.FIRST_NAME)+" "+doc.getString(Constants.LAST_NAME));
                    department.setText(doc.getString(Constants.DEPARTMENT));
                    studentId.setText(doc.getString(Constants.STUDENT_ID));
                    nationality.setText(doc.getString(Constants.NATIONALITY));
                    genderSelection.setText(doc.getString(Constants.GENDER_SELECTION));
                    contactAddress.setText(doc.getString(Constants.CONTACT_ADDRESS)+", "+
                            doc.getString(Constants.CITY)+", "+doc.getString(Constants.COUNTRY));
                    emailAddress.setText(doc.getString(Constants.EMAIL_ADDRESS));
                    phoneNumber.setText(doc.getString(Constants.PHONE_NUMBER));
                    birthDate.setText(doc.getString(Constants.BIRTH_DATE));
                    placeOfBirth.setText(doc.getString(Constants.PLACE_OF_BIRTH)+", "+
                            doc.getString(Constants.COUNTRY_OF_BIRTH));
                    registrationDate.setText(doc.getString(Constants.REGISTRATION_DATE));
                    loadImage();
                } else {Toast.makeText(getActivity(), "Unable to load data", Toast.LENGTH_LONG).show();}
            }
        });
    }

    public void loadImage() {
        StorageReference storageReference = storage.getReference();
        StorageReference gsReference = storageReference.child(Constants.GS_REFERENCE+studentIdText+".png");

        long MAX_BYTES=1024*1024;
        gsReference.getBytes(MAX_BYTES).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image.setImageBitmap(bitmap);
            progressBar.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Unable to load image", Toast.LENGTH_LONG).show();
        });
    }

    public void callAlertDialog(int validation, String textMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(textMessage);
        builder.setTitle("Warning");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                (dialog, nom) -> {
                    if (validation == 0) {
                        updateData();
                        String otp = "-1";
                        javaMailAPI = new JavaMailAPI(getActivity(), emailAddress.getText().toString(), Constants.subject_msg, Constants.message_accept, otp);
                        javaMailAPI.execute();
                        Toast.makeText(getActivity(), "Registration validated!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        deleteData();
                        String otp = "-1";
                        javaMailAPI = new JavaMailAPI(getActivity(), emailAddress.getText().toString(), Constants.subject_msg, Constants.message_reject, otp);
                        javaMailAPI.execute();
                        Toast.makeText(getActivity(), "Registration rejected!", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("No",
                (dialog, nom) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateData() {
        database.collection("students")
                .document(studentIdText)
                .update("registrationAccepted", "true")
                .addOnSuccessListener(unused -> translateToPreviousPage());
    }

    public void deleteData() {
        database.collection("students")
                .document(studentIdText)
                .delete().addOnCompleteListener(task -> {
                    translateToPreviousPage();
                    Toast.makeText(getActivity(), "Rejected the request successfully", Toast.LENGTH_LONG).show();
                });
    }

    public void translateToPreviousPage() {
        AdminRequestsFragment fragment = new AdminRequestsFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.adminFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null) return;

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                translateToPreviousPage();
                return true;
            }return false;
        });
    }
}