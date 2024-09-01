package com.Iotaii.car_tracker;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.Iotaii.car_tracker.R;

public class splashscreen extends AppCompatActivity {

    // Variables
    Animation topanim, botanim;
    ImageView image;
    TextView textView;

    // Duration of splash screen in milliseconds
    private static final long SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splashscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize views
        image = findViewById(R.id.image);
        textView = findViewById(R.id.textView);

        // Load animations
        topanim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        botanim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Set animations
        image.setAnimation(topanim);
        textView.setAnimation(botanim);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Post delayed action to start the intent after a delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splashscreen.this, MainActivity.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(image, "splash");
            pairs[1] = new Pair<View, String>(textView, "splashtxt");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splashscreen.this, pairs);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
            finish(); // Close splash screen
        }, SPLASH_DURATION);
    }
}
