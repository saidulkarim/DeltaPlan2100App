package com.cegis.deltaplan2100.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelComponentLevelThree {
    @SerializedName("ComponentLevel3Id")
    @Expose
    private Integer componentLevel3Id;

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

    public Integer getComponentLevel3Id() {
        return componentLevel3Id;
    }

    public void setComponentLevel3Id(Integer componentLevel3Id) {
        this.componentLevel3Id = componentLevel3Id;
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