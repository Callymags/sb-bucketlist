package com.bucketlist.project.service;

import com.bucketlist.project.payload.ExperienceDTO;
import com.bucketlist.project.payload.ExperienceResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExperienceService {
    ExperienceDTO addExperience(Long categoryId, ExperienceDTO experience, Authentication authentication);

    ExperienceResponse getAllExperiences(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ExperienceResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ExperienceResponse searchExperienceByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ExperienceDTO updateExperience(Long experienceId, ExperienceDTO experience, Authentication authentication);

    ExperienceDTO deleteExperience(Long experienceId, Authentication authentication);

    ExperienceDTO updateExperienceImage(Long experienceId, MultipartFile image, Authentication authentication) throws IOException;
}
