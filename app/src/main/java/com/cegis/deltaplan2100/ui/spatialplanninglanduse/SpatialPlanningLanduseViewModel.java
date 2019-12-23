package com.cegis.deltaplan2100.ui.spatialplanninglanduse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SpatialPlanningLanduseViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public SpatialPlanningLanduseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Spatial Planning Landuse fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
