package com.urbanxd.mobilalk_kotprog.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.utils.Utils;
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

    @NonNull
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

        stateInput.setText(String.valueOf(minValue + 1));
        stateInput.setSelection(String.valueOf(minValue + 1).length());
        stateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    stateInput.setText(String.valueOf(minValue));
                    stateInput.setSelection(String.valueOf(minValue).length());
                    return;
                }

                try {
                    long stateValue = Utils.getNumberTextInput(stateInput, 0, minValue, -1);
                    String stateString = String.valueOf(stateValue);

                    if (!s.toString().equals(stateString)){
                        stateInput.setText(stateString);
                        stateInput.setSelection(stateString.length());
                    }
                } catch (NumberFormatException e) {
                    stateInput.setText(String.valueOf(minValue + 1));
                    stateInput.setSelection(String.valueOf(minValue + 1).length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        stateInput.setFocusableInTouchMode(true);

        addButton.setOnClickListener(v -> {
            long state = Utils.getNumberTextInput(stateInput, 0, minValue, -1);

            if(state <= minValue) {
                stateError.setText(getString(R.string.invalid_new_water_meter_state));
                stateError.setVisibility(View.VISIBLE);
                return;
            }

            mainViewModel.addNewWaterMeterState(state);
            Utils.openToast(requireContext(), "Sikeresen felvitte a vízórájának állását!", Toast.LENGTH_LONG);
            dismiss();
        });

        dismissButton.setOnClickListener(v -> dismiss());

        increaseButton.setOnClickListener(v -> {
            long newState = Utils.getNumberTextInput(stateInput, 1, minValue, -1);
            String newStateString = String.valueOf(newState);
            stateInput.setText(newStateString);
            stateInput.setSelection(newStateString.length());
        });

        decreaseButton.setOnClickListener(v -> {
            long newState = Utils.getNumberTextInput(stateInput, -1, minValue, -1);
            String newStateString = String.valueOf(newState);
            stateInput.setText(newStateString);
            stateInput.setSelection(newStateString.length());
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
        }, 250);
    }
}
