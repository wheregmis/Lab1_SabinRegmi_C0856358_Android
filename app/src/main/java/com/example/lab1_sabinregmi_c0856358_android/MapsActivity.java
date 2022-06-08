package com.example.lab1_sabinregmi_c0856358_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lab1_sabinregmi_c0856358_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;

    List<Marker> markers = new ArrayList();

    // for drawing polygon
    Polyline line;
    Polygon shape;
    private static final int POLYGON_SIDES = 4;

    // location with location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                List<LatLng> test = polyline.getPoints();
                float[] results = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        results);

                Toast.makeText(MapsActivity.this, "Total distance between the two point of this line is "+results[0], Toast.LENGTH_LONG).show();
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

                Toast.makeText(MapsActivity.this, "Total duration from A to B to C to D is "+ totalDistance, Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                setMarker(latLng);
            }
            private void setMarker(LatLng latLng) {

//                String json_str = "{\"AD\":\"Europe\",\"AE\":\"Asia\",\"AF\":\"Asia\",\"AG\":\"North America\",\"AI\":\"North America\",\"AL\":\"Europe\",\"AM\":\"Asia\",\"AN\":\"North America\",\"AO\":\"Africa\",\"AQ\":\"Antarctica\",\"AR\":\"South America\",\"AS\":\"Australia\",\"AT\":\"Europe\",\"AU\":\"Australia\",\"AW\":\"North America\",\"AZ\":\"Asia\",\"BA\":\"Europe\",\"BB\":\"North America\",\"BD\":\"Asia\",\"BE\":\"Europe\",\"BF\":\"Africa\",\"BG\":\"Europe\",\"BH\":\"Asia\",\"BI\":\"Africa\",\"BJ\":\"Africa\",\"BM\":\"North America\",\"BN\":\"Asia\",\"BO\":\"South America\",\"BR\":\"South America\",\"BS\":\"North America\",\"BT\":\"Asia\",\"BW\":\"Africa\",\"BY\":\"Europe\",\"BZ\":\"North America\",\"CA\":\"North America\",\"CC\":\"Asia\",\"CD\":\"Africa\",\"CF\":\"Africa\",\"CG\":\"Africa\",\"CH\":\"Europe\",\"CI\":\"Africa\",\"CK\":\"Australia\",\"CL\":\"South America\",\"CM\":\"Africa\",\"CN\":\"Asia\",\"CO\":\"South America\",\"CR\":\"North America\",\"CU\":\"North America\",\"CV\":\"Africa\",\"CX\":\"Asia\",\"CY\":\"Asia\",\"CZ\":\"Europe\",\"DE\":\"Europe\",\"DJ\":\"Africa\",\"DK\":\"Europe\",\"DM\":\"North America\",\"DO\":\"North America\",\"DZ\":\"Africa\",\"EC\":\"South America\",\"EE\":\"Europe\",\"EG\":\"Africa\",\"EH\":\"Africa\",\"ER\":\"Africa\",\"ES\":\"Europe\",\"ET\":\"Africa\",\"FI\":\"Europe\",\"FJ\":\"Australia\",\"FK\":\"South America\",\"FM\":\"Australia\",\"FO\":\"Europe\",\"FR\":\"Europe\",\"GA\":\"Africa\",\"GB\":\"Europe\",\"GD\":\"North America\",\"GE\":\"Asia\",\"GF\":\"South America\",\"GG\":\"Europe\",\"GH\":\"Africa\",\"GI\":\"Europe\",\"GL\":\"North America\",\"GM\":\"Africa\",\"GN\":\"Africa\",\"GP\":\"North America\",\"GQ\":\"Africa\",\"GR\":\"Europe\",\"GS\":\"Antarctica\",\"GT\":\"North America\",\"GU\":\"Australia\",\"GW\":\"Africa\",\"GY\":\"South America\",\"HK\":\"Asia\",\"HN\":\"North America\",\"HR\":\"Europe\",\"HT\":\"North America\",\"HU\":\"Europe\",\"ID\":\"Asia\",\"IE\":\"Europe\",\"IL\":\"Asia\",\"IM\":\"Europe\",\"IN\":\"Asia\",\"IO\":\"Asia\",\"IQ\":\"Asia\",\"IR\":\"Asia\",\"IS\":\"Europe\",\"IT\":\"Europe\",\"JE\":\"Europe\",\"JM\":\"North America\",\"JO\":\"Asia\",\"JP\":\"Asia\",\"KE\":\"Africa\",\"KG\":\"Asia\",\"KH\":\"Asia\",\"KI\":\"Australia\",\"KM\":\"Africa\",\"KN\":\"North America\",\"KP\":\"Asia\",\"KR\":\"Asia\",\"KW\":\"Asia\",\"KY\":\"North America\",\"KZ\":\"Asia\",\"LA\":\"Asia\",\"LB\":\"Asia\",\"LC\":\"North America\",\"LI\":\"Europe\",\"LK\":\"Asia\",\"LR\":\"Africa\",\"LS\":\"Africa\",\"LT\":\"Europe\",\"LU\":\"Europe\",\"LV\":\"Europe\",\"LY\":\"Africa\",\"MA\":\"Africa\",\"MC\":\"Europe\",\"MD\":\"Europe\",\"ME\":\"Europe\",\"MG\":\"Africa\",\"MH\":\"Australia\",\"MK\":\"Europe\",\"ML\":\"Africa\",\"MM\":\"Asia\",\"MN\":\"Asia\",\"MO\":\"Asia\",\"MP\":\"Australia\",\"MQ\":\"North America\",\"MR\":\"Africa\",\"MS\":\"North America\",\"MT\":\"Europe\",\"MU\":\"Africa\",\"MV\":\"Asia\",\"MW\":\"Africa\",\"MX\":\"North America\",\"MY\":\"Asia\",\"MZ\":\"Africa\",\"NA\":\"Africa\",\"NC\":\"Australia\",\"NE\":\"Africa\",\"NF\":\"Australia\",\"NG\":\"Africa\",\"NI\":\"North America\",\"NL\":\"Europe\",\"NO\":\"Europe\",\"NP\":\"Asia\",\"NR\":\"Australia\",\"NU\":\"Australia\",\"NZ\":\"Australia\",\"OM\":\"Asia\",\"PA\":\"North America\",\"PE\":\"South America\",\"PF\":\"Australia\",\"PG\":\"Australia\",\"PH\":\"Asia\",\"PK\":\"Asia\",\"PL\":\"Europe\",\"PM\":\"North America\",\"PN\":\"Australia\",\"PR\":\"North America\",\"PS\":\"Asia\",\"PT\":\"Europe\",\"PW\":\"Australia\",\"PY\":\"South America\",\"QA\":\"Asia\",\"RE\":\"Africa\",\"RO\":\"Europe\",\"RS\":\"Europe\",\"RU\":\"Europe\",\"RW\":\"Africa\",\"SA\":\"Asia\",\"SB\":\"Australia\",\"SC\":\"Africa\",\"SD\":\"Africa\",\"SE\":\"Europe\",\"SG\":\"Asia\",\"SH\":\"Africa\",\"SI\":\"Europe\",\"SJ\":\"Europe\",\"SK\":\"Europe\",\"SL\":\"Africa\",\"SM\":\"Europe\",\"SN\":\"Africa\",\"SO\":\"Africa\",\"SR\":\"South America\",\"ST\":\"Africa\",\"SV\":\"North America\",\"SY\":\"Asia\",\"SZ\":\"Africa\",\"TC\":\"North America\",\"TD\":\"Africa\",\"TF\":\"Antarctica\",\"TG\":\"Africa\",\"TH\":\"Asia\",\"TJ\":\"Asia\",\"TK\":\"Australia\",\"TM\":\"Asia\",\"TN\":\"Africa\",\"TO\":\"Australia\",\"TR\":\"Asia\",\"TT\":\"North America\",\"TV\":\"Australia\",\"TW\":\"Asia\",\"TZ\":\"Africa\",\"UA\":\"Europe\",\"UG\":\"Africa\",\"US\":\"North America\",\"UY\":\"South America\",\"UZ\":\"Asia\",\"VC\":\"North America\",\"VE\":\"South America\",\"VG\":\"North America\",\"VI\":\"North America\",\"VN\":\"Asia\",\"VU\":\"Australia\",\"WF\":\"Australia\",\"WS\":\"Australia\",\"YE\":\"Asia\",\"YT\":\"Africa\",\"ZA\":\"Africa\",\"ZM\":\"Africa\",\"ZW\":\"Africa\"}";
//
//                try {
//
//                    JSONObject jsonObject = new JSONObject(json_str);
//
//                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
//
//                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//
//                    if (addresses.size() > 0) {
//
//                        Address fetchedAddress = addresses.get(0);
//
//                        // getCountryCode from Address
//                        String countryCode = fetchedAddress.getCountryCode();
//
//                        // get continentName here
//                        String continentName = jsonObject.getString(countryCode);
//
//                        if (continentName == "North America"){
//
//
//                        }
//                        Log.d("Contient Test", "setMarker: "+continentName);
//                    }else{
//                        MarkerOptions options = new MarkerOptions().position(latLng)
//                                .title("F");
//
//                        // check if there are already the same number of markers, we clear the map.
//                        markers.add(mMap.addMarker(options));
//
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                if (markers.size() == 0){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("A");

                    // check if there are already the same number of markers, we clear the map.
                    markers.add(mMap.addMarker(options));
                }else if (markers.size() == 1){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("B");

                    // check if there are already the same number of markers, we clear the map.
                    markers.add(mMap.addMarker(options));
                }else if (markers.size() == 2){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("C");

                    // check if there are already the same number of markers, we clear the map.
                    markers.add(mMap.addMarker(options));
                }else if (markers.size() == 3){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("D");

                    // check if there are already the same number of markers, we clear the map.
                    markers.add(mMap.addMarker(options));
                }else{
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("P");
//
//                    // check if there are already the same number of markers, we clear the map.
                    markers.add(mMap.addMarker(options));
                }

                if (markers.size() == POLYGON_SIDES)
                {
                    drawShape();
                    drawLine();
                }

            }



            private void drawLine() {
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

//                line = mMap.addPolyline(options);
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
                    options.add(markers.get(i).getPosition());
//                    sourcePoints.add(markers.get(i).getPosition());

                }
//                Projection projection = mMap.getProjection();
//                ArrayList<Point> screenPoints = new ArrayList<>(sourcePoints.size());
//                for (LatLng location : sourcePoints) {
//                    Point p = projection.toScreenLocation(location);
//                    screenPoints.add(p);
//                }

//                ArrayList<Point> convexHullPoints = convexHull(screenPoints);
//                ArrayList<LatLng> convexHullLocationPoints = new ArrayList(convexHullPoints.size());
//                for (Point screenPoint : convexHullPoints) {
//                    LatLng location = projection.fromScreenLocation(screenPoint);
//                    convexHullLocationPoints.add(location);
//                }
//
//                for (LatLng latLng : convexHullLocationPoints) {
//                    options.add(latLng);
//                }
                mMap.addPolygon(options);
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

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
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