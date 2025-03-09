package com.bucketlist.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity(name = "experiences")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "experience_seq")
    @SequenceGenerator(name = "experience_seq", sequenceName = "experience_seq", allocationSize = 1)
    private Long experienceId;

    @NotBlank
    @Size(min = 3, message = "Experience name must contain at least three characters")
    private String experienceName;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private Category category;

    private String imgAddress;

    @NotBlank
    @Size(min = 3, message = "Experience description must contain at least three characters")
    private String description;

    @NotNull
    private Long addedBy;
    @NotNull
    private Long lastModifiedBy;

    public Experience() {
    }

    public Experience(Long experienceId, String experienceName, Category category, String imgAddress, String description, Long addedBy, Long lastModifiedBy) {
        this.experienceId = experienceId;
        this.experienceName = experienceName;
        this.category = category;
        this.imgAddress = imgAddress;
        this.description = description;
        this.addedBy = addedBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getExperienceIdId() {
        return experienceId;
    }

    public void setExperienceId(Long id) {
        this.experienceId = experienceId;
    }

    public String getExperienceName() {
        return experienceName;
    }

    public void setExperienceName(String experienceName) {
        this.experienceName = experienceName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImgAddress() {
        return imgAddress;
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Long addedBy) {
        this.addedBy = addedBy;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
