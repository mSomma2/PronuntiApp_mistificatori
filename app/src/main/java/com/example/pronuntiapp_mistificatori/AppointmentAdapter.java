package com.example.pronuntiapp_mistificatori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        appointment appointment = appointmentList.get(position);
        if (appointment != null) {
            String appointmentDate = appointment.getDate();
            holder.dateTextView.setText(appointmentDate);
        }
    }

    private List<appointment> appointmentList;

    public AppointmentAdapter(List<appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        appointment appointment = appointmentList.get(position);
        holder.dateTextView.setText(appointment.getDate());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
