package com.bucketlist.project.repositories;

import com.bucketlist.project.model.Category;
import com.bucketlist.project.model.Experience;
import com.bucketlist.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    Page<Experience> findByCategory(Category category, Pageable pageDetails);

    Page<Experience> findByExperienceNameLikeIgnoreCase(String s, Pageable keyword);

    Page<Experience> findByCreatedBy(User user, Pageable pageable);
}
