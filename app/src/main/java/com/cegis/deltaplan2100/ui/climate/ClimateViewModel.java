package com.cegis.deltaplan2100.ui.climate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClimateViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ClimateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Climate fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
