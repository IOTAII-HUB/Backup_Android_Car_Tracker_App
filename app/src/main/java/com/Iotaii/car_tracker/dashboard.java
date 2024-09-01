package com.Iotaii.car_tracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class dashboard extends AppCompatActivity {
    //private static int selectedVehicleId = -1;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuButton;
    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private CardView animatedCardView;
    private boolean isCardViewVisible = false;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private ImageView report;
    private EditText searchEditText;
    private ImageView service;
    private ImageView lock;
    private ImageView parking;
    private ImageView nearby;
    private ImageView history;
    private ImageView noti;
    private ImageView locationImageView;
    private View currentSelectedButton;
    private ImageView share;
    private ImageView drive;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Retrieve the username from the intent
        Intent intent1 = getIntent();
        String username = intent1.getStringExtra("username");

        System.out.println(username);

        fetchVehicleId(username);
        fetchAndLogUserData(username);
        // Initialize views
        animatedCardView = findViewById(R.id.animatedCardView);
        ImageView slideImageView = findViewById(R.id.slide);
        locationImageView = findViewById(R.id.location);
        noti = findViewById(R.id.notifications);
        drive = findViewById(R.id.driver);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        report = findViewById(R.id.report);
        searchEditText = findViewById(R.id.magnifying); // Initialize the EditText
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        service = findViewById(R.id.service);
        lock = findViewById(R.id.lock);
        parking = findViewById(R.id.parking);
        nearby = findViewById(R.id.find_location);
        history = findViewById(R.id.history);
        share= findViewById(R.id.share);
        itemList = new ArrayList<>();

        setButtonText(R.id.button_all, "All", "2");
        setButtonText(R.id.button_overspeed, "Overspeed", "0");
        setButtonText(R.id.button_running, "Running", "0");
        setButtonText(R.id.button_idle, "Idle", "0");
        setButtonText(R.id.button_stop, "Stop", "2");

        setupButtonClickListeners();
        applyButtonStateDrawable();

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Simulate data loading
        loadData(username);

        // Add TextWatcher to the EditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list as the user types
                itemAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        // Set up click listener for Report
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate custom layout
                LayoutInflater inflater = LayoutInflater.from(dashboard.this);
                View dialogView = inflater.inflate(R.layout.dialog_report_options, null);
                // Create an AlertDialog with the custom layout
                AlertDialog alertDialog = new AlertDialog.Builder(dashboard.this)
                        .setView(dialogView)
                        //.setTitle("Choose Report")
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                // Set up click listeners for the dialog options
                dialogView.findViewById(R.id.statusReports).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle Status Reports click
                        showFirstDialog();
                    }
                });

                dialogView.findViewById(R.id.locate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle Locate click
                        showsecondDialog();
                    }
                });

                dialogView.findViewById(R.id.kmSummary).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle KM Summary click
                        Intent intent = new Intent(dashboard.this, km.class);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                });

                dialogView.findViewById(R.id.travelSummary).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle Travel Summary click
                        Intent intent = new Intent(dashboard.this, travelsummary.class);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });

        // Set up Click Listener for Nearby
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflate = LayoutInflater.from(dashboard.this);
                View dialog = inflate.inflate(R.layout.nearby_layout, null);
                AlertDialog alertDialog = new AlertDialog.Builder(dashboard.this)
                        .setView(dialog)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                alertDialog.show(); // Make sure to call show() to display the dialog
            }
        });
        TextView neartxt = findViewById(R.id.txt_find_location);
        neartxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflate = LayoutInflater.from(dashboard.this);
                View dialog = inflate.inflate(R.layout.nearby_layout, null);
                AlertDialog alertDialog = new AlertDialog.Builder(dashboard.this)
                        .setView(dialog)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                alertDialog.show(); // Make sure to call show() to display the dialog
            }
        });

        // Set up click listener for slideImageView
        slideImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCardViewVisible) {
                    animatedCardViewUpwards();
                } else {
                    animatedCardViewDownwards();
                }
                isCardViewVisible = !isCardViewVisible;
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(dashboard.this).inflate(R.layout.share_time, null);

                TextView dialogTitle = dialogView.findViewById(R.id.tv_vehicle_id);
                ImageView right = dialogView.findViewById(R.id.iv_confirm);
                ImageView wrong = dialogView.findViewById(R.id.iv_cancel);
                RadioGroup radioGroup = dialogView.findViewById(R.id.rg_share_location_options);

                dialogTitle.setText("Vehicle No.");

                // Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
                builder.setView(dialogView);

                // Create and show the AlertDialog
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                            String shareContent = selectedRadioButton.getText().toString();
                            shareLocation(shareContent);
                        }
                        alertDialog.dismiss();
                    }
                });

                wrong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                View dialogView = LayoutInflater.from(dashboard.this).inflate(R.layout.custom_dialog, null);

                // Initialize views inside custom layout
                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
                Button positiveButton = dialogView.findViewById(R.id.positive_button);
                Button negativeButton = dialogView.findViewById(R.id.negative_button);

                // Set title and message
                dialogTitle.setText("Parking Alert");
                dialogMessage.setText("Do you want to park here?");

                // Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
                builder.setView(dialogView);

                // Create and show the AlertDialog
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Set onClickListeners for buttons
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle positive button click
                        // Example: Toast.makeText(MainActivity.this, "Parking confirmed!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss(); // Dismiss the dialog
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle negative button click
                        // Example: Toast.makeText(MainActivity.this, "Parking canceled.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss(); // Dismiss the dialog
                    }
                });
            }
        });

        // Set screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuButton = findViewById(R.id.iv_menu);

        // Set up click listener for menu button
        menuButton.setOnClickListener(view -> toggleDrawer());

        // Set up the navigation view
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.livemap) {
                // Live map screen
                Intent intent = new Intent(this, livemap.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.km_summary) {
                // Kilometer summary screen
                Intent intent = new Intent(this, km.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.subscription) {
                // Subscription screen
                Intent intent = new Intent(this, subscription.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.drivers) {
                // Drivers screen
                Intent intent = new Intent(this, drivers.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.contact) {
                // Contact screen
                Intent intent = new Intent(this, contact.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.raise) {
                // Raise screen
                Intent intent = new Intent(this, raise.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.settings) {
                // Settings screen
                Intent intent = new Intent(this, settings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.privacy) {
                // Privacy screen
                Intent intent = new Intent(this, privacy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.signout) {
                // Signout
                Intent intent = new Intent(dashboard.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity(); // Close all activities and exit the app
            }

            // Handle navigation item clicks here
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Apply edge-to-edge display
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

        // Update vehicle profile TextView
        updateVehicleProfile();
    }

    private void fetchAndLogUserData(String username) {
        System.out.println(username);
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL
                URL url = new URL("http://3.109.34.34:5454/vehicles/" + username);
                Log.d("UserData", "Request URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000); // Set timeout for the connection

                // Get the response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder response = new StringBuilder();
                    if (inputStream != null) {
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    // Log the entire response for debugging
                    Log.d("UserData", "Response: " + response.toString());

                    // Parse the JSON response
                    JSONArray vehiclesArray = new JSONArray(response.toString());
                    for (int i = 0; i < vehiclesArray.length(); i++) {
                        JSONObject vehicle = vehiclesArray.getJSONObject(i);
                        int vehicleId = vehicle.getInt("vehicleId");
                        int speed = vehicle.getInt("speed");
                        int distance = vehicle.getInt("distance");
                        int fuelUsed = vehicle.getInt("fuelUsed");
                        String location = vehicle.getString("location");
                        int totalDuration = vehicle.getInt("totalDuration");
                        String user = vehicle.getString("username");

                        // Log the parsed data
                        Log.d("VehicleData", "Vehicle ID: " + vehicleId + ", Speed: " + speed +
                                ", Distance: " + distance + ", Fuel Used: " + fuelUsed +
                                ", Location: " + location + ", Total Duration: " + totalDuration +
                                ", Username: " + user);
                        //System.out.println("++++++" + vehicleId);
                    }

                    // Show a Toast message to indicate data retrieval (optional)
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(getApplicationContext(), "Data retrieved for " + username, Toast.LENGTH_SHORT).show());
                } else {
                    Log.d("UserData", "GET request failed: " + responseCode);
                }
            } catch (Exception e) {
                Log.d("UserData", "Exception: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.d("UserData", "Error closing stream: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    public void fetchVehicleId(String username) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL
                URL url = new URL("http://3.109.34.34:5454/vehicles/ids/" + username);
                System.out.println("Request URL: " + url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000); // Set timeout for the connection

                // Get the response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder response = new StringBuilder();
                    if (inputStream != null) {
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    // Log the entire response for debugging
                    System.out.println("Response: " + response.toString());

                    // Parse the JSON response
                    JSONArray vehicleIdsArray = new JSONArray(response.toString());
                    for (int i = 0; i < vehicleIdsArray.length(); i++) {
                        int vehicleId = vehicleIdsArray.getInt(i);
                        // Log the vehicle ID
                        System.out.println("Vehicle ID: " + vehicleId);
                    }
                } else {
                    System.out.println("GET request failed: " + responseCode);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        System.out.println("Error closing stream: " + e.getMessage());
                    }
                }
            }
        }).start();
    }


    // Method to share location
    private void shareLocation(String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    //Locate alert Dialog View Finder
     private void showsecondDialog() {
         LayoutInflater inflater = getLayoutInflater();
         View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);

         TextView licensePlate = dialogView.findViewById(R.id.license_plate);
         TextView date = dialogView.findViewById(R.id.date);
         TextView time = dialogView.findViewById(R.id.time);
         ImageView correct = dialogView.findViewById(R.id.confirm_button);
         ImageView wrong = dialogView.findViewById(R.id.cancel_button);

         licensePlate.setText("BR06PF7466");

         date.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final Calendar calendar = Calendar.getInstance();
                 int year = calendar.get(Calendar.YEAR);
                 int month = calendar.get(Calendar.MONTH);
                 int day = calendar.get(Calendar.DAY_OF_MONTH);

                 DatePickerDialog datePickerDialog = new DatePickerDialog(dashboard.this, new DatePickerDialog.OnDateSetListener() {
                     @Override
                     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                         // Set the selected date to the EditText
                         date.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
                     }
                 }, year, month, day);

                 datePickerDialog.show();
             }
         });

         time.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final Calendar calendar = Calendar.getInstance();
                 int hour = calendar.get(Calendar.HOUR_OF_DAY);
                 int minute = calendar.get(Calendar.MINUTE);

                 TimePickerDialog timePickerDialog = new TimePickerDialog(dashboard.this, new TimePickerDialog.OnTimeSetListener() {
                     @Override
                     public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                         // Set the selected time to the EditText
                         time.setText(String.format("%02d:%02d", hourOfDay, minute));
                     }
                 }, hour, minute, true);

                 timePickerDialog.show();
             }
         });

         androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
         builder.setView(dialogView);

         final androidx.appcompat.app.AlertDialog showsecondDialog = builder.create();

         correct.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(dashboard.this, locate.class);
                 startActivity(intent);
                 showsecondDialog.dismiss(); // Dismiss the dialog when navigating
             }
         });

         wrong.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 showsecondDialog.dismiss(); // Dismiss the dialog when tapping the wrong button
             }
         });

         showsecondDialog.show();
     }

    //Dialog area swaping
    // Declare the secondDialog variable outside the method
    private androidx.appcompat.app.AlertDialog secondDialog;

    private void showFirstDialog() {
        // Inflate the layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);

        // Find the EditText fields in the inflated layout
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        EditText edittextdateto = dialogView.findViewById(R.id.editTexttoDate);
        EditText edittexttimeto = dialogView.findViewById(R.id.editTextToTime);
        ImageView correct = dialogView.findViewById(R.id.correct);
        ImageView wrong = dialogView.findViewById(R.id.wrong);

        // Set on click listener for correct
        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, report.class);
                startActivity(intent);
            }
        });

        // Set an OnClickListener to show the DatePickerDialogTo
        edittextdateto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date to set as default in the DatePicker
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(dashboard.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the EditText
                        edittextdateto.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Set an OnClickListener to show the TimePickerDialogTo
        edittexttimeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time to set as default in the TimePicker
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(dashboard.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time to the EditText
                        edittexttimeto.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        // Set an OnClickListener to show the DatePickerDialog
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date to set as default in the DatePicker
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(dashboard.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the EditText
                        editTextDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Set an OnClickListener to show the TimePickerDialog
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time to set as default in the TimePicker
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(dashboard.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time to the EditText
                        editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        // Create the AlertDialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);  // Set the inflated view to the dialog

        // Set the message or other properties if needed
        // builder.setMessage("This is the second dialog. Click a button to navigate to another page.");

        builder.setPositiveButton("Navigate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(dashboard.this, report.class);
                startActivity(intent);
                showFirstDialog();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        secondDialog = builder.create();
        secondDialog.show();

        wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondDialog.dismiss();
            }
        });
    }

    private void setButtonText(int buttonId, String text1, String text2) {
        View button = findViewById(buttonId);
        TextView textView1 = button.findViewById(R.id.text1);
        TextView textView2 = button.findViewById(R.id.text2);
        textView1.setText(text1);
        textView2.setText(text2);
    }

    private void setupButtonClickListeners() {
        findViewById(R.id.button_all).setOnClickListener(buttonClickListener);
        findViewById(R.id.button_overspeed).setOnClickListener(buttonClickListener);
        findViewById(R.id.button_running).setOnClickListener(buttonClickListener);
        findViewById(R.id.button_idle).setOnClickListener(buttonClickListener);
        findViewById(R.id.button_stop).setOnClickListener(buttonClickListener);
    }
    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentSelectedButton != null && currentSelectedButton != v) {
                resetButtonToDefault(currentSelectedButton);
            }
            applyPressedStateDrawable(v);
            currentSelectedButton = v;
        }
    };
    private void applyButtonStateDrawable() {
        applyDefaultStateDrawable(findViewById(R.id.button_all));
        applyDefaultStateDrawable(findViewById(R.id.button_overspeed));
        applyDefaultStateDrawable(findViewById(R.id.button_running));
        applyDefaultStateDrawable(findViewById(R.id.button_idle));
        applyDefaultStateDrawable(findViewById(R.id.button_stop));
    }
    private void applyDefaultStateDrawable(View button) {
        StateListDrawable drawable = new StateListDrawable();
        Drawable defaultState = getResources().getDrawable(getDefaultDrawableResId(button.getId()));

        drawable.addState(new int[]{}, defaultState);
        button.setBackground(drawable);
    }
    private void applyPressedStateDrawable(View button) {
        Drawable pressed = getResources().getDrawable(getPressedDrawableResId(button.getId()));
        button.setBackground(pressed);
    }
    private void resetButtonToDefault(View button) {
        applyDefaultStateDrawable(button);
    }
    private int getDefaultDrawableResId(int buttonId) {
        if (buttonId == R.id.button_all) {
            return R.drawable.button_all_default;
        } else if (buttonId == R.id.button_overspeed) {
            return R.drawable.button_overspeed_default;
        } else if (buttonId == R.id.button_running) {
            return R.drawable.button_running_default;
        } else if (buttonId == R.id.button_idle) {
            return R.drawable.button_idle_default;
        } else if (buttonId == R.id.button_stop) {
            return R.drawable.button_stop_default;
        } else {
            return R.drawable.button_all_default;
        }
    }

    private int getPressedDrawableResId(int buttonId) {
        if (buttonId == R.id.button_all) {
            return R.drawable.button_all_pressed;
        } else if (buttonId == R.id.button_overspeed) {
            return R.drawable.button_overspeed_pressed;
        } else if (buttonId == R.id.button_running) {
            return R.drawable.button_running_pressed;
        } else if (buttonId == R.id.button_idle) {
            return R.drawable.button_idle_pressed;
        } else if (buttonId == R.id.button_stop) {
            return R.drawable.button_stop_pressed;
        } else {
            return R.drawable.button_all_pressed;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load data with username when the activity resumes
        String username = getIntent().getStringExtra("username");
        loadData(username);
    }

    // Method to load data with a simulated delay
    private void loadData(String username) {
        new Handler().postDelayed(() -> {
            fetchVehicleIdsAndUpdateUI(username);
        }, 2000); // 2 seconds delay
    }

    private int selectedVehicleId = -1; // Default value indicating no vehicle is selected

    private void fetchVehicleIdsAndUpdateUI(String username) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                String urlString = String.format("http://3.109.34.34:5454/vehicles/ids/%s", username);
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder response = new StringBuilder();
                    if (inputStream != null) {
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    JSONArray vehicleIdsArray = new JSONArray(response.toString());
                    List<Integer> vehicleIds = new ArrayList<>();
                    List<Item> itemList = new ArrayList<>();
                    for (int i = 0; i < vehicleIdsArray.length(); i++) {
                        int vehicleId = vehicleIdsArray.getInt(i);
                        vehicleIds.add(vehicleId);
                        String speed = "Speed " + vehicleId;
                        String distance = "Distance " + vehicleId;
                        int imageResource = R.drawable.car;
                        String additionalText = "Additional Text " + vehicleId;

                        itemList.add(new Item(speed, distance, imageResource, additionalText));
                    }

                    runOnUiThread(() -> {
                        ItemAdapter itemAdapter = new ItemAdapter(itemList, vehicleIds, username);
                        recyclerView.setLayoutManager(new LinearLayoutManager(dashboard.this));
                        recyclerView.setAdapter(itemAdapter);

                        // Set up item click listener to store selected vehicle ID
                        itemAdapter.setOnItemClickListener(vehicleId -> {
                            selectedVehicleId = vehicleId;
                        });

                        // Set up click listener for Live Map
                        locationImageView.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, livemap.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.putExtra("username",username);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });
                        TextView loctext = findViewById(R.id.txt_location);
                        loctext.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, livemap.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.putExtra("username",username);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //set up on Click Listener for History
                        history.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, history.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.putExtra("username",username);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        TextView histxt = findViewById(R.id.txt_history);
                        histxt.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, history.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.putExtra("username",username);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Set up click listener for Notification
                        noti.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, notifications.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });
                        TextView notixt = findViewById(R.id.txt_notifications);
                        notixt.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, notifications.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Set up click listener for Drivers
                        drive.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, drivers.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });
                        TextView drivetxt = findViewById(R.id.txt_driver);
                        drivetxt.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, drivers.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Set up on Click Listener for Service
                        service.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, service.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        TextView servtxt = findViewById(R.id.txt_service);
                        servtxt.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, service.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Set up on Click Listener for Lock
                        lock.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, lock.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });
                        TextView locktxt = findViewById(R.id.txt_lock);
                        locktxt.setOnClickListener(v -> {
                            if (selectedVehicleId != -1) {
                                Intent intent = new Intent(dashboard.this, lock.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicleId", String.valueOf(selectedVehicleId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else {
                                Toast.makeText(dashboard.this, "No vehicle selected", Toast.LENGTH_SHORT).show();
                            }
                        });

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    });
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        // Handle error scenario here
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    // Handle exception scenario here
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Handle closing error
                    }
                }
            }
        }).start();
    }

    private void animatedCardViewDownwards() {
        ViewPropertyAnimator animator = animatedCardView.animate();
        animator.translationY(animatedCardView.getHeight()).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                animatedCardView.setVisibility(View.GONE);
                animatedCardView.setTranslationY(0); // Reset position
            }
        }).start();
    }

    private void animatedCardViewUpwards() {
        animatedCardView.setVisibility(View.VISIBLE);
        animatedCardView.setTranslationY(animatedCardView.getHeight()); // Start from below
        ViewPropertyAnimator animator = animatedCardView.animate();
        animator.translationY(0).setDuration(300).start(); // Animate to its original position
    }

    // Method to toggle the navigation drawer
    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (isCardViewVisible) {
                animatedCardViewDownwards();
                isCardViewVisible = false;
            } else {
                if (doubleBackToExitPressedOnce) {
                    // This will close the app
                    finishAffinity(); // Closes the app and clears the back stack
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                    handler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000); // 2 seconds delay
                }
            }
        }
    }

    private void updateVehicleProfile() {
        // Retrieve data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("vehicle_data", Context.MODE_PRIVATE);
        String vehicleProfile = sharedPreferences.getString("vehicle_profile", "");
    }
}
