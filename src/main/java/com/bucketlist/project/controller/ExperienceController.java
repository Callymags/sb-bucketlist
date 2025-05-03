package com.bucketlist.project.controller;

import com.bucketlist.project.config.AppConstants;
import com.bucketlist.project.payload.ExperienceDTO;
import com.bucketlist.project.payload.ExperienceResponse;
import com.bucketlist.project.service.ExperienceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ExperienceController {

    @Autowired
    ExperienceService experienceService;

    @PostMapping("/categories/{categoryId}/experience")
    public ResponseEntity<ExperienceDTO> addExperience (@Valid @RequestBody ExperienceDTO experienceDTO,
                                                        @PathVariable Long categoryId){
        ExperienceDTO savedExperienceDTO = experienceService.addExperience(categoryId, experienceDTO);
        return new ResponseEntity<>(savedExperienceDTO, HttpStatus.CREATED);
    }

    @GetMapping("/experiences")
    public ResponseEntity<ExperienceResponse> getAllExperiences(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_EXPERIENCES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_EXPERIENCES_ORDER, required = false) String sortOrder
    ) {
        ExperienceResponse experienceResponse = experienceService.getAllExperiences(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(experienceResponse, HttpStatus.OK);
    }

    @GetMapping("/categories/{categoryId}/experiences")
    public ResponseEntity<ExperienceResponse> getExperiencesByCategory(@PathVariable Long categoryId,
                                                                       @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                       @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                       @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_EXPERIENCES_BY, required = false) String sortBy,
                                                                       @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_EXPERIENCES_ORDER, required = false) String sortOrder) {
        ExperienceResponse experienceResponse = experienceService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(experienceResponse, HttpStatus.OK);
    }

    @GetMapping("/experiences/keyword/{keyword}")
    public ResponseEntity<ExperienceResponse> getExperienceByKeyword(@PathVariable String keyword,
                                                                     @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                     @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                     @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_EXPERIENCES_BY, required = false) String sortBy,
                                                                     @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_EXPERIENCES_ORDER, required = false) String sortOrder){
        ExperienceResponse experienceResponse = experienceService.searchExperienceByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(experienceResponse, HttpStatus.OK);
    }

    @PutMapping ("/experience/{experienceId}")
    public ResponseEntity<ExperienceDTO> updateExperience(@Valid @RequestBody ExperienceDTO experienceDTO,
                                                          @PathVariable Long experienceId){
        ExperienceDTO updatedExperienceDTO = experienceService.updateExperience(experienceId, experienceDTO);
        return new ResponseEntity<>(updatedExperienceDTO, HttpStatus.OK);
    }

//    @DeleteMapping("/public/experience/{experienceId}")
//    public ResponseEntity<ExperienceDTO> deleteExperience(@PathVariable Long experienceId,
//                                                          Authentication authentication){
//        ExperienceDTO deletedExperience = experienceService.deleteExperience(experienceId, authentication);
//        return new ResponseEntity<>(deletedExperience, HttpStatus.OK);
//    }

    @DeleteMapping("/experience/{experienceId}")
    public ResponseEntity<ExperienceDTO> deleteExperience(@PathVariable Long experienceId) {
        ExperienceDTO deletedExperience = experienceService.deleteExperience(experienceId);
        return new ResponseEntity<>(deletedExperience, HttpStatus.OK);
    }

    @PutMapping("/experience/{experienceId}/image")
    public ResponseEntity<ExperienceDTO> updateExperienceImage(@PathVariable Long experienceId,
                                                               @RequestParam("image") MultipartFile image) throws IOException {
        ExperienceDTO updatedExperience = experienceService.updateExperienceImage(experienceId, image);
        return new ResponseEntity<>(updatedExperience, HttpStatus.OK);
    }
}
