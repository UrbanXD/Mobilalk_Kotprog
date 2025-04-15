package com.urbanxd.mobilalk_kotprog.ui.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;

import java.util.List;

public class WaterMeterStateAdapter extends RecyclerView.Adapter<WaterMeterStateAdapter.StateViewHolder> {

    private List<WaterMeterState> states;

    public WaterMeterStateAdapter(List<WaterMeterState> states) {
        this.states = states;
    }

    public static class StateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, stateTextView;

        public StateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            stateTextView = itemView.findViewById(R.id.stateTextView);
        }
    }

    @Override
    public StateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watermeter_state, parent, false);
        return new StateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StateViewHolder holder, int position) {
        WaterMeterState state = states.get(position);
        holder.dateTextView.setText(state.getFormatedDate());
        holder.stateTextView.setText(String.valueOf(state.getState()));
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    public void updateStates(List<WaterMeterState> newStates) {
        this.states = newStates;
        notifyDataSetChanged();
    }
}