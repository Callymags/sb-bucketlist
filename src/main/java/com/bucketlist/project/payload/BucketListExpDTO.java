package com.bucketlist.project.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BucketListExpDTO {
    private Long bucketListExperienceId;
    private BucketListDTO bucketList;
    private ExperienceDTO experience;
    private LocalDate dateSaved;
    private boolean completed;
}
