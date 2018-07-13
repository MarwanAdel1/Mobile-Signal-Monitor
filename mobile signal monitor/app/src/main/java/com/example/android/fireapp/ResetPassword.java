package com.example.android.fireapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private TextView Error;
    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;

    ConnectivityManager connect;
    NetworkInfo networkInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        Error = (TextView) findViewById(R.id.error);

        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Error.setText("");

                String email = inputEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Error.setText("can't be empty");
                } else {
                    if (isConnected()) {
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Error.setText("Done");
                                    Toast.makeText(ResetPassword.this, "Reset password message sent successfully to your Email", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Error.setText("wrong mail");
                                }

                            }
                        });
                    }else {
                        Error.setText("No Internet Connection");
                    }
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
