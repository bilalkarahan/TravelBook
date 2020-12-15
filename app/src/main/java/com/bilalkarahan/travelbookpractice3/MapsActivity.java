package com.bilalkarahan.travelbookpractice3;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain = new Intent(MapsActivity.this,MainActivity.class);
        startActivity(intentToMain);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("new")) {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                SharedPreferences sharedPreferences = getSharedPreferences("com.bilalkarahan.travelbookpractice3", MODE_PRIVATE);
                boolean trackBoolean = sharedPreferences.getBoolean("userLocation",false);

                if(!trackBoolean) {

                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));

                    sharedPreferences.edit().putBoolean("userLocation",true).apply();

                }
            }
        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {

                LatLng userLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 13));
                mMap.addMarker(new MarkerOptions().position(userLastLocation).title("You are here!"));

            }

        }

        } else {
            //Sqlite Data

            mMap.clear();
            int placeId = intent.getIntExtra("placeId",1);
            database = this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM places WHERE id = ?", new String[] {String.valueOf(placeId)});

            int addressIx = cursor.getColumnIndex("address");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx = cursor.getColumnIndex("longitude");

            while (cursor.moveToNext()) {

                Double latitudeFromDatabase = Double.parseDouble(cursor.getString(latitudeIx));
                Double longitudeFromDatabase = Double.parseDouble(cursor.getString(longitudeIx));
                String address = cursor.getString(addressIx);

                LatLng latLng = new LatLng(latitudeFromDatabase,longitudeFromDatabase);

                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

            }

            cursor.close();

        }


        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1 && grantResults.length > 0) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Intent intent = getIntent();
                String info = intent.getStringExtra("info");

                if(info.matches("new")) {

                    Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(lastlocation != null) {

                        LatLng userLastLocation = new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,13));

                    }

                } else {
                    //Sqlite Data

                    mMap.clear();
                    int placeId = intent.getIntExtra("placeId",1);
                    database = this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
                    Cursor cursor = database.rawQuery("SELECT * FROM places WHERE id = ?", new String[] {String.valueOf(placeId)});

                    int addressIx = cursor.getColumnIndex("address");
                    int latitudeIx = cursor.getColumnIndex("latitude");
                    int longitudeIx = cursor.getColumnIndex("longitude");

                    while (cursor.moveToNext()) {

                        Double latitudeFromDatabase = Double.parseDouble(cursor.getString(latitudeIx));
                        Double longitudeFromDatabase = Double.parseDouble(cursor.getString(longitudeIx));
                        String address = cursor.getString(addressIx);

                        LatLng latLng = new LatLng(latitudeFromDatabase,longitudeFromDatabase);

                        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

                    }

                    cursor.close();

                }

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("new")) {

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            String address = "";

            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList != null && addressList.size() > 0) {

                    if (addressList.get(0).getThoroughfare() != null) {

                        address += addressList.get(0).getThoroughfare();
                        address += " ";

                    }

                    if (addressList.get(0).getSubThoroughfare() != null) {

                        address += addressList.get(0).getSubThoroughfare();

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMap.clear();

            mMap.addMarker(new MarkerOptions().title(address).position(latLng));

            final Double latitude = latLng.latitude;
            final Double longitude = latLng.longitude;

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(address);
            alert.setCancelable(false);
            alert.setMessage("Are you sure?");
            final String finalAddress = address;
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {

                        database = MapsActivity.this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS places (id INTEGER PRIMARY KEY, address VARCHAR, latitude VARCHAR, longitude VARCHAR)");

                        String sqlString = "INSERT INTO places (address, latitude, longitude) VALUES(?, ?, ?)";
                        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                        sqLiteStatement.bindString(1, finalAddress);
                        sqLiteStatement.bindDouble(2, latitude);
                        sqLiteStatement.bindDouble(3, longitude);
                        sqLiteStatement.execute();

                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();

                }
            });

            alert.show();
        }
    }
}
