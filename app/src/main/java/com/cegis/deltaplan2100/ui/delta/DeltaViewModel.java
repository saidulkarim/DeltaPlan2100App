package com.cegis.deltaplan2100.ui.delta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeltaViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public DeltaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is delta fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}