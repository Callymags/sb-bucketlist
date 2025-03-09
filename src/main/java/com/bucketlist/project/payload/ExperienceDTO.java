package com.bucketlist.project.payload;

public class ExperienceDTO {
    private Long experienceId;
    private String experienceName;
    private Long categoryId;
    private String imgAddress;
    private String description;
    private Long addedBy;
    private Long lastModifiedBy;

    public ExperienceDTO() {
    }

    public ExperienceDTO(Long experienceId, String experienceName, Long categoryId, String imgAddress, String description, Long addedBy, Long lastModifiedBy) {
        this.experienceId = experienceId;
        this.experienceName = experienceName;
        this.categoryId = categoryId;
        this.imgAddress = imgAddress;
        this.description = description;
        this.addedBy = addedBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(Long experienceId) {
        this.experienceId = experienceId;
    }

    public String getExperienceName() {
        return experienceName;
    }

    public void setExperienceName(String experienceName) {
        this.experienceName = experienceName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
