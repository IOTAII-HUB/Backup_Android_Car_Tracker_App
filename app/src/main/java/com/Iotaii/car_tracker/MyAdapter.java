package com.Iotaii.car_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Vehicle> vehicleList;

    public MyAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.tvVehicleId.setText(vehicle.getVehicleId());
        holder.tvDistance.setText(vehicle.getDistance());
        holder.tvRunningTime.setText("Running: " + vehicle.getRunningTime());
        holder.tvStopTime.setText("Stop: " + vehicle.getStopTime());
        holder.tvIdleTime.setText("Idle: " + vehicle.getIdleTime());
        holder.tvHalts.setText("Halts: " + vehicle.getHalts());
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public void updateVehicleList(List<Vehicle> newVehicleList) {
        this.vehicleList.clear();
        this.vehicleList.addAll(newVehicleList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvVehicleId, tvDistance, tvRunningTime, tvStopTime, tvIdleTime, tvHalts;

        public MyViewHolder(View view) {
            super(view);
            tvVehicleId = view.findViewById(R.id.tv_vehicle_id);
            tvDistance = view.findViewById(R.id.tv_distance);
            tvRunningTime = view.findViewById(R.id.tv_running_time);
            tvStopTime = view.findViewById(R.id.tv_stop_time);
            tvIdleTime = view.findViewById(R.id.tv_idle_time);
            tvHalts = view.findViewById(R.id.tv_halts);
        }
    }
}
