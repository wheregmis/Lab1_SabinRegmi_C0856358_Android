package com.example.lab1_sabinregmi_c0856358_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lab1_sabinregmi_c0856358_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;

    List<Marker> markers;
    private ArrayList<com.example.lab1_sabinregmi_c0856358_android.Location> latLngList = new ArrayList();

    // for drawing polygon
    Polygon shape;
    private static final int POLYGON_SIDES = 4;

    // location with location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;

    private String m_Text = "";

    FloatingActionButton fab;

    // instance of shared preferences
    SharedPreferences sharedPreferences;

    public static final String SHARED_PREFERENCES_NAME = "LabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // instantiate shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        latLngList = new ArrayList();
        markers = new ArrayList();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Hex Value with Transparency for Color");

                // Set up the input
                final EditText input = new EditText(MapsActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);


                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        Log.d("Color", "onClick: "+input);
                        shape.setFillColor(Integer.decode(m_Text));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();  }
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();
        // todo: uncomment below line to get from shared preferences
        getLocationsFromSharedPreferences();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                // simulating long tap to remove marker
                markers.remove(marker);
                marker.remove();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (markers.contains(marker)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Click to change the text of marker");

                    // Set up the input
                    final EditText input = new EditText(MapsActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);


                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            marker.setIcon(createPureTextIcon(input.getText().toString()));
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    return false;
                }else{
                    marker.remove();
                    return true;
                }

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                List<LatLng> test = polyline.getPoints();
                float[] results = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        results);

                // todo: Need to append in textbox
                Toast.makeText(MapsActivity.this, "Total distance between the two point of this line is "+results[0], Toast.LENGTH_LONG).show();

                // calculate midpoint of polyline
                LatLng centerLatLng = null;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(int i = 0 ; i < polyline.getPoints().size() ; i++)
                {
                    builder.include(polyline.getPoints().get(i));
                }
                LatLngBounds bounds = builder.build();
                centerLatLng =  bounds.getCenter();

                setMarkerInCoordinate(centerLatLng, results[0]);
            }
        });
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                List<LatLng> test = polygon.getPoints();
                float[] distanceBetweenAAndB = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        distanceBetweenAAndB);

                float[] distanceBetweenBAndC = new float[1];
                Location.distanceBetween(test.get(1).latitude, test.get(1).longitude,
                        test.get(2).latitude, test.get(2).longitude,
                        distanceBetweenBAndC);

                float[] distanceBetweenCAndD = new float[1];
                Location.distanceBetween(test.get(2).latitude, test.get(2).longitude,
                        test.get(3).latitude, test.get(3).longitude,
                        distanceBetweenCAndD);

                double totalDistance = distanceBetweenAAndB[0]+distanceBetweenBAndC[0]+distanceBetweenCAndD[0];

                // calculate midpoint of polyline
                LatLng centerLatLng = null;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(int i = 0 ; i < polygon.getPoints().size() ; i++)
                {
                    builder.include(polygon.getPoints().get(i));
                }
                LatLngBounds bounds = builder.build();
                centerLatLng =  bounds.getCenter();

                setMarkerInCoordinate(centerLatLng, (float) totalDistance);

                // todo: Need to append in textbox
                Toast.makeText(MapsActivity.this, "Total duration from A to B to C to D is "+ totalDistance, Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                markers.removeAll(markers);
                latLngList.removeAll(latLngList);
                addLocationToSharedPreferences();
                shape = null;
                mMap.clear();
                fab.hide();
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                setMarker(latLng);

                // todo: Uncomment below line to add location to shared preferences
                addLocationToSharedPreferences();

            }
        });

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
         Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMarker(lastKnownLocation);
    }

    private void drawLine() {
        // reason to make multiple options and make multiple polyline is because when we tap
        PolylineOptions options1 = new PolylineOptions()
                .color(Color.RED)
                .width(10)
                .add(markers.get(0).getPosition(), markers.get(1).getPosition());
        options1.clickable(true);
        options1.zIndex(2F);
        mMap.addPolyline(options1);

        PolylineOptions options2 = new PolylineOptions()
                .color(Color.RED)
                .width(10)
                .add(markers.get(1).getPosition(), markers.get(2).getPosition());
        options2.clickable(true);
        options2.zIndex(2F);
        mMap.addPolyline(options2);

        PolylineOptions options3 = new PolylineOptions()
                .color(Color.RED)
                .width(10)
                .add(markers.get(2).getPosition(), markers.get(3).getPosition());
        options3.clickable(true);
        options3.zIndex(2F);
        mMap.addPolyline(options3);

        PolylineOptions options4 = new PolylineOptions()
                .color(Color.RED)
                .width(10)
                .add(markers.get(3).getPosition(), markers.get(0).getPosition());
        options4.clickable(true);
        options4.zIndex(2F);
        mMap.addPolyline(options4);

    }

    private void drawShape() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x5900FF00)
                // 0xFF00FF00 is green and 59 instead of FF is 35% transparency
                .strokeColor(Color.RED)
                .strokeWidth(5);
        options.clickable(true);

        ArrayList<LatLng> sourcePoints = new ArrayList<>();

        for (int i=0; i<POLYGON_SIDES; i++) {
//                    options.add(markers.get(i).getPosition());
            sourcePoints.add(markers.get(i).getPosition());

        }
        Projection projection = mMap.getProjection();
        ArrayList<Point> screenPoints = new ArrayList<>(sourcePoints.size());
        for (LatLng location : sourcePoints) {
            Point p = projection.toScreenLocation(location);
            screenPoints.add(p);
        }

        ArrayList<Point> convexHullPoints = convexHull(screenPoints);
        ArrayList<LatLng> convexHullLocationPoints = new ArrayList(convexHullPoints.size());
        for (Point screenPoint : convexHullPoints) {
            LatLng location = projection.fromScreenLocation(screenPoint);
            convexHullLocationPoints.add(location);
        }

        for (LatLng latLng : convexHullLocationPoints) {
            options.add(latLng);
        }
        shape = mMap.addPolygon(options);
    }
    public BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint(); // Adapt to your needs
        textPaint.setTextSize(50);
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) (textHeight);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        // For development only:
        // Set a background in order to see the
        // full size and positioning of the bitmap.
        // Remove that for a fully transparent icon.
        canvas.drawColor(Color.LTGRAY);

        canvas.drawText(text, 0, 0, textPaint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }
    public void setMarker(LatLng latLng) {
        //Log.d("MapsActivity", "setMarker: "+latLng);
        if (markers.size() == 0){
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title("A")
                    .icon(createPureTextIcon("A"))
                    ;
            options.draggable(true);

            // check if there are already the same number of markers, we clear the map.
            markers.add(mMap.addMarker(options));
            latLngList.add(new com.example.lab1_sabinregmi_c0856358_android.Location(latLng));
        }else if (markers.size() == 1){
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title("B")
                    .icon(createPureTextIcon("B"));
            options.draggable(true);

            // check if there are already the same number of markers, we clear the map.
            markers.add(mMap.addMarker(options));
            latLngList.add(new com.example.lab1_sabinregmi_c0856358_android.Location(latLng));
        }else if (markers.size() == 2){
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title("C")
                    .icon(createPureTextIcon("C"));
            options.draggable(true);

            // check if there are already the same number of markers, we clear the map.
            markers.add(mMap.addMarker(options));
            latLngList.add(new com.example.lab1_sabinregmi_c0856358_android.Location(latLng));
        }else if (markers.size() == 3){
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title("D")
                    .icon(createPureTextIcon("D"));
            options.draggable(true);
            // check if there are already the same number of markers, we clear the map.
            markers.add(mMap.addMarker(options));
            latLngList.add(new com.example.lab1_sabinregmi_c0856358_android.Location(latLng));
        }else{
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title("P");
            options.draggable(true);
//
//                    // check if there are already the same number of markers, we clear the map.
            markers.add(mMap.addMarker(options));
            latLngList.add(new com.example.lab1_sabinregmi_c0856358_android.Location(latLng));

        }


        if (markers.size() == POLYGON_SIDES)
        {
            drawShape();
            drawLine();
            fab.show();
        }

    }

    private void setMarkerInCoordinate(LatLng latLng, Float distance){
        Log.i("MapsActivity", "setMarkerInCoordinate: "+latLng);
        DecimalFormat df = new DecimalFormat("#.##");
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title("")
                .icon(createPureTextIcon(String.valueOf(df.format(distance))));
        mMap.addMarker(options);
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    public void addLocationToSharedPreferences(){
        try {
            //Log.i("MapsActivity", "addLocationToSharedPreferences: "+latLngList.get(0).getLatLng().latitude);
            sharedPreferences.edit().putString("location_serialized", ObjectSerializer.serialize((Serializable) latLngList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getLocationsFromSharedPreferences(){
        String receivedSerializedString = sharedPreferences.getString("location_serialized", null);
        try {
            ArrayList<com.example.lab1_sabinregmi_c0856358_android.Location>testLatLang = new ArrayList();
            testLatLang = (ArrayList<com.example.lab1_sabinregmi_c0856358_android.Location>) ObjectSerializer.deserialize(receivedSerializedString);
            if (testLatLang != null){
                for (com.example.lab1_sabinregmi_c0856358_android.Location location: testLatLang){
                    Log.i("MapsActivity", "Getting value from Shared: "+location.getLatLng());
                    if (location.getLatLng() != null){
                        setMarker(location.getLatLng());
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private boolean CCW(Point p, Point q, Point r) {
        return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y) > 0;
    }

    public ArrayList<Point> convexHull(ArrayList<Point> points)
    {
        int n = points.size();
        if (n <= 3) return points;

        ArrayList<Integer> next = new ArrayList<>();

        // find the leftmost point
        int leftMost = 0;
        for (int i = 1; i < n; i++)
            if (points.get(i).x < points.get(leftMost).x)
                leftMost = i;
        int p = leftMost, q;
        next.add(p);

        // iterate till p becomes leftMost
        do {
            q = (p + 1) % n;
            for (int i = 0; i < n; i++)
                if (CCW(points.get(p), points.get(i), points.get(q)))
                    q = i;
            next.add(q);
            p = q;
        } while (p != leftMost);

        ArrayList<Point> convexHullPoints = new ArrayList();
        for (int i = 0; i < next.size() - 1; i++) {
            int ix = next.get(i);
            convexHullPoints.add(points.get(ix));
        }

        return convexHullPoints;
    }
}