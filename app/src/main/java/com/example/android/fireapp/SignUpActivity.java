package com.example.android.fireapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.android.fireapp.R.id.signUp;

public class SignUpActivity extends AppCompatActivity {


    private Button signConfirm;
    private EditText First, Last, Email, Phone, Pass, Pass2;
    private TextView Error2, Error3, Error4, Error5, Error6;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference UsersData;
    private DatabaseReference databaseReference;


    int flag;
    private FirebaseAuth mAuth;
    private int dCount = 0;

    ConnectivityManager connect;
    NetworkInfo networkInfo;
    ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
        UsersData = firebaseDatabase.getReference("UsersData");


        signConfirm = (Button) findViewById(signUp);
        First = (EditText) findViewById(R.id.first);
        Last = (EditText) findViewById(R.id.last);
        Email = (EditText) findViewById(R.id.NewMail);
        Phone = (EditText) findViewById(R.id.phone);
        Pass = (EditText) findViewById(R.id.password);
        Pass2 = (EditText) findViewById(R.id.password2);
        Error2 = (TextView) findViewById(R.id.error2);
        Error3 = (TextView) findViewById(R.id.error3);
        Error4 = (TextView) findViewById(R.id.error4);
        Error5 = (TextView) findViewById(R.id.error5);
        Error6 = (TextView) findViewById(R.id.error6);


        signConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Error2.setText("");
                Error3.setText("");
                Error4.setText("");
                Error5.setText("");
                Error6.setText("");
                flag = 1;

                if (TextUtils.isEmpty(First.getText())) {
                    Error2.setText("can't be empty");    //if any data is empty
                    flag = 0;
                }
                if (TextUtils.isEmpty(Last.getText())) {
                    Error2.setText("can't be empty");
                    flag = 0;
                }
                if (TextUtils.isEmpty(Email.getText())) {
                    Error3.setText("can't be empty");
                    flag = 0;
                }
                if (!TextUtils.isEmpty(Phone.getText())&&Phone.getText().length()!=11) {
                    Error4.setText("wrong phone number ");
                    flag = 0;
                }
                if (TextUtils.isEmpty(Phone.getText())) {
                    Error4.setText("can't be empty");
                    flag = 0;
                }

                if (!TextUtils.isEmpty(Pass.getText())) {
                    if (Pass.getText().toString().length() < 6) {
                        Error5.setText("use at least 7 characters");
                        flag = 0;
                    } else {
                        if ((!(Pass2.getText().toString().equals(Pass.getText().toString()))) && !TextUtils.isEmpty(Pass2.getText().toString())) {  //confirming password
                            Error6.setText("wrong confirmation");
                            Error6.setTextSize(15);
                            flag = 0;
                        }
                    }
                }else{
                    Error5.setText("can't be empty");
                    flag = 0;
                }
                if (TextUtils.isEmpty(Pass2.getText())) {
                    Error6.setText("can't be empty");
                    flag = 0;
                }

                if (flag == 1) {   //nothing wrong
                    if (isConnected()) {
                        signUp();
                    } else {
                        Error6.setText("No Internet Connection");
                        Error6.setTextSize(15);
                    }
                }
            }
        });
    }


    private void signUp() {

        final String email = Email.getText().toString();
        String password = Pass.getText().toString();
        final String FullName = First.getText().toString() + " " + Last.getText().toString();
        final String phone = Phone.getText().toString();
        nDialog = new ProgressDialog(SignUpActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Signing Up");
        nDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                    nDialog.dismiss();
                } else {

                    UsersData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                if (dataSnapshot.child("Number").getValue() == null) {   //first time adding in database
                                    UsersData.child("1").child("Phone").setValue(phone);
                                    UsersData.child("1").child("Email").setValue(email);
                                    UsersData.child("1").child("Name").setValue(FullName);
                                    UsersData.child("Number").setValue(1);

                                    Toast.makeText(SignUpActivity.this, "Signing Up Completed Successfully", Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    finish();
                                } else {
                                    int num = Integer.valueOf(dataSnapshot.child("Number").getValue().toString());
                                    UsersData.child(num + 1 + "").child("Phone").setValue(phone);
                                    UsersData.child(num + 1 + "").child("Email").setValue(email);
                                    UsersData.child(num + 1 + "").child("Name").setValue(FullName);
                                    UsersData.child("Number").setValue(num + 1);

                                    Toast.makeText(SignUpActivity.this, "Signing Up Completed Successfully", Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    finish();
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this, "error", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

        });

    }

    public boolean isConnected() {
        connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connect.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}