package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TemperatureAdapter extends RecyclerView.Adapter<TemperatureAdapter.ViewHolder> {

    private List<String> temperatures;

    public TemperatureAdapter(List<String> temperatures) {
        this.temperatures = temperatures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temperature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String temperature = temperatures.get(position);
        holder.temperatureTextView.setText(temperature);
    }

    @Override
    public int getItemCount() {
        return temperatures.size();
    }

    public void updateData(List<String> newTemperatures) {
        temperatures = newTemperatures;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView temperatureTextView;

        ViewHolder(View itemView) {
            super(itemView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
        }
    }
}
