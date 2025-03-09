package com.bucketlist.project.service;

import com.bucketlist.project.payload.ExperienceDTO;
import com.bucketlist.project.payload.ExperienceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExperienceService {
    ExperienceDTO addExperience(Long categoryId, ExperienceDTO experience, Long userId);

    ExperienceResponse getAllExperiences(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ExperienceResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ExperienceResponse searchExperienceByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ExperienceDTO updateExperience(Long experienceId, ExperienceDTO experience, Long userId);

    ExperienceDTO deleteExperience(Long experienceId, Long userId);

    ExperienceDTO updateExperienceImage(Long experienceId, MultipartFile image) throws IOException;
}
