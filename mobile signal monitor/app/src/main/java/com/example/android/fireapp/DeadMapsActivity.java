package com.example.android.fireapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeadMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
    private GoogleApiClient client;

    ArrayList<LatLng> MarkerPoints1;
    private Button drive1, walk1;
    private TextView txtDistance, txtTime;
    private boolean noholes;

    private Polyline line;

    private Location originalLocation;
    private LatLng originalLatlng;
    private LatLng destinationLatlng;
    private Location destinationLocation;

    private ArrayList<Integer> sig, count;
    private ArrayList<ArrayList<LatLng>> loc;
    private ArrayList<List<LatLng>> allList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dead_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        allList = new ArrayList<>();
        loc = MainActivity.locationData;
        sig = MainActivity.signalData;
        count = MainActivity.signalCount;

        MarkerPoints1 = new ArrayList<>();
        //  nearest=new ArrayList<>();
        txtDistance = (TextView) findViewById(R.id.txt_distance);
        txtTime = (TextView) findViewById(R.id.txt_time);
        drive1 = (Button) findViewById(R.id.drive);
        walk1 = (Button) findViewById(R.id.walk);

        if (MainActivity.myLocation != null && MainActivity.minPointLocation != null) {
            originalLocation = MainActivity.myLocation;
            destinationLocation = new Location("Destination");
            destinationLocation.setLatitude(MainActivity.minPointLocation.latitude);
            destinationLocation.setLongitude(MainActivity.minPointLocation.longitude);

            originalLatlng = new LatLng(MainActivity.myLocation.getLatitude(),MainActivity.myLocation.getLongitude());
            destinationLatlng = new LatLng(MainActivity.minPointLocation.latitude,MainActivity.minPointLocation.longitude);
        }

        drive1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeadMapsActivity.this, "start", Toast.LENGTH_SHORT).show();
                if (originalLatlng != null && destinationLatlng != null) {
                    DeadMapsActivity.this.getRoutingPathdrive();
                }
            }
        });
        walk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeadMapsActivity.this, "start", Toast.LENGTH_SHORT).show();
                if (originalLatlng != null && destinationLatlng != null) {
                    DeadMapsActivity.this.getRoutingPathwalk();
                }
            }
        });

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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        int g = 0;
        int i;
        if (loc.size() > 0) {
            noholes = false;
            int countttt = 0;
            int j = sig.size()-1;
            for (i = loc.size()-1; i >=0; i--) {
                List<LatLng> l = new ArrayList<>();
                if (loc.get(i).size() > 0 && count.get(j) > 0) {
                    l = findBoundingBoxForGivenLocations(loc.get(i));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(l.get(0));
                    builder.include(l.get(1));
                    builder.include(l.get(2));
                    builder.include(l.get(3));

                    LatLngBounds bounds = builder.build();
                    /*
                    if (MainActivity.minPointLocation != null && !noholes) {
                        if (bounds.contains(new LatLng(MainActivity.minPointLocation.latitude, MainActivity.minPointLocation.longitude))) {
                            originalLatlng = new LatLng(MainActivity.myLocation.getLatitude(), MainActivity.myLocation.getLongitude());
                            destinationLatlng = new LatLng(MainActivity.minPointLocation.latitude, MainActivity.minPointLocation.longitude);
                            LatLng center = bounds.getCenter();
                            destinationLatlng = center;/*destinationLatlng*/;
             /*               mMap.addMarker(new MarkerOptions().position(destinationLatlng).title("Center : "));
                        }
                    }
                    */

                    if (sig.get(j) == 4) { // 5 bar - green
                        //mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 0, 255, 0)).fillColor(Color.argb(130, 0, 255, 0)));
                        /*yellow*/
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 255, 0)).fillColor(Color.argb(70, 255, 255, 0)));
                    } else if (sig.get(j) == 3) {  // 4 bar - blue
                        //mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 0, 0, 255)).fillColor(Color.argb(130, 0, 0, 255)));
                        /* orange */
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 150, 0)).fillColor(Color.argb(100, 230, 150, 0)));
                    } else if (sig.get(j) == 2) { // 3 bar - yellow
//                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 255, 0)).fillColor(Color.argb(130, 255, 255, 0)));
                         /*red */
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 0, 0)).fillColor(Color.argb(100, 255, 0, 0)));
                    } else if (sig.get(j) == 1) {  // 2 bar - orange
                        //                      mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 128, 0)).fillColor(Color.argb(130, 255, 128, 0)));
                          /*green*/
                        mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 51, 204, 51)).fillColor(Color.argb(140, 51, 204, 51)));
                    } else if (sig.get(j) == 0) { // 1 bar - red
                        //mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 255, 0, 0)).fillColor(Color.argb(130, 255, 0, 0)));
                        /*black*/mMap.addPolygon(new PolygonOptions().addAll(l).strokeColor(Color.argb(0, 51, 51, 204)).fillColor(Color.argb(255, 0, 0, 0)));
                    }

                    countttt++;
                    if (count.get(j) == countttt) {
                        j--;
                        countttt = 0;
                    }
                }
            }
        }
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

    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(DeadMapsActivity.this, "Routing Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> list, int i) {
        try {
            //Get all points and plot the polyLine route.
            List<LatLng> listPoints = list.get(0).getPoints();
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            Iterator<LatLng> iterator = listPoints.iterator();
            while (iterator.hasNext()) {
                LatLng data = iterator.next();
                options.add(data);
            }

            //If line not null then remove old polyline routing.
            if (line != null) {
                line.remove();
            }
            line = mMap.addPolyline(options);

            //Show distance and duration.
            txtDistance.setText("Distance: " + "" + list.get(0).getDistanceText());
            txtTime.setText("Duration: " + "" + list.get(0).getDurationText());

            //Focus on map bounds
            mMap.moveCamera(CameraUpdateFactory.newLatLng(list.get(0).getLatLgnBounds().getCenter()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(originalLatlng);
            builder.include(destinationLatlng);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 18));
        } catch (Exception e) {
            Toast.makeText(DeadMapsActivity.this, "EXCEPTION: Cannot parse routing response", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {
        Toast.makeText(DeadMapsActivity.this, "Routing Cancelled", Toast.LENGTH_SHORT).show();
    }


    private void getRoutingPathdrive() {
        try {
            Log.i(DeadMapsActivity.class.getSimpleName(), "d5");
            Toast.makeText(DeadMapsActivity.this, "drive Route", Toast.LENGTH_SHORT).show();
            //Do Routing
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)///can change so i will put button to switch
                    //..travelMode(Routing.TravelMode.WALKING)

                    .withListener(this)
                    .waypoints(originalLatlng, destinationLatlng)
                    .build();
            routing.execute();
            Log.i(DeadMapsActivity.class.getSimpleName(), "d6");
        } catch (Exception e) {
            Toast.makeText(DeadMapsActivity.this, "Unable to Route", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRoutingPathwalk() {
        try {
            Toast.makeText(DeadMapsActivity.this, "walk Route", Toast.LENGTH_SHORT).show();
            //Do Routing
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(originalLatlng, destinationLatlng)
                    .build();
            routing.execute();
        } catch (Exception e) {
            Toast.makeText(DeadMapsActivity.this, "Unable to Route", Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        client.connect();

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
}
