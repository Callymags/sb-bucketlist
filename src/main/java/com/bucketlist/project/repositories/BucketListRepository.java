package com.bucketlist.project.repositories;

import com.bucketlist.project.model.BucketList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BucketListRepository extends JpaRepository<BucketList, Long> {
    @Query("Select bl from BucketList bl where bl.user.email = ?1")
    BucketList findBucketListByEmail(String email);

    @Query("Select bl from BucketList bl where bl.user.email = ?1 and bl.bucketListId = ?2")
    BucketList findBucketListByEmailAndBucketListID(String emailId, Long bucketListId);
}
