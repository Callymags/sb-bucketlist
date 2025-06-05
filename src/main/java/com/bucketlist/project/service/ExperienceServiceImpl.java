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
            mapper.map(src -> src.getCategory().getCategoryId(), ExperienceDTO::setCategoryName);
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
    public ExperienceDTO getExperienceById(Long experienceId) {
        Experience experience = getExperienceEntityById(experienceId);
        return mapExperience(experience);
    }

    @Override
    public ExperienceResponse getAllExperiences(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy, sortOrder));
        Page<Experience> page = experienceRepository.findAll(pageable);

        if (page.isEmpty()) {
            throw new APIException("No experiences exist");
        }

        List<ExperienceDTO> experienceDTOS = mapExperiences(page.getContent());
        return buildExperienceResponse(page, experienceDTOS);
    }

    @Override
    public ExperienceResponse getExperiencesCreatedByUser(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        User currentUser = authUtil.loggedInUser();

        User resourceOwner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        authUtil.checkOwnerOrAdmin(currentUser, resourceOwner, "experience", "view");


        Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy, sortOrder));
        Page<Experience> page = experienceRepository.findByCreatedBy(currentUser, pageable);

        List<ExperienceDTO> experienceDTOS = mapExperiences(page.getContent());
        return buildExperienceResponse(page, experienceDTOS);
    }


    @Override
    public ExperienceResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy, sortOrder));
        Page<Experience> page = experienceRepository.findByCategory(category, pageable);

        if (page.isEmpty()) {
            throw new APIException("No experiences found for the given category");
        }

        List<ExperienceDTO> experienceDTOS = mapExperiences(page.getContent());
        return buildExperienceResponse(page, experienceDTOS);
    }

    @Override
    public ExperienceResponse searchExperienceByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy, sortOrder));
        Page<Experience> page = experienceRepository.findByExperienceNameLikeIgnoreCase('%' + keyword + '%', pageable);

        if (page.isEmpty()) {
            throw new APIException("Experiences not found with keyword: " + keyword);
        }

        List<ExperienceDTO> experienceDTOS = mapExperiences(page.getContent());
        return buildExperienceResponse(page, experienceDTOS);
    }

    @Override
    public ExperienceDTO updateExperience(Long experienceId, ExperienceDTO experienceDTO) {
        Experience experienceFromDB = getExperienceEntityById(experienceId);
        User currentUser = authUtil.loggedInUser();
        authUtil.checkOwnerOrAdmin(currentUser, experienceFromDB.getCreatedBy(), "experience", "update");

        Experience experience = modelMapper.map(experienceDTO, Experience.class);

        experienceFromDB.setExperienceName(experience.getExperienceName());
        experienceFromDB.setDescription(experience.getDescription());
        experienceFromDB.setExperienceImage(experience.getExperienceImage());
        experienceFromDB.setLastModifiedBy(currentUser);

        Experience savedExperience = experienceRepository.save(experienceFromDB);

        return modelMapper.map(savedExperience, ExperienceDTO.class);
    }

    @Override
    @Transactional
    public ExperienceDTO deleteExperience(Long experienceId) {
        Experience experience = getExperienceEntityById(experienceId);

        User currentUser = authUtil.loggedInUser();
        authUtil.checkOwnerOrAdmin(currentUser, experience.getCreatedBy(), "experience", "delete");

        // Delete related bucket list entries
        bucketListExpRepository.deleteAllByExperienceId(experience.getExperienceId());

        experienceRepository.delete(experience);

        return modelMapper.map(experience, ExperienceDTO.class);
    }


    @Override
    public ExperienceDTO updateExperienceImage(Long experienceId, MultipartFile image) throws IOException {
        Experience experience = getExperienceEntityById(experienceId);
        User currentUser = authUtil.loggedInUser();
        authUtil.checkOwnerOrAdmin(currentUser, experience.getCreatedBy(), "experience", "update");


        String fileName = fileService.uploadImage(uploadDir, image);
        experience.setExperienceImage(fileName);
        Experience updatedExperience = experienceRepository.save(experience);
        return modelMapper.map(updatedExperience, ExperienceDTO.class);
    }

    private Experience getExperienceEntityById(Long experienceId) {
        return experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "experienceId", experienceId));
    }

    private ExperienceDTO mapExperience(Experience experience) {
        ExperienceDTO dto = new ExperienceDTO();
        dto.setExperienceId(experience.getExperienceId());
        dto.setExperienceName(experience.getExperienceName());
        dto.setCategoryName(experience.getCategory().getCategoryName());
        dto.setExperienceImage(experience.getExperienceImage());
        dto.setDescription(experience.getDescription());
        dto.setCreatedBy(
                experience.getCreatedBy() != null ? experience.getCreatedBy().getUsername() : "Unknown"
        );
        dto.setLastModifiedBy(
                experience.getLastModifiedBy() != null ? experience.getLastModifiedBy().getUsername() : "Unknown"
        );
        return dto;
    }

    private List<ExperienceDTO> mapExperiences(List<Experience> experiences) {
        return experiences.stream().map(this::mapExperience).collect(Collectors.toList());
    }

    private Sort buildSort(String sortBy, String sortOrder) {
        return sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    private ExperienceResponse buildExperienceResponse(Page<?> page, List<ExperienceDTO> experienceDTOS) {
        ExperienceResponse response = new ExperienceResponse();
        response.setContent(experienceDTOS);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }


}
