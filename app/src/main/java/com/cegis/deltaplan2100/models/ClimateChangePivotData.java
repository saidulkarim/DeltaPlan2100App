package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClimateChangePivotData {

    @SerializedName("scenario_data_year")
    @Expose
    private Integer scenarioDataYear;

    @SerializedName("moderate")
    @Expose
    private Double moderate;

    @SerializedName("productive")
    @Expose
    private Double productive;

    @SerializedName("active")
    @Expose
    private Double active;

    @SerializedName("resilient")
    @Expose
    private Double resilient;

    @SerializedName("error_status")
    @Expose
    private Object errorStatus;

    @SerializedName("error_msg")
    @Expose
    private Object errorMsg;

    public Integer getScenarioDataYear() {
        return scenarioDataYear;
    }

    public void setScenarioDataYear(Integer scenarioDataYear) {
        this.scenarioDataYear = scenarioDataYear;
    }

    public Double getModerate() {
        return moderate;
    }

    public void setModerate(Double moderate) {
        this.moderate = moderate;
    }

    public Double getProductive() {
        return productive;
    }

    public void setProductive(Double productive) {
        this.productive = productive;
    }

    public Double getActive() {
        return active;
    }

    public void setActive(Double active) {
        this.active = active;
    }

    public Double getResilient() {
        return resilient;
    }

    public void setResilient(Double resilient) {
        this.resilient = resilient;
    }

    public Object getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(Object errorStatus) {
        this.errorStatus = errorStatus;
    }

    public Object getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Object errorMsg) {
        this.errorMsg = errorMsg;
    }

}