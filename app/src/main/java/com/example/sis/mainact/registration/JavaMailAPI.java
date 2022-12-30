package com.example.sis.mainact.registration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void,Void,Void>  {

    Context contextOfMail;
    Session session;
    String emailAddress, subjectOfMail, message, otpCode;
    ProgressDialog progressDialog;

    public JavaMailAPI(Context contextOfMail, String emailAddress, String subjectOfMail, String message, String otpCode) {
        this.contextOfMail = contextOfMail;
        this.emailAddress = emailAddress;
        this.subjectOfMail = subjectOfMail;
        this.message = message;
        this.otpCode = otpCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!otpCode.equals("-1"))
            progressDialog = ProgressDialog.show(contextOfMail,"Sending email", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(!otpCode.equals("-1")) {
            progressDialog.dismiss();
            AlertDialog.Builder phoneDialog = new AlertDialog.Builder(contextOfMail);
            phoneDialog.setMessage("The email has been sent. Check your email please!");
            phoneDialog.setTitle("Success");
            phoneDialog.setCancelable(false);
            phoneDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int nom) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = phoneDialog.create();
            dialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS


        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("noreply.stis@gmail.com", "ybgaskpymbaeaujx");
                    }
                });

        try {
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress("noreply.stis@gmail.com"));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
            mm.setSubject(subjectOfMail);
            if(otpCode.equals("-1")) {
                mm.setText(message);
            } else{
            mm.setText(message+otpCode); }
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOtpCode() {
        return otpCode;
    }
}