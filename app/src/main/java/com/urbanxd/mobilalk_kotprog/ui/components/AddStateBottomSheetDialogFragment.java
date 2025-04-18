package com.urbanxd.mobilalk_kotprog.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.viewmodel.MainViewModel;

public class AddStateBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private static final String MIN_VALUE_ARG = "minValue";

    private MainViewModel mainViewModel;
    private EditText stateInput;
    private TextView stateError;
    private long minValue = 0;

    public static AddStateBottomSheetDialogFragment newInstance(long state) {
        AddStateBottomSheetDialogFragment fragment = new AddStateBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putLong(MIN_VALUE_ARG, state);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("stateError", stateError.getText().toString());
        outState.putInt("stateErrorVisibility", stateError.getVisibility());
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.add_state_bottom_sheet, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateInput = view.findViewById(R.id.stateInput);
        stateError = view.findViewById(R.id.stateError);
        Button increaseButton = view.findViewById(R.id.increaseButton);
        Button decreaseButton = view.findViewById(R.id.decreaseButton);
        Button addButton = view.findViewById(R.id.addButton);
        ImageButton dismissButton = view.findViewById(R.id.dismissButton);

        if (getArguments() != null) {
            minValue = getArguments().getLong(MIN_VALUE_ARG, 0);
        }

        if(savedInstanceState != null) {
            stateError.setText(savedInstanceState.getString("stateError", ""));
            stateError.setVisibility(savedInstanceState.getInt("stateErrorVisibility", View.GONE));
        }

        stateInput.setText(String.valueOf(minValue));
        stateInput.setFocusableInTouchMode(true);
        stateInput.requestFocus();

        addButton.setOnClickListener(v -> {
            long state = getNumber(stateInput);

            if(state <= minValue) {
                stateError.setText(getString(R.string.invalid_new_water_meter_state));
                stateError.setVisibility(View.VISIBLE);
                return;
            }

            mainViewModel.addNewWaterMeterState(state);
            dismiss();
        });

        dismissButton.setOnClickListener(v -> dismiss());

        increaseButton.setOnClickListener(v -> {
            long current = getNumber(stateInput);
            stateInput.setText(String.valueOf(current + 1));
        });

        decreaseButton.setOnClickListener(v -> {
            long current = getNumber(stateInput);
            if (current > 0) {
                stateInput.setText(String.valueOf(current - 1));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        stateInput.postDelayed(() -> {
            stateInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(stateInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    private long getNumber(EditText editText) {
        String text = editText.getText().toString().trim();

        try {
            long state = Long.parseLong(text);
            return Math.max(state, minValue);
        } catch (NumberFormatException e) {
            return minValue;
        }
    }
}
