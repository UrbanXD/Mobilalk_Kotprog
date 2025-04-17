package com.urbanxd.mobilalk_kotprog.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.urbanxd.mobilalk_kotprog.data.model.User;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeter;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.data.repository.UserRepository;
import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterRepository;
import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterStateRepository;

import java.util.Objects;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<WaterMeter> waterMeterLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadedLiveData = new MutableLiveData<>(false);

    private final UserRepository userRepository = new UserRepository();
    private final WaterMeterRepository waterMeterRepository = new WaterMeterRepository();
    private final WaterMeterStateRepository waterMeterStateRepository = new WaterMeterStateRepository();

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> isLoaded() {
        return loadedLiveData;
    }

    public void loadUser(FirebaseUser firebaseUser) {
        userRepository.getUser(firebaseUser, result -> {
            userLiveData.setValue(result.data);

            loadWaterMeter(firebaseUser.getUid());
            waterMeterLiveData.observeForever(waterMeter -> {
                User user = userLiveData.getValue();
                if(user != null) {
                    user.setWaterMeter(waterMeter);
                    userLiveData.setValue(user);
                }

                if(!Boolean.TRUE.equals(loadedLiveData.getValue())) {
                    loadedLiveData.setValue(true);
                }
            });
        });
    }

    private void loadWaterMeter(String userId) {
        waterMeterRepository.getWaterMeter(userId, result -> {
            waterMeterLiveData.setValue(result.data);
        });
    }

    public long getHighestWaterMeterState() {
        long highestStateValue = 0;
        for (WaterMeterState state : Objects.requireNonNull(waterMeterLiveData.getValue()).getStates()) {
            if (state.getState() > highestStateValue) {
                highestStateValue = state.getState();
            }
        }

        return highestStateValue;
    }

    public WaterMeterState getWaterMeterStateById(String id) {
        for (WaterMeterState state : Objects.requireNonNull(waterMeterLiveData.getValue()).getStates()) {
            if (Objects.equals(state.getId(), id)) {
                return state;
            }
        }
        return null;
    }

    public void addNewWaterMeterState(long state) {
        User user = userLiveData.getValue();
        if(user == null) return;

        WaterMeterState newWaterMeterState = new WaterMeterState(user.getWaterMeter().getId(), state);

        waterMeterStateRepository.createWaterMeterState(newWaterMeterState, _void -> {
            loadWaterMeter(user.getId());
        });
    }

    public void editWaterMeterState(WaterMeterState newState) {
        User user = userLiveData.getValue();
        if(user == null) return;

        waterMeterStateRepository.editWaterMeterState(newState, _void -> {
            loadWaterMeter(user.getId());
        });
    }

    public void deleteWaterMeterState(String id) {
        User user = userLiveData.getValue();
        if(user == null) return;

        waterMeterStateRepository.deleteWaterMeterState(id, _void -> {
            loadWaterMeter(user.getId());
        });
    }
}
