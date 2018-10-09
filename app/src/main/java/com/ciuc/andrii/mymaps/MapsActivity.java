package com.ciuc.andrii.mymaps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;

import Modules.GPSTracker;
import Modules.RouteManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;
    private Button btn_find;
    private Button btn_type;
    private EditText edit_find;
    private ProgressDialog progressDialog;
    private RouteManager routeManager;
    private GPSTracker gpsTracker;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Мої координати
        gpsTracker = new GPSTracker(MapsActivity.this);

        double myLat = 0;
        double myLong = 0;

        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }else{
            myLat = gpsTracker.getLatitude();
            myLong = gpsTracker.getLongitude();
        }
        myLocation = new LatLng(myLat, myLong);

        btn_find = findViewById(R.id.btn_find);
        btn_type = findViewById(R.id.btn_type);
        edit_find = findViewById(R.id.edit_Find);

        // Шукати адресу
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = edit_find.getText().toString();
                List<Address> addressList = null;
                if (!location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
                    InputMethodManager imm = (InputMethodManager) MapsActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        // Змінити тип карти
        btn_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                else
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        routeManager = new RouteManager(MapsActivity.this,progressDialog,mMap);

        LatLng MyHome = new LatLng(48.252328, 25.930814);
        LatLng BusStation = new LatLng(48.283245, 25.972690);
        // Створення на малювання маршруту між двома точками
        routeManager.sendRequest(BusStation,MyHome);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        googleMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("myLocation").icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,13));
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MapsActivity.this,"lat :" + myLocation.latitude + ", long:"+myLocation.longitude,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
