package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelComponentLevelOne {
    @SerializedName("ComponentLevel1Id")
    @Expose
    private Integer componentLevel1Id;
    @SerializedName("ComponentName")
    @Expose
    private String componentName;
    @SerializedName("DataVisualization")
    @Expose
    private Object dataVisualization;
    @SerializedName("IsActive")
    @Expose
    private Boolean isActive;
    @SerializedName("IsDelete")
    @Expose
    private Boolean isDelete;

    public Integer getComponentLevel1Id() {
        return componentLevel1Id;
    }

    public void setComponentLevel1Id(Integer componentLevel1Id) {
        this.componentLevel1Id = componentLevel1Id;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Object getDataVisualization() {
        return dataVisualization;
    }

    public void setDataVisualization(Object dataVisualization) {
        this.dataVisualization = dataVisualization;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}