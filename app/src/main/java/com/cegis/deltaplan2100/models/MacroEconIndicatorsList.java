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

    @SerializedName("VisualOrder")
    @Expose
    private Integer visualOrder;

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

    public Integer getVisualOrder() {
        return visualOrder;
    }

    public void setVisualOrder(Integer visualOrder) {
        this.visualOrder = visualOrder;
    }
}