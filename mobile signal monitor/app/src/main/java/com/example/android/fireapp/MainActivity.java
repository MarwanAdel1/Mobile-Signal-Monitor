package com.example.android.fireapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.fireapp.menuButtons.AlertOnOff;
import com.example.android.fireapp.menuButtons.ContactUs;
import com.example.android.fireapp.menuButtons.EditeData;
import com.example.android.fireapp.menuButtons.Help;
import com.example.android.fireapp.menuButtons.SoundSetting;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.os.Build.VERSION_CODES.M;
import static com.example.android.fireapp.LoginActivity.userUid;
import static com.example.android.fireapp.R.id.signal;


public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;

    public static final int RequestPermissionCode = 1;

    private DatabaseReference networkTypeReference, allData;
    private int userDataCount = 1, mySignal = -1;

    private boolean signalFound = false;
    private LocationManager locationManager;
    public static ArrayList<LatLng> original;
    public static ArrayList<ArrayList<LatLng>> locationData;
    public static ArrayList<Integer> signalData;
    public static ArrayList<Integer> signalCount;
    public static ArrayList<LatLng> routingRangeDrawingLocation;
    public static ArrayList<Integer> routingRangeDrawingSignal;
    public static ArrayList<LatLngBounds> commonBounds;
    public static ArrayList<Integer> boundSignals;
    public static LatLng minPointLocation;
    public static Location myLocation;
    public int count_check = 0;
    float maxRange = 2000;
    private TextView FullName, FinalMail, FinalPhone, city;
    public static TextView Signal;
    private String type = LoginActivity.type, DispName, DispMail, DispPhone;
    ConnectivityManager connect;
    NetworkInfo networkInfo;

    ProgressDialog nDialog;
    SignalStrenthListener m;

    private ImageView mProfilePicture;
    private int PICK_IMAGE = 1;
    private Uri mImageUri;
    byte[] image;
    Bitmap bitmap;
    public static Bitmap retrievedImage;


    StorageReference imageRef;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        imageRef = storageRef.child("Images");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        networkTypeReference = firebaseDatabase.getReference("Database/NetworkTypes");
        allData = firebaseDatabase.getReference();

        nDialog = new ProgressDialog(MainActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Get Data");
        nDialog.show();


        ///////////////////Navigation Menu/////////////////
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main);
        Button menu = (Button) findViewById(R.id.Menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        NavigationView navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.data: {
                        Intent i = new Intent(MainActivity.this, EditeData.class);
                        startActivity(i);
                    }

                    break;
                    case R.id.alert: {
                        Intent i = new Intent(MainActivity.this, AlertOnOff.class);
                        startActivity(i);
                    }
                    break;
                    case R.id.sound: {
                        Intent i = new Intent(MainActivity.this, SoundSetting.class);
                        startActivity(i);
                    }
                    break;
                    case R.id.contact_us: {
                        Intent i = new Intent(MainActivity.this, ContactUs.class);
                        startActivity(i);
                    }
                    break;
                    case R.id.help: {
                        Intent i = new Intent(MainActivity.this, Help.class);
                        startActivity(i);
                    }
                    break;
                    case R.id.out:
                        if (isConnected()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(toLogin);
                        } else {
                            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });
        ////////////////////////////////////
        city = (TextView) findViewById(R.id.city);
        Button fetchdata = (Button) findViewById(R.id.fetchLocation);
        Button Order = (Button) findViewById(R.id.order);
        Button routeButton = (Button) findViewById(R.id.deadZone);
        FullName = (TextView) findViewById(R.id.fullName);
        FinalMail = (TextView) findViewById(R.id.mail);
        FinalPhone = (TextView) findViewById(R.id.phone);
        Signal = (TextView) findViewById(signal);
        mProfilePicture = (ImageView) findViewById(R.id.profileImage);
        locationData = new ArrayList<>(); // retrieving all location in the database
        signalData = new ArrayList<>();
        original = new ArrayList<>();
        signalCount = new ArrayList<>();
        routingRangeDrawingLocation = new ArrayList<>();
        routingRangeDrawingSignal = new ArrayList<>();
        commonBounds = new ArrayList<>();
        boundSignals = new ArrayList<>();


        int sig = SignalStrenthListener.x;
        if (sig == 0) {
            Signal.setText("Unknown");
        } else if (sig == 1) {
            MainActivity.Signal.setText("Poor");
        } else if (sig == 2) {
            MainActivity.Signal.setText("Intermediate");
        } else if (sig == 3) {
            MainActivity.Signal.setText("Good");
        } else if (sig == 4) {
            MainActivity.Signal.setText("Great");
        }

        m = new SignalStrenthListener();
        m.setContxt(MainActivity.this);

        /////////////////////

        if (checkPermission()) {
            Toast.makeText(MainActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        city.setText(getCompleteAddressString(myLocation.getLatitude(), myLocation.getLongitude()));

        userDataCount = fetchCount();
        fetchAllData();
        if (myLocation != null) {
            NearestPoints(myLocation, SignalStrenthListener.x);
            Log.i(MainActivity.class.getSimpleName(), "Just Rain : 2 : ");
        }

        ////////////////Buttons listeners////////////

        allData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot rootChild : dataSnapshot.getChildren()) {
                        if (rootChild.getKey().equalsIgnoreCase("UsersData")) {
                            for (DataSnapshot child : rootChild.getChildren()) {
                                if (child.child("Email").getValue().toString().equals(LoginActivity.LogMail)) {
                                    Log.i(MainActivity.class.getSimpleName(), "zain:3");
                                    DispMail = LoginActivity.LogMail;
                                    DispName = child.child("Name").getValue().toString();
                                    DispPhone = child.child("Phone").getValue().toString();

                                    FullName.setText(DispName);
                                    FinalMail.setText(DispMail);
                                    FinalPhone.setText(DispPhone);

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images/" + userUid);
                                    storageReference.getBytes(10485760).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            retrievedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            mProfilePicture.setImageBitmap(retrievedImage);
                                            nDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Photo Download Error", Toast.LENGTH_SHORT).show();
                                            nDialog.dismiss();
                                        }
                                    });

                                    break;
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(MainActivity.class.getSimpleName(), "Lina : " + myLocation);
                if (myLocation != null) {
                    if (SignalStrenthListener.x <= 5) {
                        int signal = findingMySignal(myLocation, SignalStrenthListener.x);
                        NearestPoints(myLocation, signal);
                        Log.i(MainActivity.class.getSimpleName(), "Signal : " + signal);
                        Log.i(MainActivity.class.getSimpleName(), "Fatma : " + minPointLocation + "\n" + myLocation.getLongitude());
                        // hnshel l equal bs - for testing
                        // Log.i(MainActivity.class.getSimpleName(),"Just Rain : 1"+"\n"+(userDataCount-1)+"\n"+SignalStrenthListener.x);
                        //        if ((userDataCount - 1) == count_check) {
                        count_check = 0;
                        Intent intentToMap = new Intent(MainActivity.this, DeadMapsActivity.class);
                        startActivity(intentToMap);
            /*            } else {
                            count_check = 0;
                            NearestPoints(myLocation);
                            Toast.makeText(MainActivity.this, "Still fetch", Toast.LENGTH_SHORT); //  hdos 3la l button tany
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Your Signal in its optimal state", Toast.LENGTH_SHORT).show();
*/
                    }
                }
            }
        });


        fetchdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(MainActivity.class.getName(), "Done : 1 : " + locationData.size());
                if (locationData.size() == 0) {
                    fetchAllData();
                }

                Log.i(MainActivity.class.getName(), "Done : 1 : " + locationData.size());
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
             /*   } else if (locationData.size() != userDataCount - 1) {
                    Toast.makeText(MainActivity.this, "Still fetch data from the server", Toast.LENGTH_SHORT).show();
                    locationData.clear();
                }
*/
            }
        });


        Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OrderingActivity.class);
                startActivity(intent);

            }
        });

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, mProfilePicture);
                popup.getMenuInflater().inflate(R.menu.image_down_list_activity, popup.getMenu());
                Log.i(MainActivity.class.getSimpleName(), "ANAS : ");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view:
                                Intent viewIntent = new Intent(MainActivity.this, imageView.class);
                                startActivity(viewIntent);
                                break;
                            case R.id.select:
                                Intent selectIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                selectIntent.setType("image/*");
                                startActivityForResult(Intent.createChooser(selectIntent, "Select Profile Picture"), PICK_IMAGE);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

    }
/////////////////////// intent image //////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), mImageUri);
                } catch (Exception e) {

                }

                UploadTask fileUpload = imageRef.child(userUid).putFile(mImageUri);
                fileUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProfilePicture.setImageBitmap(bitmap);
                        retrievedImage = bitmap;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Data = Null", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /////////////////methods//////////////////////
    private int findingMySignal(final Location l, int readSignal) {
        networkTypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals(type)) {
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                int signal = Integer.parseInt(child2.getKey());
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    ArrayList<LatLng> areabound = new ArrayList<>();
                                    for (DataSnapshot child4 : child3.child("Data").getChildren()) {
                                        double latitude = child4.child("Latitude").getValue(Double.class);
                                        double longitude = child4.child("Longitude").getValue(Double.class);
                                        LatLng location = new LatLng(latitude, longitude);
                                        areabound.add(location);
                                    }
                                    List<LatLng> listCheck = findBoundingBoxForGivenLocations(areabound);
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(listCheck.get(0));
                                    builder.include(listCheck.get(1));
                                    builder.include(listCheck.get(2));
                                    builder.include(listCheck.get(3));

                                    LatLngBounds bounds = builder.build();

                                    LatLng myLatlng = new LatLng(l.getLatitude(), l.getLongitude());
                                    if (bounds.contains(myLatlng)) {
                                        if (!signalFound) {
                                            mySignal = signal;
                                            signalFound = true;
                                            commonBounds.add(bounds);
                                            boundSignals.add(signal);
                                            break;
                                        } else {
                                            commonBounds.add(bounds);
                                            boundSignals.add(signal);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!signalFound) {
            mySignal = readSignal;
        }
        return mySignal;
    }


    private void NearestPoints(final Location l, final int signalpassed) {
        //1-search to the nearest point in db +add points to arraylist
        networkTypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            float minPointDis = 2000;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    float dis;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals(type)) {
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                if (signal > signalpassed) {
                                    if (commonBounds.size() == 1) {
                                        for (DataSnapshot child3 : child2.getChildren()) {
                                            for (DataSnapshot child4 : child3.child("Data").getChildren()) {
                                                double latitude = child4.child("Latitude").getValue(Double.class);
                                                double longitude = child4.child("Longitude").getValue(Double.class);
                                                LatLng location = new LatLng(latitude, longitude);

                                                Location newLocation = new Location("DataBase Location");
                                                newLocation.setLatitude(latitude);
                                                newLocation.setLongitude(longitude);

                                                dis = l.distanceTo(newLocation);

                                                if (dis <= minPointDis) {
                                                    minPointDis = dis;
                                                    minPointLocation = new LatLng(location.latitude, location.longitude);
                                                }
                                            }
                                        }
                                    }else if(commonBounds.size()>1){
                                        for (DataSnapshot child3 : child2.getChildren()) {
                                            for (DataSnapshot child4 : child3.child("Data").getChildren()) {
                                                double latitude = child4.child("Latitude").getValue(Double.class);
                                                double longitude = child4.child("Longitude").getValue(Double.class);
                                                LatLng location = new LatLng(latitude, longitude);

                                                for (int i=0; i<commonBounds.size();i++){
                                                    if (commonBounds.get(i).contains(location) && boundSignals.get(i)!=signalpassed){
                                                        Location newLocation = new Location("DataBase Location");
                                                        newLocation.setLatitude(latitude);
                                                        newLocation.setLongitude(longitude);

                                                        dis = l.distanceTo(newLocation);

                                                        if (dis <= minPointDis) {
                                                            minPointDis = dis;
                                                            minPointLocation = new LatLng(location.latitude, location.longitude);
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "your signal is in the best state", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public int fetchAllData() {
        networkTypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equalsIgnoreCase(type)) {
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                int count = 0;
                                int signal = Integer.parseInt(child2.getKey());
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    ArrayList<LatLng> newArray = new ArrayList<>();
                                        for (DataSnapshot child4 : child3.child("Data").getChildren()) {
                                            double latitude = child4.child("Latitude").getValue(Double.class);
                                            double longitude = child4.child("Longitude").getValue(Double.class);
                                            LatLng location = new LatLng(latitude, longitude);
                                            newArray.add(location);
                                        newArray.add(location);
                                    }
                                    count++;
                                    locationData.add(newArray);
                                }
                                signalCount.add(count);
                                signalData.add(signal);
                            }
                            for (int i = 0; i < signalData.size(); i++)
                                Log.i(MainActivity.class.getSimpleName(), "Lina : " + signalData.size() + "\n");
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "There's no data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return locationData.size();
    }

    public List<LatLng> findBoundingBoxForGivenLocations(ArrayList<LatLng> coordinates) {

        double west = 0.0;
        double east = 0.0;
        double north = 0.0;
        double south = 0.0;
        LatLng loc;
        for (int lc = 0; lc < coordinates.size(); lc++) {
            loc = coordinates.get(lc);
            if (lc == 0) {
                north = loc.latitude;
                south = loc.latitude;
                west = loc.longitude;
                east = loc.longitude;
            } else {
                if (loc.latitude > north) {
                    north = loc.latitude;
                } else if (loc.latitude < south) {
                    south = loc.latitude;
                }
                if (loc.longitude < west) {
                    west = loc.longitude;
                } else if (loc.longitude > east) {
                    east = loc.longitude;
                }
            }
        }

        // OPTIONAL - Add some extra "padding" for better map display
        double padding = 0.0;
        north = north + padding;
        south = south - padding;
        west = west - padding;
        east = east + padding;

        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(north, east));
        list.add(new LatLng(south, east));
        list.add(new LatLng(south, west));
        list.add(new LatLng(north, west));

        return list;
    }

    /////////////////Permissions //////////////////

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
                        Toast.makeText(MainActivity.this, "Permissions Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permissions Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        READ_PHONE_STATE,
                        ACCESS_COARSE_LOCATION,
                        INTERNET,
                        ACCESS_NETWORK_STATE
                }, RequestPermissionCode);

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


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {

            new AlertDialog.Builder(this).setTitle("Exit").setMessage("Are you want to exit?").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    moveTaskToBack(true);
                    System.exit(1);
                }
            }).setNegativeButton("Sign Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (isConnected()) {
                        FirebaseAuth.getInstance().signOut();
                        Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(toLogin);
                    } else {
                        Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();
        }
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        //   Log.e(TAG, "getCompleteAddressString: " +LONGITUDE+" " +LATITUDE );
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                //         Log.e(TAG, "getCompleteAddressString: Is this an ID ????????????? "+ returnedAddress.getAdminArea());

                strAdd = returnedAddress.getAdminArea();
                String[] arrayAdd = strAdd.split(" ");
                strAdd = arrayAdd[0];

            } else {
                Log.e("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("My Current location address", "Cannot get Address!" + e.getMessage());
        }
        return strAdd;
    }

}

