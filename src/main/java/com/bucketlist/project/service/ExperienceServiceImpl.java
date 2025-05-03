package com.bucketlist.project.service;

import com.bucketlist.project.exceptions.APIException;
import com.bucketlist.project.exceptions.PermissionDeniedException;
import com.bucketlist.project.exceptions.ResourceNotFoundException;
import com.bucketlist.project.model.*;
import com.bucketlist.project.payload.ExperienceDTO;
import com.bucketlist.project.payload.ExperienceResponse;
import com.bucketlist.project.repositories.BucketListExpRepository;
import com.bucketlist.project.repositories.CategoryRepository;
import com.bucketlist.project.repositories.ExperienceRepository;
import com.bucketlist.project.repositories.UserRepository;
import com.bucketlist.project.util.AuthUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private BucketListExpRepository bucketListExpRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void setupModelMapper() {
        modelMapper.typeMap(Experience.class, ExperienceDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getCreatedBy().getUserId(), ExperienceDTO::setCreatedBy);
            mapper.map(src -> src.getLastModifiedBy().getUserId(), ExperienceDTO::setLastModifiedBy);
            mapper.map(src -> src.getCategory().getCategoryId(), ExperienceDTO::setCategoryId);
            mapper.map(Experience::getExperienceImage, ExperienceDTO::setExperienceImage); // optional
        });
    }

    @Override
    public ExperienceDTO addExperience(Long categoryId, ExperienceDTO experienceDTO) {
        User user = authUtil.loggedInUser();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isExpNotPresent = true;

        List<Experience> experiences = category.getExperiences();

        for (Experience value : experiences) {
            if (value.getExperienceName().equals(experienceDTO.getExperienceName())) {
                isExpNotPresent = false;
                break;
            }
        }

        if (isExpNotPresent){
            Experience experience = modelMapper.map(experienceDTO, Experience.class);
            experience.setExperienceImage("default.jpg");
            experience.setCategory(category);
            experience.setCreatedBy(user);
            experience.setLastModifiedBy(user);

            Experience savedExperience = experienceRepository.save(experience);
            return modelMapper.map(savedExperience, ExperienceDTO.class);
        } else {
            throw new APIException("Experience already exists");
        }

    }

    @Override
    public ExperienceResponse getAllExperiences(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Experience> pageExperiences = experienceRepository.findAll(pageDetails);

        List<Experience> experiences = pageExperiences.getContent();

        List<ExperienceDTO> experienceDTOS = experiences.stream()
                .map(experience -> modelMapper.map(experience, ExperienceDTO.class))
                .collect(Collectors.toList());

        if (experiences.isEmpty()){
            throw new APIException("No experiences exist");
        }

        ExperienceResponse experienceResponse = new ExperienceResponse();
        experienceResponse.setContent(experienceDTOS);
        experienceResponse.setPageNumber(pageExperiences.getNumber());
        experienceResponse.setPageSize(pageExperiences.getSize());
        experienceResponse.setTotalElements(pageExperiences.getTotalElements());
        experienceResponse.setTotalPages(pageExperiences.getTotalPages());
        experienceResponse.setLastPage(pageExperiences.isLast());
        return experienceResponse;
    }

    @Override
    public ExperienceResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Experience> pageExperiences = experienceRepository.findByCategory(category, pageDetails);

        List<Experience> experiences = pageExperiences.getContent();

        if (experiences.isEmpty()) {
            throw new APIException("No experiences found for the given category");
        }

        List<ExperienceDTO> experienceDTOS = experiences.stream()
                .map(experience -> modelMapper.map(experience, ExperienceDTO.class))
                .collect(Collectors.toList());

        ExperienceResponse experienceResponse = new ExperienceResponse();
        experienceResponse.setContent(experienceDTOS);
        experienceResponse.setPageNumber(pageExperiences.getNumber());
        experienceResponse.setPageSize(pageExperiences.getSize());
        experienceResponse.setTotalElements(pageExperiences.getTotalElements());
        experienceResponse.setTotalPages(pageExperiences.getTotalPages());
        experienceResponse.setLastPage(pageExperiences.isLast());
        return experienceResponse;
    }

    @Override
    public ExperienceResponse searchExperienceByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Experience> pageExperiences = experienceRepository.findByExperienceNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Experience> experiences = pageExperiences.getContent();
        List<ExperienceDTO> experienceDTOS = experiences.stream()
                .map(experience -> modelMapper.map(experience, ExperienceDTO.class))
                .collect(Collectors.toList());

        if (experiences.isEmpty()){
            throw new APIException("Experiences not found with keyword: " + keyword);
        }

        ExperienceResponse experienceResponse = new ExperienceResponse();
        experienceResponse.setContent(experienceDTOS);
        experienceResponse.setPageNumber(pageExperiences.getNumber());
        experienceResponse.setPageSize(pageExperiences.getSize());
        experienceResponse.setTotalElements(pageExperiences.getTotalElements());
        experienceResponse.setTotalPages(pageExperiences.getTotalPages());
        experienceResponse.setLastPage(pageExperiences.isLast());
        return experienceResponse;
    }

    @Override
    public ExperienceDTO updateExperience(Long experienceId, ExperienceDTO experienceDTO) {
        Experience experienceFromDB = getExperienceById(experienceId);
        User user = authUtil.loggedInUser();;

        // Check if admin or user that added experience before allowing update
        validateUserPermission(user, experienceFromDB, "update");

        Experience experience = modelMapper.map(experienceDTO, Experience.class);

        experienceFromDB.setExperienceName(experience.getExperienceName());
        experienceFromDB.setDescription(experience.getDescription());
        experienceFromDB.setExperienceImage(experience.getExperienceImage());
        experienceFromDB.setLastModifiedBy(user);

        Experience savedExperience = experienceRepository.save(experienceFromDB);

        return modelMapper.map(savedExperience, ExperienceDTO.class);

    }

    @Override
    @Transactional
    public ExperienceDTO deleteExperience(Long experienceId) {
        Experience experience = getExperienceById(experienceId);
        User user = authUtil.loggedInUser();

        validateUserPermission(user, experience, "delete");

        // Delete related bucket list entries
        bucketListExpRepository.deleteAllByExperienceId(experience.getExperienceId());

        // No need to clear() the list if fetch = LAZY and you didn’t touch it

        experienceRepository.delete(experience);

        return modelMapper.map(experience, ExperienceDTO.class);
    }


    @Override
    public ExperienceDTO updateExperienceImage(Long experienceId, MultipartFile image) throws IOException {
        Experience experience = getExperienceById(experienceId);
        User user = authUtil.loggedInUser();;

        // Check if admin or user that added the experience before allowing update
        validateUserPermission(user, experience, "update");

        String fileName = fileService.uploadImage(uploadDir, image);
        experience.setExperienceImage(fileName);
        Experience updatedExperience = experienceRepository.save(experience);
        return modelMapper.map(updatedExperience, ExperienceDTO.class);
    }

    private Experience getExperienceById(Long experienceId) {
        return experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "experienceId", experienceId));
    }

    private void validateUserPermission(User user, Experience experience, String action) {
        boolean isAdmin = user.getRole().getRoleName() == AppRole.ROLE_ADMIN;
        boolean isOwner = experience.getCreatedBy().getUserId().equals(user.getUserId());

        if (!isAdmin && !isOwner) {
            throw new PermissionDeniedException("UserId", user.getUserId(), "experience", action);
        }
    }



}
