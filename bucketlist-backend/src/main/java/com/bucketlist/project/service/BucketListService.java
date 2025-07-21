package com.bucketlist.project.service;

import com.bucketlist.project.model.Experience;
import com.bucketlist.project.payload.BucketListDTO;

import java.util.List;

public interface BucketListService {
    BucketListDTO addExpToBucketList(Long experienceId);

    List<BucketListDTO> getBucketLists();

    BucketListDTO getUserBucketList();

//    BucketListDTO getUserBucketList(Long bucketListId);

    String deleteExpFromBucketList(Long bucketListId, Long experienceId);

    BucketListDTO updateBucketListExpStatus(Long bucketListId, Long bucketListExpId, boolean completed);

}
