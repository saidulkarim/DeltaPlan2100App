package com.cegis.deltaplan2100.ui.waterresource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WaterResourceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WaterResourceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Water Resource fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}