package com.example.android.fireapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.fireapp.MainActivity.signalData;
import static com.example.android.fireapp.R.id.signal;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    private GoogleApiClient client;
    private ArrayList<Integer> sig ,count;
    private ArrayList<ArrayList<LatLng>> loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(MapsActivity.class.getSimpleName(), "Test : Maps Activity start");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loc = MainActivity.locationData;
        sig = MainActivity.signalData;
        count = MainActivity.signalCount;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        int i;
        if (loc.size() > 0) {
            int countttt = 0;
            int j = 0;
            for (i = 0; i < loc.size(); i++) {
                List<LatLng> l;
                if (loc.get(i).size() > 0 && count.get(j) > 0) {
                    l = findBoundingBoxForGivenLocations(loc.get(i));

                    if (sig.get(j) == 4) { // 5 bar - green
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 0, 255, 0)).fillColor(Color.argb(130, 0, 255, 0)));
                    } else if (sig.get(j) == 3) {  // 4 bar - blue
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 0, 0, 255)).fillColor(Color.argb(130, 0, 0, 255)));
                    } else if (sig.get(j) == 2) { // 3 bar - yellow
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 255, 0)).fillColor(Color.argb(130, 255, 255, 0)));
                    } else if (sig.get(j) == 1) {  // 2 bar - orange
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 128, 0)).fillColor(Color.argb(130, 255, 128, 0)));
                    } else if (sig.get(j) == 0) { // 1 bar - red
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 0, 0)).fillColor(Color.argb(130, 255, 0, 0)));
                    }

                    countttt++;
                    if (count.get(j) == countttt) {
                        j++;
                        countttt = 0;
                    }
                }
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public List<LatLng> findBoundingBoxForGivenLocations(ArrayList<LatLng> coordinates)
    {

        double west = 0.0;
        double east = 0.0;
        double north = 0.0;
        double south = 0.0;
        LatLng loc;
        for (int lc = 0; lc < coordinates.size(); lc++)
        {
            loc = coordinates.get(lc);
            if (lc == 0)
            {
                north = loc.latitude;
                south = loc.latitude;
                west = loc.longitude;
                east = loc.longitude;
            }
            else
            {
                if (loc.latitude > north)
                {
                    north = loc.latitude;
                }
                else if (loc.latitude < south)
                {
                    south = loc.latitude;
                }
                if (loc.longitude < west)
                {
                    west = loc.longitude;
                }
                else if (loc.longitude > east)
                {
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
        list.add(new LatLng(north,east));
        list.add(new LatLng(south,east));
        list.add(new LatLng(south,west));
        list.add(new LatLng(north,west));

        return list;
    }
}


