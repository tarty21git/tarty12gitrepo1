package com.cth.pay.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
public class SystemSettingEntity {

    @Id
    private String settingKey;

    @Column(nullable = false)
    private String settingValue;

    private String description;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public SystemSettingEntity() {}

    public SystemSettingEntity(String settingKey, String settingValue, String description) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
    }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
        this.updatedAt = LocalDateTime.now();
    }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
