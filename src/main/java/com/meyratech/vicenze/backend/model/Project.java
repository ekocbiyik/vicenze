package com.meyratech.vicenze.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Random;

/**
 * ekocbiyik on 4/28/19
 */
@Entity
@Table(name = "t_project")
public class Project extends AbstractEntity {

    @NotBlank
    @Column(name = "project_name", nullable = false)
    private String projectName;

    @NotBlank
    @Column(name = "company", nullable = false)
    private String company;

    @NotEmpty
    @Email
    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // aktivate-deactivate

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
