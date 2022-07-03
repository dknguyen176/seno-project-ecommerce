package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.senomerc.R;
import com.example.senomerc.databinding.ActivityMapsBinding;
import com.example.senomerc.model.Store;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    List<Store> stores;
    List<Marker> markers;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);

        stores = new ArrayList<>();
        markers = new ArrayList<>();

        stores.add(new Store(10.762910922087965, 106.68217050135999, "Store 1", "227 Nguyen Van Cu, Phuong 4, Quan 5, TPHCM"));
        stores.add(new Store(10.875651138778734, 106.79916558196608, "Store 2", "VQGX+7M3, Phuong Linh Trung, TP Thu Duc, TPHCM"));

        List<String> names = new ArrayList<>();

        for (int i = 0; i < stores.size(); ++i) {
            LatLng coordinate = new LatLng(stores.get(i).getLatitude(), stores.get(i).getLongitude());
            if (i == 0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17.0f));
            names.add(stores.get(i).getName());
            markers.add(mMap.addMarker(new MarkerOptions()
                    .title(stores.get(i).getName())
                    .position(coordinate)
                    .draggable(false)
                    .alpha(1f)));
        }

        Spinner dropdown = findViewById(R.id.locationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names.toArray(new String[0]));
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(position).getPosition(), 17.0f));
                TextView textView = findViewById(R.id.addressText);
                textView.setText(stores.get(position).getAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20.0f));
        Spinner dropdown = findViewById(R.id.locationSpinner);
        for(int i = 0; i < markers.size(); ++i){
            if (marker == markers.get(i)){
                dropdown.setSelection(i);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.menu_cart) {
            startActivity(new Intent(MapsActivity.this, CartActivity.class));
        }*/

        /*if (id == R.id.map) {
            startActivity(new Intent(MapsActivity.this, MapsActivity.class));
        }*/

        return true;
    }
}