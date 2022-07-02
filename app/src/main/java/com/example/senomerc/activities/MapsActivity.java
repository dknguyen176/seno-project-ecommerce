package com.example.senomerc.activities;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    List<Store> stores;
    List<Marker> markers;

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

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);

        stores = new ArrayList<>();
        markers = new ArrayList<>();

        stores.add(new Store(10.762910922087965, 106.68217050135999, "Store 1"));
        stores.add(new Store(10.875651138778734, 106.79916558196608, "Store 2"));

        List<String> names = new ArrayList<>();

        for (int i = 0; i < stores.size(); ++i) {
            LatLng coordinate = new LatLng(stores.get(i).getLatitude(), stores.get(i).getLongitude());
            if (i == 0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15.0f));
            markers.add(mMap.addMarker(new MarkerOptions()
                    .title(stores.get(i).getName())
                    .position(coordinate)
                    .draggable(false)
                    .alpha(1f)));
            names.add(stores.get(i).getName());
        }

        String[] items = names.toArray(new String[0]);
        Spinner dropdown = findViewById(R.id.locationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(position).getPosition(), 15.0f));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}