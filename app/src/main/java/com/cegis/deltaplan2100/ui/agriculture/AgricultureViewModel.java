package com.cegis.deltaplan2100.ui.agriculture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.esri.arcgisruntime.mapping.view.MapView;

public class AgricultureViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public AgricultureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is agriculture fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}