package com.Iotaii.car_tracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.Iotaii.car_tracker.R;

public class veichle extends AppCompatActivity {

    private EditText vehicleNameEditText;
    private EditText vehicleAliasEditText;
    private Button submitButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "VehiclePrefs";
    private static final String KEY_VEHICLE_NAME = "vehicle_name";
    private static final String KEY_VEHICLE_ALIAS = "vehicle_alias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_veichle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vehicleNameEditText = findViewById(R.id.veichlename);
        vehicleAliasEditText = findViewById(R.id.veichlealias);
        submitButton = findViewById(R.id.button);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load saved data if available
        loadSavedData();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicleName = vehicleNameEditText.getText().toString();
                String vehicleAlias = vehicleAliasEditText.getText().toString();

                if (vehicleName.isEmpty() || vehicleAlias.isEmpty()) {
                    Toast.makeText(veichle.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save data to SharedPreferences
                    saveData(vehicleName, vehicleAlias);
                    Toast.makeText(veichle.this, "Vehicle saved locally:\nName: " + vehicleName + "\nAlias: " + vehicleAlias, Toast.LENGTH_SHORT).show();
                }
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(veichle.this, dashboard.class);
                startActivity(intent);
            }
        });
    }

    private void saveData(String vehicleName, String vehicleAlias) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_VEHICLE_NAME, vehicleName);
        editor.putString(KEY_VEHICLE_ALIAS, vehicleAlias);
        editor.apply();
    }

    private void loadSavedData() {
        String savedVehicleName = sharedPreferences.getString(KEY_VEHICLE_NAME, "");
        String savedVehicleAlias = sharedPreferences.getString(KEY_VEHICLE_ALIAS, "");

        vehicleNameEditText.setText(savedVehicleName);
        vehicleAliasEditText.setText(savedVehicleAlias);
    }
}
