package com.finance.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Domain Entity Model]
 * Represents dynamic application-wide configuration settings and feature toggles.
 * Editable by Admins from the /admin/settings panel.
 * ============================================================================
 */
public class SystemSetting {

    private int id;
    private String settingKey;
    private String settingValue;
    private Integer updatedBy;
    private LocalDateTime updatedAt;

    public SystemSetting() {
    }

    public SystemSetting(int id, String settingKey, String settingValue, Integer updatedBy, LocalDateTime updatedAt) {
        this.id = id;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
