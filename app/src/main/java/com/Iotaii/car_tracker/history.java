package com.Iotaii.car_tracker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

public class history extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private final String mapId = "streets-v2";
    private final String apiKey = "O8hzf5l378NIwtGVcvEF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Mapbox.getInstance(this); // Replace with your actual Mapbox access token
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapView = findViewById(R.id.MapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                history.this.mapboxMap = mapboxMap;
                mapboxMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + apiKey));

                drawPolyline();
            }
        });
    }

    private void drawPolyline() {
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(22.5726, 88.3639));
        points.add(new LatLng(22.5736, 88.3649));
        points.add(new LatLng(22.5746, 88.3659));
        points.add(new LatLng(22.5756, 88.3669));

        int tealBlueColor = Color.rgb(54, 117, 136);

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(points)
                .color(tealBlueColor)
                .width(5f);

        Polyline polyline = mapboxMap.addPolyline(polylineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();

        int padding = 100;
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    // Other lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
