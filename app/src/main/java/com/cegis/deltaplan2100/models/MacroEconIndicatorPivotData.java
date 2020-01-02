package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MacroEconIndicatorPivotData {

    @SerializedName("indicator_name")
    @Expose
    private String indicatorName;
    @SerializedName("indicator_type")
    @Expose
    private String indicatorType;
    @SerializedName("FY2016")
    @Expose
    private Double fY2016;
    @SerializedName("FY2020")
    @Expose
    private Double fY2020;
    @SerializedName("FY2021")
    @Expose
    private Double fY2021;
    @SerializedName("FY2025")
    @Expose
    private Double fY2025;
    @SerializedName("FY2026")
    @Expose
    private Double fY2026;
    @SerializedName("FY2030")
    @Expose
    private Double fY2030;
    @SerializedName("FY2031")
    @Expose
    private Double fY2031;
    @SerializedName("FY2035")
    @Expose
    private Double fY2035;
    @SerializedName("FY2036")
    @Expose
    private Double fY2036;
    @SerializedName("FY2040")
    @Expose
    private Double fY2040;
    @SerializedName("FY2041")
    @Expose
    private Double fY2041;
    @SerializedName("error")
    @Expose
    private Object error;

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public Double getFY2016() {
        return fY2016;
    }

    public void setFY2016(Double fY2016) {
        this.fY2016 = fY2016;
    }

    public Double getFY2020() {
        return fY2020;
    }

    public void setFY2020(Double fY2020) {
        this.fY2020 = fY2020;
    }

    public Double getFY2021() {
        return fY2021;
    }

    public void setFY2021(Double fY2021) {
        this.fY2021 = fY2021;
    }

    public Double getFY2025() {
        return fY2025;
    }

    public void setFY2025(Double fY2025) {
        this.fY2025 = fY2025;
    }

    public Double getFY2026() {
        return fY2026;
    }

    public void setFY2026(Double fY2026) {
        this.fY2026 = fY2026;
    }

    public Double getFY2030() {
        return fY2030;
    }

    public void setFY2030(Double fY2030) {
        this.fY2030 = fY2030;
    }

    public Double getFY2031() {
        return fY2031;
    }

    public void setFY2031(Double fY2031) {
        this.fY2031 = fY2031;
    }

    public Double getFY2035() {
        return fY2035;
    }

    public void setFY2035(Double fY2035) {
        this.fY2035 = fY2035;
    }

    public Double getFY2036() {
        return fY2036;
    }

    public void setFY2036(Double fY2036) {
        this.fY2036 = fY2036;
    }

    public Double getFY2040() {
        return fY2040;
    }

    public void setFY2040(Double fY2040) {
        this.fY2040 = fY2040;
    }

    public Double getFY2041() {
        return fY2041;
    }

    public void setFY2041(Double fY2041) {
        this.fY2041 = fY2041;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}