package com.Iotaii.car_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.ArrayList;
import java.util.List;

public class km extends AppCompatActivity {

    private ImageView back, threedots;
    private RecyclerView recyclerView;
    private KmSummaryAdapter kmSummaryAdapter;
    private List<KmSummary> kmSummaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_km);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_light));
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            back = findViewById(R.id.back);
            threedots = findViewById(R.id.threedots); // Add this line to find the threedots ImageView
            recyclerView = findViewById(R.id.recyclerView);

            back.setOnClickListener(v -> {
                Intent intent = new Intent(km.this, dashboard.class);
                startActivity(intent);
            });

            threedots.setOnClickListener(v -> showOptionsDialog()); // Set OnClickListener for threedots

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Sample data
            kmSummaryList = new ArrayList<>();
            kmSummaryList.add(new KmSummary("BR06PE6292", 28, "149D 19H 2M 55S"));
            kmSummaryList.add(new KmSummary("BR06PE6287", 27, "144D 17H 2M 50S"));
            // Add more items to the list as needed

            kmSummaryAdapter = new KmSummaryAdapter(kmSummaryList);
            recyclerView.setAdapter(kmSummaryAdapter);

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
        }
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose to Download");
        builder.setItems(new CharSequence[]
                        {"Download PDF", "Download Excel"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                downloadPdf();
                                break;
                            case 1:
                                downloadExcel();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void downloadPdf() {
        // Handle Download PDF action
    }

    private void downloadExcel() {
        // Handle Download Excel action
    }
}
