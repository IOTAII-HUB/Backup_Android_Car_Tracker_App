package com.Iotaii.car_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Iotaii.car_tracker.R;

import java.util.List;

public class KmSummaryAdapter extends RecyclerView.Adapter<KmSummaryAdapter.KmSummaryViewHolder> {

    private List<KmSummary> kmSummaryList;

    public KmSummaryAdapter(List<KmSummary> kmSummaryList) {
        this.kmSummaryList = kmSummaryList;
    }

    @NonNull
    @Override
    public KmSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_km_summary, parent, false);
        return new KmSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KmSummaryViewHolder holder, int position) {
        KmSummary kmSummary = kmSummaryList.get(position);
        holder.tvId.setText(kmSummary.getId());
        holder.tvTotalDistance.setText(String.valueOf(kmSummary.getTotalDistance()));
        holder.tvTotalDuration.setText(kmSummary.getTotalDuration());
    }

    @Override
    public int getItemCount() {
        return kmSummaryList.size();
    }

    public static class KmSummaryViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvTotalDistance, tvTotalDuration;

        public KmSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTotalDistance = itemView.findViewById(R.id.tvTotalDistance);
            tvTotalDuration = itemView.findViewById(R.id.tvTotalDuration);
        }
    }
}
