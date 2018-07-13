package com.example.android.fireapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.R.attr.theme;
import static android.R.attr.type;
import static com.example.android.fireapp.MainActivity.RequestPermissionCode;
import static com.example.android.fireapp.R.id.phone;

public class LoginActivity extends AppCompatActivity {

    private int sigst, area = 0;
    boolean flag,newArea;

    private String StrPass, StrMail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static LocationManager locationManager;
    public static LocationListener listener1;

    Button login, signUp;
    EditText mail, pass;
    TextView Error1, Reset;
    public static String LogMail,userUid;
    ConnectivityManager connect;
    NetworkInfo networkInfo;

    private DatabaseReference UsersData;
    private DatabaseReference alldataReference;
    private DatabaseReference networkTypeReference;

    private int userDataCount = 1;
    public static String type;

    private ProgressDialog nDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //connecting to the firebase database and declaring references to use it when needed
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
        networkTypeReference = firebaseDatabase.getReference("Database/NetworkTypes");
        alldataReference = firebaseDatabase.getReference("Database");
        UsersData = firebaseDatabase.getReference("UsersData");

        //checking the permissions allowing
        if (checkPermission()) {
            Toast.makeText(LoginActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }

        //determination of number of points saved in database for used sim type
        userDataCount = fetchCount();

        //connection for determination of mobile signal every certain time
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tel.listen(new SignalStrenthListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        //sim card type
        type = tel.getSimOperatorName();

        //checking the first character in the sim type to be stored in right way in database
        if (type.charAt(0) == 'o' || type.charAt(0) == 'O' || type.charAt(0) == 'm' || type.charAt(0) == 'M') {
            type = "Orange";
        }
        if (type.charAt(0) == 'e' || type.charAt(0) == 'E') {
            type = "Etisalat";
        }
        if (type.charAt(0) == 'v' || type.charAt(0) == 'V') {
            type = "Vodafone";
        }

        //location retrieving
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //connecting to the firebase authentication options
        mAuth = FirebaseAuth.getInstance();


        //listener used to know if the user of the app was signed out at the last using of the app on the that mobile phone or not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {    //if kept signed in

                    //getting the email used in signing in before from the authentication data stored
                    LogMail = firebaseAuth.getCurrentUser().getEmail().toString();

                    //getting the id used for this user for storing his data at the first time when signed up
                    userUid = firebaseAuth.getCurrentUser().getUid();

                    if (checkPermission()) {
                        if (isConnected()) {   //checking if there is internet connection

                            //listening for the the certain location every 5 seconds
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener1);

                            //intent for pathing to the activity that has the user data and all the app allowed options
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                          }
                    } else {
                        requestPermission();
                    }
                }  //if signed out at the last using of the app
                else {
                    //if the location listener is still working we stop it
                    if(locationManager != null){
                        locationManager.removeUpdates(listener1);
                    }
                    Toast.makeText(LoginActivity.this, "Login", Toast.LENGTH_LONG).show();
                }
            }
        };


        listener1 = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.i(MainActivity.class.getSimpleName(), "Hey : 2 ");
                sigst = SignalStrenthListener.x;

                if (sigst == -1) {
                    Toast.makeText(LoginActivity.this, "Can't Read Signal Now", Toast.LENGTH_SHORT).show();
                } else {
                    insertToFirebase(location, sigst);
                }
            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        login = (Button) findViewById(R.id.signIn);
        mail = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        signUp = (Button) findViewById(R.id.SignUpLink);
        Error1 = (TextView) findViewById(R.id.error1);
        Reset = (TextView) findViewById(R.id.forgot);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Error1.setText("");
                if (isConnected()) {
                    signIn();
                } else {
                    Error1.setText("No Internet Connection");
                }
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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

    private void signIn() {

        StrMail = mail.getText().toString();
        StrPass = pass.getText().toString();


        if (!TextUtils.isEmpty(StrMail) && !TextUtils.isEmpty(StrPass)) {

            nDialog = new ProgressDialog(LoginActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Get Data");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

            mAuth.signInWithEmailAndPassword(StrMail, StrPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Error1.setText("wrong mail or password");
                        nDialog.dismiss();
                    } else {
                        nDialog.dismiss();

                        if (checkPermission())
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener1);


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    }

                }

            });

        } else {         //if empty
            Error1.setText("wrong mail or password");
        }
    }

    private void insertToFirebase(final Location location, final int strength) {
        Log.i(MainActivity.class.getSimpleName(), "Before Updating 0 : " + strength);
        networkTypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newArea = false;
                int y = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equalsIgnoreCase(type)) {
                        int keyValue = 1;
                        for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                            if (child2.getKey().equalsIgnoreCase(String.valueOf(strength))) {
                                y = 1;
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    area = Integer.parseInt(child3.getKey());
                                    flag = false;
                                    Location first = new Location("First");
                                    for (DataSnapshot child4 : child3.child("Data").getChildren()) {
                                        if (!flag) {
                                            flag = true;
                                            double latitude = child4.child("Latitude").getValue(Double.class);
                                            double longitude = child4.child("Longitude").getValue(Double.class);

                                            first.setLatitude(latitude);
                                            first.setLongitude(longitude);
                                            int range = 0;
                                            switch (strength) {
                                                case 4:
                                                    range = 1000;
                                                    break;
                                                case 3:
                                                    range = 700;
                                                    break;
                                                case 2:
                                                    range = 200;
                                                    break;
                                                case 1:
                                                    range = 50;
                                                    break;
                                                case 0:
                                                    range = 20;
                                                    break;
                                            }

                                            if (location.distanceTo(first) <= range) {
                                                userDataCount = child3.child("Number").getValue(Integer.class);
                                                Log.i(LoginActivity.class.getSimpleName(), "Mero : " + userDataCount);
                                                userDataCount++;
                                                Log.i(LoginActivity.class.getSimpleName(), "Rainnn : " + location.distanceTo(first) + "\n" + strength);
                                                alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Data").child(String.valueOf(userDataCount)).child("Longitude").setValue(location.getLongitude());
                                                alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Data").child(String.valueOf(userDataCount)).child("Latitude").setValue(location.getLatitude());
                                                alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Number").setValue(userDataCount);

                                                newArea = true;
                                            }
                                        }
                                    }
                                    if (newArea)
                                        break;

                                }
                                if (newArea)
                                    break;
                            }
                        }
                        if (newArea)
                            break;
                    }
                }
                if (!newArea)

                {
                    area++;
                    userDataCount = 1;
                    alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Data").child(String.valueOf(userDataCount)).child("Longitude").setValue(location.getLongitude());
                    alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Data").child(String.valueOf(userDataCount)).child("Latitude").setValue(location.getLatitude());
                    alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Number").setValue(userDataCount);
                }
                if (y == 0)

                {
                    area = 1;
                    userDataCount = 1;
                    alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Data").child(String.valueOf(userDataCount)).child("Longitude").setValue(location.getLongitude());
                    alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Data").child(String.valueOf(userDataCount)).child("Latitude").setValue(location.getLatitude());
                    alldataReference.child("NetworkTypes").child(type).child("Signal").child("" + strength).child("" + area).child("Number").setValue(userDataCount);
                    Log.i(LoginActivity.class.getSimpleName(), "Rainnn : 2 :");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED; //&&
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean FineLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean Phone_StatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean Coarse_LocationPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean Network_StatePermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    if (FineLocationPermission && Phone_StatePermission && Coarse_LocationPermission && InternetPermission && Network_StatePermission) {
                        Toast.makeText(LoginActivity.this, "Permissions Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Permissions Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private int fetchCount() {
        networkTypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equalsIgnoreCase(type)) {
                        for (DataSnapshot child2 : child.getChildren()) {
                            if (child2.getKey().equalsIgnoreCase("number")) {
                                userDataCount = child2.getValue(Integer.class);
                                userDataCount++;
                            } else {
                                userDataCount = 1;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userDataCount;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        READ_PHONE_STATE,
                        ACCESS_COARSE_LOCATION,
                        INTERNET,
                        ACCESS_NETWORK_STATE
                }, RequestPermissionCode);

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Exit").setMessage("Are you want to exit?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
                System.exit(1);
            }
        }).setNegativeButton("No", null).show();
    }
}
