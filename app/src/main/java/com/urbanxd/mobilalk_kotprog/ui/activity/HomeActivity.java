package com.urbanxd.mobilalk_kotprog.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.ui.components.AddStateBottomSheetDialogFragment;
import com.urbanxd.mobilalk_kotprog.ui.components.WaterMeterStateAdapter;
import com.urbanxd.mobilalk_kotprog.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private UserViewModel userViewModel;

    private WaterMeterStateAdapter adapter;

    private List<WaterMeterState> states;
    private int currentPage = 0;
    private final int PAGE_SIZE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            finish();
            return;
        }

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.loadUser(firebaseUser);

        TextView welcomeText = findViewById(R.id.welcomeText);
        RecyclerView recyclerView = findViewById(R.id.statesRecyclerView);
        ImageButton prevPageButton = findViewById(R.id.prevPageButton);
        ImageButton nextPageButton = findViewById(R.id.nextPageButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WaterMeterStateAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        int elementHeight = getResources().getDimensionPixelSize(R.dimen.min_touchable_layout_height) + 2 * getResources().getDimensionPixelSize(R.dimen.gap);
        int minHeight = elementHeight * PAGE_SIZE;
        recyclerView.setMinimumHeight(minHeight);

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

        userViewModel.getUserLiveData().observe(this, user -> {
            if (user == null) return;

            String welcomeMessage = "Üdv nálunk " + user.getLastname() + " " + user.getFirstname() + "!";
            welcomeText.setText(welcomeMessage);

            if (user.getWaterMeter() == null) return;

            states = user.getWaterMeter().getStates();;
            showPage(currentPage);
        });


        LinearLayout logoAndTitle = findViewById(R.id.logoAndTitleContainer);
        logoAndTitle.post(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logoAndTitle.startAnimation(slideIn);
        });
    }

    private void showPage(int page) {
        TextView paginatorInfoText = findViewById(R.id.paginatorInfoText);
        LinearLayout paginatorLayout = findViewById(R.id.paginatorLayout);

        int fromIndex = page * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, states.size());
        List<WaterMeterState> pageData = states.subList(fromIndex, toIndex);

        int startItem = fromIndex + 1;
        int endItem = toIndex == states.size() ? toIndex : toIndex + 1;
        String paginatorInfoMessage =  startItem + " - " + endItem + " / " + states.size();
        paginatorInfoText.setText(paginatorInfoMessage);
        adapter.updateStates(pageData);
        paginatorLayout.setVisibility(View.VISIBLE);
    }

    public void logout(View view) {
        openAddStateBottomSheet(view);
//        firebaseAuth.signOut();
//        openMainActivity();
//        Toast.makeText(getApplicationContext(), getString(R.string.success_logout), Toast.LENGTH_SHORT).show();
    }
    ///  https://docs.google.com/spreadsheets/d/16DW3t5EAqHwCpE9dSO_Xyh4nyv5SOCTy2vXcO0nzRU8/edit?gid=528411539#gid=528411539

    public void openAddStateBottomSheet(View view) {
        AddStateBottomSheetDialogFragment.newInstance(1800).show(getSupportFragmentManager(), "AddStateBottomSheet");
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); /// uriti a stacket, hogy back buttonnal veletlen se lehessen pl ide visszakerulni
        startActivity(intent);
    }
}