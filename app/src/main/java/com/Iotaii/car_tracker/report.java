package com.Iotaii.car_tracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.ArrayList;
import java.util.List;

public class report extends AppCompatActivity {

    private ImageView back;
    private RecyclerView recyclerView;
    private ReportItemAdapter reportItemAdapter;
    private List<ItemModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        // Set screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(report.this, dashboard.class);
                startActivity(intent);
            }
        });

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

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize item list and add sample data
        itemList = new ArrayList<>();
        itemList.add(new ItemModel("27/06/2024 14:14 to 27/06/2024 14:14", "0.00", "Gopinathpur Dokda, Gopinathpur Doghra, Saraiya, Bihar 843126, India", "Gopinathpur Dokda, Gopinathpur Doghra, Saraiya, Bihar 843126, India"));
        // Add more items to the list as needed

        // Set up adapter and attach to RecyclerView
        reportItemAdapter = new ReportItemAdapter(itemList);
        recyclerView.setAdapter(reportItemAdapter);
    }
}
