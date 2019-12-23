package com.cegis.deltaplan2100.ui.envdis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EnvDisViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public EnvDisViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is environment and disaster fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
