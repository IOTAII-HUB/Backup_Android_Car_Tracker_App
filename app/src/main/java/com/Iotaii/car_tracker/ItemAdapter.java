package com.Iotaii.car_tracker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private List<Integer> vehicleIds;
    private String username;
    private static OnItemClickListener mListener;

    public ItemAdapter(List<Item> itemList, List<Integer> vehicleIds, String username) {
        this.itemList = itemList;
        this.vehicleIds = vehicleIds;
        this.username = username;
    }

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(int vehicleId);
    }

    // Set the listener for item clicks
    public static void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        int vehicleId = vehicleIds.get(position);

        // Fetch and display vehicle details
        fetchVehicleDetails(vehicleId, username, vehicle -> {
            if (vehicle != null) {
                holder.titleTextView.setText("Vehicle ID: " + vehicle.getVehicleId());
                holder.descriptionTextView.setText("Distance: " + vehicle.getDistance());
                holder.imageView.setImageResource(R.drawable.car); // Example image resource
                holder.additionalTextView.setText("Additional Text " + vehicleId);
            } else {
                holder.titleTextView.setText("Details not found");
                holder.descriptionTextView.setText("");
            }
        });

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(vehicleId);
            }

            // Optional: Click the slide image if in dashboard context
            if (v.getContext() instanceof dashboard) {
                ImageView slideImageView = ((dashboard) v.getContext()).findViewById(R.id.slide);
                if (slideImageView != null) {
                    slideImageView.performClick();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Method to filter items based on a query
    public void filter(String query) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        itemList = filteredList;
        notifyDataSetChanged();
    }

    // Method to fetch vehicle details
    private void fetchVehicleDetails(int vehicleId, String username, VehicleDetailsCallback callback) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                String urlString = String.format("http://3.109.34.34:5454/vehicles/details?vehicleId=%d&username=%s", vehicleId, username);
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

                    // Parse the JSON response
                    JSONObject vehicleDetails = new JSONObject(response.toString());
                    Vehicle vehicle = new Vehicle();
                    vehicle.setVehicleId(vehicleDetails.getInt("vehicleId"));
                    vehicle.setDistance(vehicleDetails.getInt("distance"));

                    // Callback with the fetched vehicle details
                    new Handler(Looper.getMainLooper()).post(() -> callback.onDetailsFetched(vehicle));
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onDetailsFetched(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onDetailsFetched(null));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Callback interface for vehicle details
    interface VehicleDetailsCallback {
        void onDetailsFetched(Vehicle vehicle);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView imageView;
        TextView additionalTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            descriptionTextView = itemView.findViewById(R.id.item_description);
            imageView = itemView.findViewById(R.id.item_image);
            additionalTextView = itemView.findViewById(R.id.image_text);
        }
    }
}
