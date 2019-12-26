package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelComponentLevelTwo {
    @SerializedName("ComponentLevel2Id")
    @Expose
    private Integer componentLevel2Id;

    @SerializedName("ParentId")
    @Expose
    private Integer parentId;

    @SerializedName("ComponentName")
    @Expose
    private String componentName;

    @SerializedName("DataVisualization")
    @Expose
    private String dataVisualization;

    @SerializedName("IsActive")
    @Expose
    private Boolean isActive;

    @SerializedName("IsDelete")
    @Expose
    private Boolean isDelete;

    public Integer getComponentLevel2Id() {
        return componentLevel2Id;
    }

    public void setComponentLevel2Id(Integer componentLevel2Id) {
        this.componentLevel2Id = componentLevel2Id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getDataVisualization() {
        return dataVisualization;
    }

    public void setDataVisualization(String dataVisualization) {
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