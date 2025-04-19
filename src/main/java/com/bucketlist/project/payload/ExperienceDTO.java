package com.bucketlist.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private Long experienceId;
    private String experienceName;
    private Long categoryId;
    private String experienceImage;
    private String description;
    private Long createdBy;
    private Long lastModifiedBy;

}
