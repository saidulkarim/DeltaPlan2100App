package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MacroEconIndicatorsList {

    @SerializedName("IndicatorName")
    @Expose
    private String indicatorName;
    @SerializedName("IndicatorType")
    @Expose
    private Integer indicatorType;

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public Integer getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(Integer indicatorType) {
        this.indicatorType = indicatorType;
    }

}