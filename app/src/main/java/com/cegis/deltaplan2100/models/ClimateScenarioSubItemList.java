package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClimateScenarioSubItemList {

    @SerializedName("subitem_id")
    @Expose
    private Integer subitemId;

    @SerializedName("subitem_name")
    @Expose
    private String subitemName;

    @SerializedName("subitem_unit")
    @Expose
    private String subitemUnit;

    @SerializedName("subitem_description")
    @Expose
    private String subitemDescription;

    @SerializedName("error_status")
    @Expose
    private Object errorStatus;

    @SerializedName("error_msg")
    @Expose
    private Object errorMsg;

    public Integer getSubitemId() {
        return subitemId;
    }

    public void setSubitemId(Integer subitemId) {
        this.subitemId = subitemId;
    }

    public String getSubitemName() {
        return subitemName;
    }

    public void setSubitemName(String subitemName) {
        this.subitemName = subitemName;
    }

    public String getSubitemUnit() {
        return subitemUnit;
    }

    public void setSubitemUnit(String subitemUnit) {
        this.subitemUnit = subitemUnit;
    }

    public String getSubitemDescription() {
        return subitemDescription;
    }

    public void setSubitemDescription(String subitemDescription) {
        this.subitemDescription = subitemDescription;
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