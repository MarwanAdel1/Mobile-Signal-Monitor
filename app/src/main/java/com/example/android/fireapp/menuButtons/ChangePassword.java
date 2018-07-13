package com.example.android.fireapp.menuButtons;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.fireapp.LoginActivity;
import com.example.android.fireapp.MainActivity;
import com.example.android.fireapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {

    private EditText oldpass, newpass, confirmnew;
    private Button update;
    String Email;
    //    private FirebaseAuth mAuth;
    //   private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
/*
        user = FirebaseAuth.getInstance().getCurrentUser();
        Email = user.getEmail();

        oldpass = (EditText) findViewById(R.id.oldPassword);
        newpass = (EditText) findViewById(R.id.newPassword);
        confirmnew = (EditText) findViewById(R.id.newPassword2);
        update = (Button) findViewById(R.id.update);

        AuthCredential credential = EmailAuthProvider.getCredential(Email, oldpass.getText().toString());

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(ChangePassword.class.getSimpleName(), "Password updated");
                            } else {
                                Log.d(ChangePassword.class.getSimpleName(), "Error password not updated");
                            }
                        }
                    });
                } else {
                    Log.d(ChangePassword.class.getSimpleName(), "Error auth failed");
                }
            }
        });
  */  }
}
