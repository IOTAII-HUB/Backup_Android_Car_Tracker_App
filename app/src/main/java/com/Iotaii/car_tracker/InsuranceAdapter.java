package com.Iotaii.car_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.List;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.InsuranceViewHolder> {
    private List<Insurance> insuranceList;
    private Context context;

    public InsuranceAdapter(List<Insurance> insuranceList, Context context) {
        this.insuranceList = insuranceList;
        this.context = context;
    }

    @NonNull
    @Override
    public InsuranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_layout, parent, false);
        return new InsuranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsuranceViewHolder holder, int position) {
        Insurance insurance = insuranceList.get(position);
        holder.title.setText(insurance.getTitle());
        holder.expiryDate.setText("Expired on : " + insurance.getExpiryDate());
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    public static class InsuranceViewHolder extends RecyclerView.ViewHolder {
        TextView title, expiryDate;
        ImageView iconWarning;

        public InsuranceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            expiryDate = itemView.findViewById(R.id.expiry_date);
            iconWarning = itemView.findViewById(R.id.icon_warning);
        }
    }
}
