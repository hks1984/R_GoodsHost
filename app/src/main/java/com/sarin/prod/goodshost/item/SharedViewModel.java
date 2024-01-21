package com.sarin.prod.goodshost.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> favoriteStatus = new MutableLiveData<>();

    public void setFavorite(boolean isFavorite) {
        favoriteStatus.setValue(isFavorite);
    }

    public LiveData<Boolean> getFavoriteStatus() {
        return favoriteStatus;
    }
}