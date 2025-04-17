package com.urbanxd.mobilalk_kotprog.ui.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.ui.components.AddStateBottomSheetDialogFragment;
import com.urbanxd.mobilalk_kotprog.ui.components.NotificationJobService;
import com.urbanxd.mobilalk_kotprog.ui.components.WaterMeterStateAdapter;
import com.urbanxd.mobilalk_kotprog.utils.Utils;
import com.urbanxd.mobilalk_kotprog.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private MainViewModel mainViewModel;

    private JobScheduler jobScheduler;
    private WaterMeterStateAdapter adapter;

    private List<WaterMeterState> states;
    private int currentPage = 0;
    private final int PAGE_SIZE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean askForNotificationPermission = sharedPreferences.getBoolean(Utils.SHARED_PREFERENCE_ASK_FOR_NOTIFICATION_PERMISSION, false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            finish();
            return;
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.loadUser(firebaseUser);
        mainViewModel.isLoaded().observe(this, isLoaded -> {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            View activityContent = findViewById(R.id.activityContent);

            if (isLoaded) {
                progressBar.setVisibility(View.GONE);
                activityContent.setVisibility(View.VISIBLE);

                RelativeLayout stateTableContainer = findViewById(R.id.stateTableContainer);
                stateTableContainer.post(() -> {
                    stateTableContainer.setVisibility(View.VISIBLE);
                    Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                    stateTableContainer.startAnimation(fadeIn);
                });

                if (askForNotificationPermission) {
                    Utils.sendNotification(this, "Sikeres teszt");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Utils.SHARED_PREFERENCE_ASK_FOR_NOTIFICATION_PERMISSION, false);
                    editor.apply();
                }
            } else {
                progressBar.setVisibility(View.VISIBLE);
                activityContent.setVisibility(View.GONE);
            }
        });

        TextView welcomeText = findViewById(R.id.welcomeText);
        RecyclerView recyclerView = findViewById(R.id.statesRecyclerView);
        TextView noStateFoundText = findViewById(R.id.noStateFound);
        View paginatorView = findViewById(R.id.paginatorLayout);
        ImageButton prevPageButton = findViewById(R.id.prevPageButton);
        ImageButton nextPageButton = findViewById(R.id.nextPageButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WaterMeterStateAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        int elementHeight = getResources().getDimensionPixelSize(R.dimen.item_water_meter_state_height) + 2 * getResources().getDimensionPixelSize(R.dimen.gap);
        int minHeight = elementHeight * PAGE_SIZE;
        recyclerView.setMinimumHeight(minHeight);
        noStateFoundText.setMinimumHeight(minHeight);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                showPage(currentPage);
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if ((currentPage + 1) * PAGE_SIZE < states.size()) {
                currentPage++;
                showPage(currentPage);
            }
        });

        mainViewModel.getUserLiveData().observeForever(user -> {
            if (user == null) return;

            String welcomeMessage = "Üdv nálunk " + user.getLastname() + " " + user.getFirstname() + "!";
            welcomeText.setText(welcomeMessage);

            if (user.getWaterMeter() == null) return;

            states = user.getWaterMeter().getStates();

            if(states.isEmpty()) {
                noStateFoundText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                paginatorView.setVisibility(View.INVISIBLE);
            } else {
                noStateFoundText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                paginatorView.setVisibility(View.VISIBLE);
            }

            showPage(currentPage);
        });


        View logoAndTitle = findViewById(R.id.logoAndTitleContainer);
        logoAndTitle.post(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logoAndTitle.startAnimation(slideIn);
        });

        setJobScheduler();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Utils.handlePermissionResult(this, requestCode, permissions, grantResults);
    }

    private void showPage(int page) {
        TextView paginatorInfoText = findViewById(R.id.paginatorInfoText);

        int fromIndex = page * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, states.size());
        List<WaterMeterState> pageData = states.subList(fromIndex, toIndex);

        String paginatorInfoMessage =  fromIndex + 1 + " - " + toIndex + " / " + states.size();
        paginatorInfoText.setText(paginatorInfoMessage);
        adapter.updateStates(pageData);
    }

    public void logout(View view) {
        firebaseAuth.signOut();

        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Utils.SHARED_PREFERENCE_USER_ID);
        editor.apply();

        Utils.openActivity(this, MainActivity.class, true);
        Utils.openToast(this, getString(R.string.success_logout));
    }

    public void openAddStateBottomSheet(View view) {
        AddStateBottomSheetDialogFragment.newInstance(mainViewModel.getHighestWaterMeterState()).show(getSupportFragmentManager(), "AddStateBottomSheet");
    }

    public void setJobScheduler() {
        ComponentName componentName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder =
            new JobInfo
                .Builder(0, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(Utils.FIFTEEN_MINUTES);

        jobScheduler.schedule(builder.build());
    }
}