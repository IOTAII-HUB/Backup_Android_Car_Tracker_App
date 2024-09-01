package com.Iotaii.car_tracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class travelsummary extends AppCompatActivity {

    private ImageView back;
    private ImageView date;
    private TextView fromDateText;
    private TextView toDateText;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private List<Vehicle> vehicleList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_travelsummary);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Handling insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        fromDateText = findViewById(R.id.from_date_text);
        toDateText = findViewById(R.id.to_date_text);
        date = findViewById(R.id.date);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recycler_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter(vehicleList);
        recyclerView.setAdapter(mAdapter);

        // Add sample data
        prepareVehicleData();

        // Set click listeners
        fromDateText.setOnClickListener(v -> showDatePickerDialog(true));
        toDateText.setOnClickListener(v -> showDatePickerDialog(false));
        back.setOnClickListener(v -> navigateBack());

        // Setting the status bar color for Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_light));
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return WindowInsetsCompat.CONSUMED;
                });
            }
        }
    }

    private void prepareVehicleData() {
        //vehicleList.add(new Vehicle("BR06PE6292", "28.55 Km", "1H 45M", "11H 50M", "25M", "3"));
        //vehicleList.add(new Vehicle("BR06PE6287", "27.55 Km", "1H 45M", "11H 50M", "25M", "4"));
        //vehicleList.add(new Vehicle("BR06PE6286", "26.55 Km", "45M", "12H 50M", "26M", "5"));
        // Add more vehicle data here

        mAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(boolean isFromDate) {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                travelsummary.this,
                (view, year1, month1, dayOfMonth) -> {
                    // Handle the date selected
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    if (isFromDate) {
                        fromDateText.setText("From Date:\n" + selectedDate);
                        fromDateText.setTextColor(getResources().getColor(R.color.app_theme_light)); // Change text color to blue
                        fromDateText.setTypeface(null, Typeface.BOLD); // Change text style to bold
                    } else {
                        toDateText.setText("To Date:\n" + selectedDate);
                        toDateText.setTextColor(getResources().getColor(R.color.green)); // Change text color to green
                        toDateText.setTypeface(null, Typeface.BOLD); // Change text style to bold
                    }
                    Toast.makeText(travelsummary.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void navigateBack() {
        Intent intent = new Intent(travelsummary.this, dashboard.class);
        startActivity(intent);
    }

    // Function to update vehicle data
    public void updateVehicleData(List<Vehicle> newVehicleList) {
        vehicleList.clear();
        vehicleList.addAll(newVehicleList);
        mAdapter.notifyDataSetChanged();
    }
}
