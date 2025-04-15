package com.urbanxd.mobilalk_kotprog.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.urbanxd.mobilalk_kotprog.data.model.User;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.data.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final WaterMeterViewModel waterMeterViewModel = new WaterMeterViewModel();
    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<Boolean> userLoadedLiveData = new MutableLiveData<>(false);

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> isUserLoaded() {
        return userLoadedLiveData;
    }

    public void loadUser(FirebaseUser firebaseUser) {
        userRepository.getUser(firebaseUser, result -> {
            userLiveData.setValue(result.data);

            waterMeterViewModel.loadWaterMeter(firebaseUser.getUid());
            waterMeterViewModel.getWaterMeterLiveData().observeForever(waterMeter -> {
                User updatedUser = userLiveData.getValue();
                if (updatedUser != null) {
                    updatedUser.setWaterMeter(waterMeter);
                    userLiveData.setValue(updatedUser);
                }

                if (!Boolean.TRUE.equals(userLoadedLiveData.getValue())) {
                    userLoadedLiveData.setValue(true);
                }
            });
        });
    }

    public long getHighestWaterMeterState() {
        return waterMeterViewModel.getHighestState();
    }

    public void addNewWaterMeterState(long state) {
        User user = userLiveData.getValue();
        if(user == null) return;

        WaterMeterState newWaterMeterState = new WaterMeterState(user.getWaterMeter().getId(), state);
        waterMeterViewModel.addNewState(user.getId(), newWaterMeterState);
    }
}
