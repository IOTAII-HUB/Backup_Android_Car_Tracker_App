package com.Iotaii.car_tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private CheckBox rememberCheckBox;
    private Button loginButton;
    private ConnectivityReceiver connectivityReceiver;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check the landing page preference
        SharedPreferences sharedPreferences = getSharedPreferences("LandingPagePrefs", Context.MODE_PRIVATE);
        String landingPage = sharedPreferences.getString("landingPage", "");

        if ("dashboard".equals(landingPage)) {
            Intent intent = new Intent(this, dashboard.class);
            intent.putExtra("username", landingPage);
            startActivity(intent);
            finish();
            return; // Exit onCreate to avoid setting the content view
        } else if ("live".equals(landingPage)) {
            Intent intent = new Intent(this, livemap.class);
            intent.putExtra("username", landingPage);
            startActivity(intent);
            finish();
            return; // Exit onCreate to avoid setting the content view
        }

        // If no preference is set, load the default content view
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_light));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        rememberCheckBox = findViewById(R.id.remember);
        loginButton = findViewById(R.id.login);

        // Load saved data if checkbox is checked
        loadSavedData();

        loginButton.setOnClickListener(v -> {
            if (isConnected) {
                login();
            } else {
                Toast.makeText(MainActivity.this, "Please Turn on Your Internet!", Toast.LENGTH_SHORT).show();
            }
        });

        // Register the ConnectivityReceiver
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);

        // Check initial connectivity
        checkInitialConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the ConnectivityReceiver
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }

    private void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (rememberCheckBox.isChecked()) {
            saveLoginData(username, password);
        } else {
            clearLoginData();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://3.109.34.34:5454/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<ResponseBody> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String rawResponse = response.body().string();
                        //Toast.makeText(MainActivity.this, "Login successful: " + rawResponse, Toast.LENGTH_SHORT).show();

                        // Retrieve landing page preference
                        SharedPreferences landingPagePrefs = getSharedPreferences("LandingPagePrefs", Context.MODE_PRIVATE);
                        String landingPagePred = landingPagePrefs.getString("landingPage", "");

                        // Redirect to the landing page or default page
                        Intent intent;
                        if ("dashboard".equals(landingPagePred)) {
                            intent = new Intent(MainActivity.this, dashboard.class);
                        } else if ("live".equals(landingPagePred)) {
                            intent = new Intent(MainActivity.this, livemap.class);
                        } else {
                            intent = new Intent(MainActivity.this, dashboard.class); // Default landing page
                        }
                        intent.putExtra("username", username); // Pass the username to the next activity
                        startActivity(intent);
                        finish();
                    } else if (response.code() == 401) {
                        // Handle incorrect username or password
                        Toast.makeText(MainActivity.this, "Check your username and password.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorBody = response.errorBody().string();
                        Log.e("Login", "Unsuccessful response: " + errorBody);
                        Toast.makeText(MainActivity.this, "Server is in Maintenance", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("Login", "Error reading response body", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Login", "Error: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Server is in Maintenance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginData(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("remember", true);
        editor.apply();
    }

    private void clearLoginData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean remember = sharedPreferences.getBoolean("remember", false);

        if (remember) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberCheckBox.setChecked(true);
        }
    }

    private void checkInitialConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.d("Connectivity", "Internet is connected.");
        } else {
            Toast.makeText(MainActivity.this, "Please Turn on Your Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    public interface ApiService {
        @POST("login")
        Call<ResponseBody> login(@Body LoginRequest loginRequest);
    }

    public static class LoginRequest {
        @SerializedName("username")
        private String username;
        @SerializedName("password")
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean currentlyConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (currentlyConnected && !isConnected) {
                isConnected = true;
                Toast.makeText(context, "Internet is turned on", Toast.LENGTH_SHORT).show();
            } else if (!currentlyConnected && isConnected) {
                isConnected = false;
                Toast.makeText(context, "Please Turn on Your Internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
