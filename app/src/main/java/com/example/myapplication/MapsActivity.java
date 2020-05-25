package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleMap mMap;
    //to show the current location
    private GoogleApiClient googleApiClient;
    //to update the location
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_code = 99;
    private double latitude, longtitude;
    private int ProximityRadius = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }





    public void onClick(View v)
    {
        String hospital = "hospital", pharmacy = "pharmacy", doctor = "doctor", dentist="dentist",atm="atm";
        Object transferData[] = new Object[2];
        //craeting an object of GetNearby_places class
        GetNearby_Places getNearby_places = new GetNearby_Places();



        switch (v.getId())
        {
            case R.id.search_area:
                EditText addressField = findViewById(R.id.location_search);
                String address = addressField.getText().toString();

                List<Address> addressList = null;
                MarkerOptions userMarkerOptions = new MarkerOptions();

                if (!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try
                    {
                        //top 6
                        addressList = geocoder.getFromLocationName(address,6);

                        if (addressList != null)
                        {
                            //loop for every addresslist
                            for (int i=0; i<addressList.size();i++)
                            {
                                Address userAddress = addressList.get(i);
                                //for every address get the langtitude and longtitude
                                LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                //set the icon
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                mMap.addMarker(userMarkerOptions);

                                //camera movement
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                //the more the zoom
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                        else
                        {
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Toast.makeText(this, "Please write any location name", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.hospital_nearby:
                mMap.clear();
                String url = getUrl(latitude,longtitude,hospital);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearby_places.execute(transferData);
                Toast.makeText(this, "Searching for nearby hospitals", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing for nearby hospitals", Toast.LENGTH_SHORT).show();
                break;

            case R.id.medicine_nearby:
                mMap.clear();
                url = getUrl(latitude, longtitude, pharmacy);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearby_places.execute(transferData);
                Toast.makeText(this, "Searching for nearby pharmacy", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing for nearby pharmacy", Toast.LENGTH_SHORT).show();
                break;

            case R.id.doctor_nearby:
                mMap.clear();
                url = getUrl(latitude,longtitude,doctor);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearby_places.execute(transferData);
                Toast.makeText(this, "Searching for nearby doctor clinic", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing for nearby doctor clinic", Toast.LENGTH_SHORT).show();
                break;

            case R.id.dentist_nearby:
                mMap.clear();
                url = getUrl(latitude,longtitude,dentist);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearby_places.execute(transferData);
                Toast.makeText(this, "Searching for nearby dentist", Toast.LENGTH_SHORT).show();

                Toast.makeText(this, "Showing for nearby dentist", Toast.LENGTH_SHORT).show();
                break;


            case R.id.atm_nearby:
                mMap.clear();
                url = getUrl(latitude,longtitude,atm);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearby_places.execute(transferData);
                Toast.makeText(this, "Searching for nearby atm", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing for nearby atm", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private String getUrl(double latitude, double longtitude, String nearbyplace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + ","+longtitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyplace);
        googleURL.append("&sensor=true");
        googleURL.append("&key="+"AIzaSyCDn_QR7V63UUZWf7h3_TM57YlCoXzAbWE");

        Log.d("GoogleMapsActivity", "url = " +googleURL.toString());

        return googleURL.toString();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }


    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_code);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case Request_User_Location_code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                    else
                    {
                        Toast.makeText(this, "Permission is not granted..", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
        }
    }

    protected synchronized void buildGoogleApiClient ()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();

        lastLocation = location;

        if (currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");
        //set the icon
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        //display the current loction as marker
        currentUserLocationMarker = mMap.addMarker(markerOptions);

        //camera movement
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //the more the zoom
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));

        if (googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        //update the location continuously

        locationRequest = new LocationRequest();
        //after 1100 ml second update location
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
