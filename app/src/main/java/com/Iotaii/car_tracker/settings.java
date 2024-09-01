package com.Iotaii.car_tracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class settings extends AppCompatActivity {

    private static final String CHANNEL_ID = "notifications_channel";
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String SOUND_ENABLED_KEY = "sound_enabled";
    private static final String ALERT_ENABLED_KEY = "alert_enabled";
    private static final String VIBRATE_ENABLED_KEY = "vibrate_enabled";
    private static final String VOICE_ENABLED_KEY = "voice_enabled";
    private static final String DASHBOARD_ENABLED_KEY = "dashboard_enabled";
    private static final String LIVE_ENABLED_KEY = "live_enabled";
    private static final String LANGUAGE_KEY = "language";

    private Spinner languageSpinner;
    private SharedPreferences sharedPreferences;
    private Switch dashboardSwitch, liveSwitch, alertSwitch, vibrateSwitch, soundSwitch, voiceSwitch;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_light));
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initializeUIElements();
        loadPreferences();
        createNotificationChannel();
        setupLanguageSpinner();
        setupSwitchListeners();
        setupClickListeners();
    }

    private void initializeUIElements() {
        ImageView back = findViewById(R.id.back);
        Button user = findViewById(R.id.user);
        Button vehicle = findViewById(R.id.vehicle);
        alertSwitch = findViewById(R.id.alert);
        vibrateSwitch = findViewById(R.id.vibrate);
        soundSwitch = findViewById(R.id.sound);
        voiceSwitch = findViewById(R.id.voice);
        dashboardSwitch = findViewById(R.id.dashboard);
        liveSwitch = findViewById(R.id.live);
        languageSpinner = findViewById(R.id.spinner);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound);
    }

    private void loadPreferences() {
        alertSwitch.setChecked(sharedPreferences.getBoolean(ALERT_ENABLED_KEY, false));
        vibrateSwitch.setChecked(sharedPreferences.getBoolean(VIBRATE_ENABLED_KEY, false));
        soundSwitch.setChecked(sharedPreferences.getBoolean(SOUND_ENABLED_KEY, true));
        voiceSwitch.setChecked(sharedPreferences.getBoolean(VOICE_ENABLED_KEY, false));
        dashboardSwitch.setChecked(sharedPreferences.getBoolean(DASHBOARD_ENABLED_KEY, false));
        liveSwitch.setChecked(sharedPreferences.getBoolean(LIVE_ENABLED_KEY, false));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications";
            String description = "Channel for alert notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupLanguageSpinner() {
        String[] languages = {"English", "Bengali", "Hindi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String currentLanguage = getCurrentLanguage();
        switch (currentLanguage) {
            case "en":
                languageSpinner.setSelection(0);
                break;
            case "bn":
                languageSpinner.setSelection(1);
                break;
            case "hi":
                languageSpinner.setSelection(2);
                break;
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage;
                switch (position) {
                    case 0:
                        selectedLanguage = "en";
                        break;
                    case 1:
                        selectedLanguage = "bn";
                        break;
                    case 2:
                        selectedLanguage = "hi";
                        break;
                    default:
                        selectedLanguage = "en";
                        break;
                }

                if (!selectedLanguage.equals(getCurrentLanguage())) {
                    setLocale(selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void setupSwitchListeners() {
        dashboardSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(DASHBOARD_ENABLED_KEY, isChecked);
            if (isChecked) {
                liveSwitch.setChecked(false);
                saveLandingPageSelection("dashboard");
            } else if (!liveSwitch.isChecked()) {
                saveLandingPageSelection("");
            }
            updateSwitches();
        });

        liveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(LIVE_ENABLED_KEY, isChecked);
            if (isChecked) {
                dashboardSwitch.setChecked(false);
                saveLandingPageSelection("live");
            } else if (!dashboardSwitch.isChecked()) {
                saveLandingPageSelection("");
            }
            updateSwitches();
        });

        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(ALERT_ENABLED_KEY, isChecked);
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    enableNotifications();
                }
            } else {
                disableNotifications();
            }
            updateNotificationSwitchesEnabledState();
        });

        vibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(VIBRATE_ENABLED_KEY, isChecked);
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    enableNotifications();
                }
                triggerVibration();
            } else {
                disableNotifications();
            }
            updateNotificationSwitchesEnabledState();
        });

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(SOUND_ENABLED_KEY, isChecked);
            if (isChecked) {
                playSound();
            }
        });

        voiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(VOICE_ENABLED_KEY, isChecked);
        });
    }

    private void setupClickListeners() {
        findViewById(R.id.back).setOnClickListener(v -> startActivity(new Intent(settings.this, dashboard.class)));
        findViewById(R.id.user).setOnClickListener(v -> startActivity(new Intent(settings.this, userprofile.class)));
        findViewById(R.id.vehicle).setOnClickListener(v -> startActivity(new Intent(settings.this, veichle.class)));
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void triggerVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

    private void updateSwitches() {
        updateNotificationSwitchesEnabledState();
    }

    private void updateNotificationSwitchesEnabledState() {
        boolean alertEnabled = alertSwitch.isChecked();
        vibrateSwitch.setEnabled(alertEnabled);
        soundSwitch.setEnabled(alertEnabled);
        voiceSwitch.setEnabled(alertEnabled);
    }

    private String getCurrentLanguage() {
        return sharedPreferences.getString(LANGUAGE_KEY, "en");
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANGUAGE_KEY, lang);
        editor.apply();

        Intent intent = new Intent(this, settings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void saveSwitchState(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void saveLandingPageSelection(String landingPage) {
        SharedPreferences landingPagePrefs = getSharedPreferences("LandingPagePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = landingPagePrefs.edit();
        editor.putString("landingPage", landingPage);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void enableNotifications() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationsactive)
                .setContentTitle("Notifications Enabled")
                .setContentText("You will receive alerts.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (vibrateSwitch.isChecked()) {
            builder.setVibrate(new long[]{0, 500, 1000, 500});
        }

        if (soundSwitch.isChecked()) {
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        } else {
            builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void disableNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
