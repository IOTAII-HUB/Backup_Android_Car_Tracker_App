package com.Iotaii.car_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_layout, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.vehicleNumber.setText(notification.getVehicleNumber());
        holder.timestamp.setText(notification.getTimestamp());
        holder.message.setText(notification.getMessage());
        holder.location.setText(notification.getLocation());

        if (notification.isStarted()) {
            holder.statusIcon.setImageResource(R.drawable.ic_green_circle); // Update with your green icon resource
            holder.message.setText("Vehicle has started!");
        } else {
            holder.statusIcon.setImageResource(R.drawable.ic_red_circle); // Update with your red icon resource
            holder.message.setText("Vehicle has stopped!");
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public ImageView statusIcon;
        public TextView vehicleNumber;
        public TextView timestamp;
        public TextView message;
        public TextView location;

        public NotificationViewHolder(View view) {
            super(view);
            statusIcon = view.findViewById(R.id.status_icon);
            vehicleNumber = view.findViewById(R.id.vehicle_number);
            timestamp = view.findViewById(R.id.timestamp);
            message = view.findViewById(R.id.message);
            location = view.findViewById(R.id.location);
        }
    }
}
