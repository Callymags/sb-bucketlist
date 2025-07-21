package com.bucketlist.project.repositories;

import com.bucketlist.project.model.BucketListExp;
import com.bucketlist.project.model.Experience;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BucketListExpRepository extends JpaRepository<BucketListExp, Long> {
    @Query("Select ble from BucketListExp ble Where ble.experience.experienceId = ?1")
    List<BucketListExp> findBucketListExpsByExperienceId(Long experienceId);

    @Query("Select ble from BucketListExp ble where ble.bucketList.bucketListId = ?1 and ble.experience.experienceId = ?2")
    BucketListExp findByBucketListExpIdAndBucketListId(Long bucketListId, Long experienceId);

    @Modifying
    @Transactional
    @Query("Delete from BucketListExp ble where ble.bucketListExpId = ?1 and ble.bucketList.bucketListId = ?2")
    void deleteByBucketListExpIdAndBucketListId(Long bucketListExpId, Long bucketListId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BucketListExp ble WHERE ble.experience.experienceId = ?1")
    void deleteAllByExperienceId(Long experienceId);

}
