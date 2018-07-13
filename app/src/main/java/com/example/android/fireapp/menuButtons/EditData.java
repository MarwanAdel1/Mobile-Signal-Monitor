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

public class EditData extends AppCompatActivity {


    private EditText first, last, mail, phone, pass;
    private Button update;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private DatabaseReference allData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        allData = firebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final String Email = user.getEmail();

        first = (EditText) findViewById(R.id.Edit_first);

        last = (EditText) findViewById(R.id.Edit_last);
       // mail = (EditText) findViewById(R.id.Edit_Mail);
        phone = (EditText) findViewById(R.id.Edit_phone);
        pass = (EditText) findViewById(R.id.Edit_password);
        update = (Button) findViewById(R.id.update);

        //  AuthCredential credential = EmailAuthProvider.getCredential(Email, pass.getText().toString());
        //  Log.i(EditeData.class.getSimpleName(), "edit555 "+credential);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential = EmailAuthProvider.getCredential(Email, pass.getText().toString());
                Log.i(EditData.class.getSimpleName(), "edit555 " + credential);
                Log.i(EditData.class.getSimpleName(), "edit666 " + Email);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        allData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    for (DataSnapshot rootChild : dataSnapshot.getChildren()) {
                                        if (rootChild.getKey().equalsIgnoreCase("UsersData")) {
                                            for (DataSnapshot child : rootChild.getChildren()) {
                                                if (child.child("Email").getValue().toString().equals(Email)) {
                                                    String parentKey = child.getKey();
                                                    Log.i(EditData.class.getSimpleName(), "edit5");

                                                    //  Log.i(EditeData.class.getSimpleName(), "edit55 "+);
                                                    allData.child("UsersData").child("" + parentKey).child("Name").setValue(first.getText().toString() + " " + last.getText().toString());
                                                    Log.i(EditData.class.getSimpleName(), "edit55 ");

                                                    allData.child("UsersData").child("" + parentKey).child("Phone").setValue(phone.getText().toString());
                                                }
                                            }
                                        }
                                    }
                                }
                                //   Log.i(EditeData.class.getSimpleName(), "edit6 "+parentKey );
                                Log.i(EditData.class.getSimpleName(), "edit6");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Log.i(EditData.class.getSimpleName(), "edit7");
                    }

                });
                Log.i(EditData.class.getSimpleName(), "edit9");
            }

        });

        Log.i(EditData.class.getSimpleName(), "edit10");
    }
}
