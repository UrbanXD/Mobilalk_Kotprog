package com.urbanxd.mobilalk_kotprog.ui.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.utils.Utils;

import java.util.List;

public class WaterMeterStateAdapter extends RecyclerView.Adapter<WaterMeterStateAdapter.StateViewHolder> {

    private List<WaterMeterState> states;

    public WaterMeterStateAdapter(List<WaterMeterState> states) {
        this.states = states;
    }

    public static class StateViewHolder extends RecyclerView.ViewHolder {
        LinearLayout stateItemLayout;
        TextView dateTextView, stateTextView;

        public StateViewHolder(View itemView) {
            super(itemView);
            stateItemLayout = itemView.findViewById(R.id.stateItemLayout);
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

        holder.stateItemLayout.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();

            if (EditStateBottomSheetDialogFragment.isShowing()) return;
            EditStateBottomSheetDialogFragment.newInstance(state.getId()).show(fragmentManager, "EditStateBottomSheet");
        });
        holder.dateTextView.setText(Utils.formatDate(state.getDate()));
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