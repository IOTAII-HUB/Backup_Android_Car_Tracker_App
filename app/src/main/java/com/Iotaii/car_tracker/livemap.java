//package com.Iotaii.car_tracker;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.annotations.Marker;
//import com.mapbox.mapboxsdk.annotations.MarkerOptions;
//import com.mapbox.mapboxsdk.camera.CameraPosition;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.maps.MapView;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
//import com.mapbox.mapboxsdk.maps.Style;
//
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class livemap extends AppCompatActivity implements LocationListener {
//
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
//    private MapView mapView;
//    private LocationManager locationManager;
//    private MapboxMap mapboxMap;
//    private Marker userMarker;
//    private final String mapId = "streets-v2";
//    private final String apiKey = "O8hzf5l378NIwtGVcvEF";
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Mapbox.getInstance(this); // Initialize Mapbox with API key
//        setContentView(R.layout.activity_livemap);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_light));
//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//                return insets;
//            });
//        }
//
//        Intent intent = getIntent();
//        String vehicleId = intent.getStringExtra("vehicleId");
//        String username = intent.getStringExtra("username");
//
//        TextView vehicleTextView = findViewById(R.id.textView5);
//        vehicleTextView.setText("Vehicle ID: " + vehicleId);
//
//        mapView = findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(MapboxMap mapboxMap) {
//                livemap.this.mapboxMap = mapboxMap;
//                mapboxMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + apiKey), style -> {
//                    // Fetch location and vehicle details from APIs
//                    fetchAndDisplayLocationFromApi(vehicleId, username);
//                    fetchAndDisplayVehicleDetails(vehicleId, username);
//                });
//            }
//        });
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        }
//    }
//
//    private void fetchAndDisplayLocationFromApi(String vehicleId, String username) {
//        String urlString = "http://192.168.29.64:5454/vehicles/location?vehicleId=" + vehicleId + "&username=" + username;
//
//        new Thread(() -> {
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String inputLine;
//                    StringBuilder content = new StringBuilder();
//
//                    while ((inputLine = in.readLine()) != null) {
//                        content.append(inputLine);
//                    }
//
//                    in.close();
//
//                    // Parse the JSON response
//                    JSONObject jsonResponse = new JSONObject(content.toString());
//                    double latitude = jsonResponse.optDouble("latitude", 0);
//                    double longitude = jsonResponse.optDouble("longitude", 0);
//
//                    // Log the coordinates to the console
//                    System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
//
//                    // Update the marker on the map with the retrieved coordinates
//                    runOnUiThread(() -> {
//                        LatLng vehicleLocation = new LatLng(latitude, longitude);
//
//                        if (userMarker != null) {
//                            // Update the marker's position instead of removing and adding a new one
//                            userMarker.setPosition(vehicleLocation);
//                        } else {
//                            // If the marker doesn't exist, create it
//                            userMarker = mapboxMap.addMarker(new MarkerOptions().position(vehicleLocation).title("Vehicle Location"));
//                        }
//
//                        // Update the camera position to center on the new location
//                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
//                                .target(vehicleLocation)
//                                .zoom(12) // Adjust the zoom level as needed
//                                .build());
//                    });
//
//                } else {
//                    System.err.println("Failed to fetch location data. Response code: " + responseCode);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    private void fetchAndDisplayVehicleDetails(String vehicleId, String username) {
//        String urlString = "http://192.168.29.64:5454/vehicles/details/columns?vehicleId=" + vehicleId + "&username=" + username;
//
//        new Thread(() -> {
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String inputLine;
//                    StringBuilder content = new StringBuilder();
//
//                    while ((inputLine = in.readLine()) != null) {
//                        content.append(inputLine);
//                    }
//
//                    in.close();
//
//                    // Parse the JSON response
//                    JSONObject jsonResponse = new JSONObject(content.toString());
//                    double todayKm = jsonResponse.optDouble("today_km", 0);
//                    double fromLastStop = jsonResponse.optDouble("from_last_stop", 0);
//                    int totalDuration = jsonResponse.optInt("total_duration", 0);
//                    double totalDistance = jsonResponse.optDouble("distance", 0);
//
//                    // Log the details to the console
//                    System.out.println("Today's Km: " + todayKm);
//                    System.out.println("From Last Stop: " + fromLastStop);
//                    System.out.println("Total Duration: " + totalDuration);
//                    System.out.println("Total Distance: " + totalDistance);
//
//                    // Update the TextViews with the retrieved details
//                    runOnUiThread(() -> {
//                        TextView todayKmTextView = findViewById(R.id.today_km_value);
//                        TextView fromLastStopTextView = findViewById(R.id.from_last_stop_km_value);
//                        TextView totalDurationTextView = findViewById(R.id.total_duration_value);
//                        TextView totalDistanceTextView = findViewById(R.id.total_distance_value);
//
//                        todayKmTextView.setText(todayKm + " km");
//                        fromLastStopTextView.setText(fromLastStop + " km");
//                        totalDurationTextView.setText(totalDuration + " min"); // Adjust format as needed
//                        totalDistanceTextView.setText(totalDistance + " km");
//                    });
//
//                } else {
//                    System.err.println("Failed to fetch vehicle details. Response code: " + responseCode);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        // No need to update the location manually as it will be updated by the API
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        // Deprecated method, but required for LocationListener interface
//    }
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
//        // Handle provider enabled state
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
//        // Handle provider disabled state
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
//        if (mapView != null) {
//            mapView.onDestroy();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mapView != null) {
//            mapView.onStart();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mapView != null) {
//            mapView.onResume();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mapView != null) {
//            mapView.onPause();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mapView != null) {
//            mapView.onStop();
//        }
//    }
//}

package com.Iotaii.car_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class livemap extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private MapView mapView;
    private LocationManager locationManager;
    private MapboxMap mapboxMap;
    private Marker userMarker;
    private final String mapId = "streets-v2";
    private final String apiKey = "O8hzf5l378NIwtGVcvEF";
    private TextView addressTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this); // Initialize Mapbox with API key
        setContentView(R.layout.activity_livemap);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_light));
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        Intent intent = getIntent();
        String vehicleId = intent.getStringExtra("vehicleId");
        String username = intent.getStringExtra("username");

        TextView vehicleTextView = findViewById(R.id.textView5);
        vehicleTextView.setText("Vehicle ID: " + vehicleId);

        addressTextView = findViewById(R.id.location_text); // Initialize address TextView

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                livemap.this.mapboxMap = mapboxMap;
                mapboxMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + apiKey), style -> {
                    // Fetch location and vehicle details from APIs
                    fetchAndDisplayLocationFromApi(vehicleId, username);
                    fetchAndDisplayVehicleDetails(vehicleId, username);
                });
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    private void fetchAndDisplayLocationFromApi(String vehicleId, String username) {
        String urlString = "http://192.168.29.64:5454/vehicles/location?vehicleId=" + vehicleId + "&username=" + username;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    in.close();

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(content.toString());
                    double latitude = jsonResponse.optDouble("latitude", 0);
                    double longitude = jsonResponse.optDouble("longitude", 0);

                    // Log the coordinates to the console
                    System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);

                    // Update the marker on the map with the retrieved coordinates
                    runOnUiThread(() -> {
                        LatLng vehicleLocation = new LatLng(latitude, longitude);

                        if (userMarker != null) {
                            // Update the marker's position instead of removing and adding a new one
                            userMarker.setPosition(vehicleLocation);
                        } else {
                            // If the marker doesn't exist, create it
                            userMarker = mapboxMap.addMarker(new MarkerOptions().position(vehicleLocation).title("Vehicle Location"));
                        }

                        // Update the camera position to center on the new location
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(vehicleLocation)
                                .zoom(12) // Adjust the zoom level as needed
                                .build());

                        // Reverse geocode the coordinates to get the address
                        updateAddressFromLocation(latitude, longitude);
                    });

                } else {
                    System.err.println("Failed to fetch location data. Response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateAddressFromLocation(double latitude, double longitude) {
        runOnUiThread(() -> {
            try {
                Geocoder geocoder = new Geocoder(livemap.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder addressString = new StringBuilder();

                    // Concatenate the address components
                    if (address.getFeatureName() != null) addressString.append(address.getFeatureName()).append(", ");
                    if (address.getThoroughfare() != null) addressString.append(address.getThoroughfare()).append(", ");
                    if (address.getLocality() != null) addressString.append(address.getLocality()).append(", ");
                    if (address.getAdminArea() != null) addressString.append(address.getAdminArea()).append(", ");
                    if (address.getCountryName() != null) addressString.append(address.getCountryName());

                    // Set the address to the TextView
                    addressTextView.setText(addressString.toString());
                } else {
                    addressTextView.setText("No address found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                addressTextView.setText("Error retrieving address.");
            }
        });
    }

    private void fetchAndDisplayVehicleDetails(String vehicleId, String username) {
        String urlString = "http://192.168.29.64:5454/vehicles/details/columns?vehicleId=" + vehicleId + "&username=" + username;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    in.close();

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(content.toString());
                    double todayKm = jsonResponse.optDouble("today_km", 0);
                    double fromLastStop = jsonResponse.optDouble("from_last_stop", 0);
                    int totalDuration = jsonResponse.optInt("total_duration", 0);
                    double totalDistance = jsonResponse.optDouble("distance", 0);

                    // Log the details to the console
                    System.out.println("Today's Km: " + todayKm);
                    System.out.println("From Last Stop: " + fromLastStop);
                    System.out.println("Total Duration: " + totalDuration);
                    System.out.println("Total Distance: " + totalDistance);

                    // Update the TextViews with the retrieved details
                    runOnUiThread(() -> {
                        TextView todayKmTextView = findViewById(R.id.today_km_value);
                        TextView fromLastStopTextView = findViewById(R.id.from_last_stop_km_value);
                        TextView totalDurationTextView = findViewById(R.id.total_duration_value);
                        TextView totalDistanceTextView = findViewById(R.id.total_distance_value);

                        todayKmTextView.setText(todayKm + " km");
                        fromLastStopTextView.setText(fromLastStop + " km");
                        totalDurationTextView.setText(totalDuration + " min"); // Adjust format as needed
                        totalDistanceTextView.setText(totalDistance + " km");
                    });

                } else {
                    System.err.println("Failed to fetch vehicle details. Response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Optionally handle location updates if needed
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }
}
