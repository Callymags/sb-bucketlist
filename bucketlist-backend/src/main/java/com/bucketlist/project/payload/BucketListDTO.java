package com.bucketlist.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BucketListDTO {
    private Long bucketListId;
//    private List<ExperienceDTO> experiences = new ArrayList<>();
    private List<BucketListExpDTO> bucketListExps = new ArrayList<>();
}
