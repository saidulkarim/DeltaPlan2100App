package com.cegis.deltaplan2100.ui.socioeconomic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SocioEconomicViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public SocioEconomicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is socio economic fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
