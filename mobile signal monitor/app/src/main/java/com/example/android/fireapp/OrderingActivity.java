package com.example.android.fireapp;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class OrderingActivity extends AppCompatActivity {


    private DatabaseReference networkTypeReference;

    private Location myNewLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        final TextView First = (TextView) findViewById(R.id.first);
        final TextView Second = (TextView) findViewById(R.id.second);
        final TextView Third = (TextView) findViewById(R.id.third);
        final TextView Fourth = (TextView) findViewById(R.id.fourth);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
        networkTypeReference = firebaseDatabase.getReference("Database/NetworkTypes");
        myNewLocation = MainActivity.myLocation;
        Log.i(OrderingActivity.class.getSimpleName(), "orderedites1 " +myNewLocation +"\n");

        networkTypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            int ObigSignalValue=0,VbigSignalValue=0,EbigSignalValue=0,TbigSignalValue=0;
            int OSmallSignalValue=0,VSmallSignalValue=0,ESmallSignalValue=0,TSmallSignalValue=0;
            int OcountManual=0,VcountManual=0,EcountManual=0,TcountManual=0;
            double Ofinal = 0, Vfinal = 0, Efinal = 0, Tfinal = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().toUpperCase().charAt(0) == 'O') {  //ORANGE
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                OSmallSignalValue= Integer.parseInt(child2.getKey());
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    Location first = new Location("First");
                                    for (DataSnapshot child4 : child3.child("Data").getChildren()) {

                                        double latitude = child4.child("Latitude").getValue(Double.class);
                                        double longitude = child4.child("Longitude").getValue(Double.class);

                                        first.setLatitude(latitude);
                                        first.setLongitude(longitude);

                                        if (myNewLocation.distanceTo(first) <= 1000) {
                                            Log.i(OrderingActivity.class.getSimpleName(), "ordereditesO2 "  +"\n");
                                            int userDataCount = child3.child("Number").getValue(Integer.class);
                                            OcountManual += userDataCount;
                                            ObigSignalValue = userDataCount * OSmallSignalValue;
                                             break;
                                        }else{
                                           // break;
                                        }
                                    }
                                }
                            }
                            if(OcountManual>0)
                            Ofinal = ObigSignalValue /  OcountManual;
                            Log.i(OrderingActivity.class.getSimpleName(), "orderedite7 " + Ofinal +"\n");
                        }


                        if (child.getKey().toUpperCase().charAt(0) == 'V') {  //VODAFONE
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                VSmallSignalValue= Integer.parseInt(child2.getKey());
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    Location first = new Location("First");
                                    for (DataSnapshot child4 : child3.child("Data").getChildren()) {

                                        double latitude = child4.child("Latitude").getValue(Double.class);
                                        double longitude = child4.child("Longitude").getValue(Double.class);

                                        first.setLatitude(latitude);
                                        first.setLongitude(longitude);
                                        if (myNewLocation.distanceTo(first) <= 1000) {
                                            int userDataCount = child3.child("Number").getValue(Integer.class);
                                            VcountManual += userDataCount;
                                            VbigSignalValue = userDataCount * VSmallSignalValue;
                                            break;
                                        }else{
                                            //break;
                                        }
                                    }
                                }
                            }
                            if(VcountManual>0)
                           Vfinal = VbigSignalValue /  VcountManual;
                            Log.i(OrderingActivity.class.getSimpleName(), "ordereditesVelse7 " + Vfinal +"\n");
                        }

                        if (child.getKey().toUpperCase().charAt(0) == 'E') {  //ETISALAT
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                ESmallSignalValue= Integer.parseInt(child2.getKey());
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    Location first = new Location("First");
                                    for (DataSnapshot child4 : child3.child("Data").getChildren()) {

                                        double latitude = child4.child("Latitude").getValue(Double.class);
                                        double longitude = child4.child("Longitude").getValue(Double.class);

                                        first.setLatitude(latitude);
                                        first.setLongitude(longitude);
                                        if (myNewLocation.distanceTo(first) <= 1000) {
                                            int userDataCount = child3.child("Number").getValue(Integer.class);
                                            EcountManual += userDataCount;
                                            EbigSignalValue = userDataCount * ESmallSignalValue;
                                            break;
                                        }else{
                                            //break;
                                        }
                                    }
                                }
                            }
                            if(EcountManual>0)
                            Efinal = EbigSignalValue /  EcountManual;
                        }

                        if (child.getKey().toUpperCase().charAt(0) == 'T') {  //TE
                            for (DataSnapshot child2 : child.child("Signal").getChildren()) {
                                TSmallSignalValue= Integer.parseInt(child2.getKey());
                                for (DataSnapshot child3 : child2.getChildren()) {
                                    Location first = new Location("First");
                                    for (DataSnapshot child4 : child3.child("Data").getChildren()) {

                                        double latitude = child4.child("Latitude").getValue(Double.class);
                                        double longitude = child4.child("Longitude").getValue(Double.class);

                                        first.setLatitude(latitude);
                                        first.setLongitude(longitude);
                                        if (myNewLocation.distanceTo(first) <= 1000) {
                                            int userDataCount = child3.child("Number").getValue(Integer.class);
                                            TcountManual += userDataCount;
                                            TbigSignalValue = userDataCount * TSmallSignalValue;
                                            break;
                                        }else{
                                            //break;
                                        }
                                    }
                                }
                            }
                            if(TcountManual>0)
                            Tfinal = TbigSignalValue /  TcountManual;
                        }

                    }
                }

                double[] array = new double[4];
                array[0] = Ofinal;
                array[1] = Vfinal;
                array[2] = Efinal;
                array[3] = Tfinal;

                Arrays.sort(array);
                if (Ofinal == array[3] && (First.getText()=="")) {    //orange
                    if (Ofinal == 0) {
                        First.setText("there is no data for Orange now in this area, please try again in another time");
                        First.setTextSize(10);
                    } else {
                        First.setText("Orange");
                    }
                    First.setBackground(getDrawable(R.drawable.mobinil));
                } else if (Ofinal == array[2] && Second.getText() == "") {
                    if (Ofinal == 0) {
                        Second.setText("there is no data for Orange now in this area, please try again in another time");
                        Second.setTextSize(10);
                    } else {
                        Second.setText("Orange");
                    }
                    Second.setBackground(getDrawable(R.drawable.mobinil));
                } else if (Ofinal == array[1] && Third.getText() == "") {
                    if (Ofinal == 0) {
                        Third.setText("there is no data for Orange now in this area, please try again in another time");
                        Third.setTextSize(10);
                    } else {
                        Third.setText("Orange");
                    }
                    Third.setBackground(getDrawable(R.drawable.mobinil));
                } else {
                    if (Ofinal == 0) {
                        Fourth.setText("there is no data for Orange now in this area, please try again in another time");
                        Fourth.setTextSize(10);
                    } else {
                        Fourth.setText("Orange");
                    }
                    Fourth.setBackground(getDrawable(R.drawable.mobinil));
                }


                if (Vfinal == array[3] && First.getText() == "") {   //vodafone
                    if (Vfinal == 0) {
                        First.setText("there is no data for Vodafone now in this area, please try again in another time");
                        First.setTextSize(10);
                    } else {
                        First.setText("Vodafone");
                    }
                    First.setBackground(getDrawable(R.drawable.vodafone));
                } else if (Vfinal == array[2] && Second.getText() == "") {
                    if (Vfinal == 0) {
                        Second.setText("there is no data for Vodafone now in this area, please try again in another time");
                        Second.setTextSize(10);
                    } else {
                        Second.setText("Vodafone");
                    }
                    Second.setBackground(getDrawable(R.drawable.vodafone));
                } else if (Vfinal == array[1] && Third.getText() == "") {
                    if (Vfinal == 0) {
                        Third.setText("there is no data for Vodafone now in this area, please try again in another time");
                        Third.setTextSize(10);
                    } else {
                        Third.setText("Vodafone");
                    }
                    Third.setBackground(getDrawable(R.drawable.vodafone));
                } else {
                    if (Vfinal == 0) {
                        Fourth.setText("there is no data for Vodafone now in this area, please try again in another time");
                        Fourth.setTextSize(10);
                    } else {
                        Fourth.setText("Vodafone");
                    }
                    Fourth.setBackground(getDrawable(R.drawable.vodafone));
                }


                if (Efinal == array[3] && First.getText() == "") {   //etisalat
                    if (Efinal == 0) {
                        First.setText("there is no data for Etisalat now in this area, please try again in another time");
                        First.setTextSize(10);
                    } else {
                        First.setText("Etisalat");
                    }
                    First.setBackground(getDrawable(R.drawable.etisalat));
                } else if (Efinal == array[2] && Second.getText() == "") {
                    if (Efinal == 0) {
                        Second.setText("there is no data for Etisalat now in this area, please try again in another time");
                        Second.setTextSize(10);
                    } else {
                        Second.setText("Etisalat");
                    }
                    Second.setBackground(getDrawable(R.drawable.etisalat));
                } else if (Efinal == array[1] && Third.getText() == "") {
                    if (Efinal == 0) {
                        Third.setText("there is no data for Etisalat now in this area, please try again in another time");
                        Third.setTextSize(10);
                    } else {
                        Third.setText("Etisalat");
                    }
                    Third.setBackground(getDrawable(R.drawable.etisalat));
                } else {
                    if (Efinal == 0) {
                        Fourth.setText("there is no data for Etisalat now in this area, please try again in another time");
                        Fourth.setTextSize(10);
                    } else {
                        Fourth.setText("Etisalat");
                    }
                    Fourth.setBackground(getDrawable(R.drawable.etisalat));
                }


                if (Tfinal == array[3] && First.getText() == "") {   //we
                    if (Tfinal == 0) {
                        First.setText("there is no data for We now in this area, please try again in another time");
                        First.setTextSize(10);
                    } else {
                        First.setText("We");
                    }
                    First.setBackground(getDrawable(R.drawable.we));
                } else if (Tfinal == array[2] && Second.getText() == "") {
                    if (Tfinal == 0) {
                        Second.setText("there is no data for We now in this area, please try again in another time");
                        Second.setTextSize(10);
                    } else {
                        Second.setText("We");
                    }
                    Second.setBackground(getDrawable(R.drawable.we));
                } else if (Tfinal == array[1] && Third.getText() == "") {
                    if (Tfinal == 0) {
                        Third.setText("there is no data for We now in this area, please try again in another time");
                        Third.setTextSize(10);
                    } else {
                        Third.setText("We");
                    }
                    Third.setBackground(getDrawable(R.drawable.we));
                } else {
                    if (Tfinal == 0) {
                        Fourth.setText("there is no data for Te now in this area, please try again in another time");
                        Fourth.setTextSize(10);
                    } else {
                        Fourth.setText("We");
                    }
                    Fourth.setBackground(getDrawable(R.drawable.we));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
