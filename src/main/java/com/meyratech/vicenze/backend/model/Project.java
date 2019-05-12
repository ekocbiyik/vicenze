package com.meyratech.vicenze.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ekocbiyik on 4/28/19
 */
@Entity
@Table(name = "t_project")
public class Project extends AbstractEntity {

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String projectName;

    @Size(max = 255)
    @Column(name = "password")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // aktivate-deactivate

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
