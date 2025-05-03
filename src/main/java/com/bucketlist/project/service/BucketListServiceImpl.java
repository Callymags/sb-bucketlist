package com.bucketlist.project.service;

import com.bucketlist.project.exceptions.APIException;
import com.bucketlist.project.exceptions.PermissionDeniedException;
import com.bucketlist.project.exceptions.ResourceNotFoundException;
import com.bucketlist.project.model.*;
import com.bucketlist.project.payload.BucketListDTO;
import com.bucketlist.project.payload.BucketListExpDTO;
import com.bucketlist.project.payload.ExperienceDTO;
import com.bucketlist.project.repositories.BucketListExpRepository;
import com.bucketlist.project.repositories.BucketListRepository;
import com.bucketlist.project.repositories.ExperienceRepository;
import com.bucketlist.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BucketListServiceImpl implements BucketListService {

    @Autowired
    private BucketListRepository bucketListRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private BucketListExpRepository bucketListExpRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public BucketListDTO addExpToBucketList(Long experienceId) {
        BucketList bucketList = createBucketList();

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "experienceId", experienceId));

        BucketListExp existingExp = bucketListExpRepository.findByBucketListExpIdAndBucketListId(bucketList.getBucketListId(), experienceId);

        if (existingExp != null) {
            throw new APIException(experience.getExperienceName() + " already exists in your bucket list.");
        }

        BucketListExp newBucketListExp = new BucketListExp();
        newBucketListExp.setExperience(experience);
        newBucketListExp.setBucketList(bucketList);
        newBucketListExp.setDateSaved(java.time.LocalDate.now());
        newBucketListExp.setCompleted(false);

        bucketListExpRepository.save(newBucketListExp);

        // Maintain in-memory relationship
        bucketList.getBucketListExps().add(newBucketListExp);

        // Map bucket list to DTO
        BucketListDTO bucketListDTO = modelMapper.map(bucketList, BucketListDTO.class);

        // Map each BucketListExp to BucketListExpDTO
        List<BucketListExpDTO> bucketListExpDTOs = bucketList.getBucketListExps().stream()
                .map(exp -> {
                    ExperienceDTO experienceDTO = modelMapper.map(exp.getExperience(), ExperienceDTO.class);
                    BucketListExpDTO bucketListExpDTO = new BucketListExpDTO();
                    bucketListExpDTO.setBucketListExperienceId(exp.getBucketListExpId());
                    bucketListExpDTO.setBucketList(null); // prevent recursion
                    bucketListExpDTO.setExperience(experienceDTO);
                    bucketListExpDTO.setDateSaved(exp.getDateSaved());
                    bucketListExpDTO.setCompleted(exp.isCompleted());
                    return bucketListExpDTO;
                })
                .collect(Collectors.toList());

        bucketListDTO.setBucketListExps(bucketListExpDTOs);

        return bucketListDTO;
    }

    @Override
    public List<BucketListDTO> getBucketLists() {
        User currentUser = authUtil.loggedInUser();

        if (!currentUser.getRole().getRoleName().equals(AppRole.ROLE_ADMIN)) {
            throw new PermissionDeniedException("User", currentUser.getUserId(), "bucket lists", "view all");
        }

        List<BucketList> bucketLists = bucketListRepository.findAll();

        if (bucketLists.isEmpty()) {
            throw new APIException("No bucket lists exist");
        }

        List<BucketListDTO> bucketListDTOs = bucketLists.stream().map(bucketList -> {
            BucketListDTO bucketListDTO = modelMapper.map(bucketList, BucketListDTO.class);

            List<BucketListExpDTO> bucketListExpDTOs = bucketList.getBucketListExps().stream()
                    .map(exp -> {
                        ExperienceDTO experienceDTO = modelMapper.map(exp.getExperience(), ExperienceDTO.class);
                        BucketListExpDTO bucketListExpDTO = new BucketListExpDTO();
                        bucketListExpDTO.setBucketListExperienceId(exp.getBucketListExpId());
                        bucketListExpDTO.setBucketList(null); // prevent recursion
                        bucketListExpDTO.setExperience(experienceDTO);
                        bucketListExpDTO.setDateSaved(exp.getDateSaved());
                        bucketListExpDTO.setCompleted(exp.isCompleted());
                        return bucketListExpDTO;
                    })
                    .collect(Collectors.toList());

            bucketListDTO.setBucketListExps(bucketListExpDTOs);

            return bucketListDTO;
        }).collect(Collectors.toList());

        return bucketListDTOs;
    }

//    @Override
//    public BucketListDTO getUserBucketList(Long bucketListId) {
//        String loggedInEmail = authUtil.loggedInEmail();
//        BucketList bucketList = bucketListRepository.findBucketListByEmailAndBucketListID(loggedInEmail, bucketListId);
//
//        if (bucketList == null) {
//            throw new ResourceNotFoundException("BucketList", "bucketListId", bucketListId);
//        }
//
//        BucketListDTO bucketListDTO = modelMapper.map(bucketList, BucketListDTO.class);
//
//        List<BucketListExpDTO> bucketListExpDTOs = bucketList.getBucketListExps().stream()
//                .map(exp -> {
//                    ExperienceDTO experienceDTO = modelMapper.map(exp.getExperience(), ExperienceDTO.class);
//                    BucketListExpDTO bucketListExpDTO = new BucketListExpDTO();
//                    bucketListExpDTO.setBucketListExperienceId(exp.getBucketListExpId());
//                    bucketListExpDTO.setBucketList(null); // to avoid infinite recursion
//                    bucketListExpDTO.setExperience(experienceDTO);
//                    bucketListExpDTO.setDateSaved(exp.getDateSaved());
//                    bucketListExpDTO.setCompleted(exp.isCompleted());
//                    return bucketListExpDTO;
//                })
//                .collect(Collectors.toList());
//
//        bucketListDTO.setBucketListExps(bucketListExpDTOs);
//
//        return bucketListDTO;
//    }

    @Override
    public BucketListDTO getUserBucketList() {
        String loggedInEmail = authUtil.loggedInEmail();
        BucketList bucketList = bucketListRepository.findBucketListByEmail(loggedInEmail);

        if (bucketList == null) {
            throw new ResourceNotFoundException("BucketList", "email", loggedInEmail);
        }

        BucketListDTO bucketListDTO = modelMapper.map(bucketList, BucketListDTO.class);

        List<BucketListExpDTO> bucketListExpDTOs = bucketList.getBucketListExps().stream()
                .map(exp -> {
                    ExperienceDTO experienceDTO = modelMapper.map(exp.getExperience(), ExperienceDTO.class);
                    BucketListExpDTO bucketListExpDTO = new BucketListExpDTO();
                    bucketListExpDTO.setBucketListExperienceId(exp.getBucketListExpId());
                    bucketListExpDTO.setBucketList(null); // prevent recursion
                    bucketListExpDTO.setExperience(experienceDTO);
                    bucketListExpDTO.setDateSaved(exp.getDateSaved());
                    bucketListExpDTO.setCompleted(exp.isCompleted());
                    return bucketListExpDTO;
                })
                .collect(Collectors.toList());

        bucketListDTO.setBucketListExps(bucketListExpDTOs);
        return bucketListDTO;
    }


    @Transactional
    @Override
    public BucketListDTO updateBucketListExpStatus(Long bucketListId, Long bucketListExpId, boolean completed) {
        String emailId = authUtil.loggedInEmail();
        BucketList userBucketList = bucketListRepository.findBucketListByEmail(emailId);

        if (userBucketList == null) {
            throw new ResourceNotFoundException("Bucket List", "email", emailId);
        }

        // Verify that the logged-in user owns this bucket list
        if (!userBucketList.getBucketListId().equals(bucketListId)) {
            throw new APIException("You do not have permission to modify this bucket list.");
        }

        // Find the specific BucketListExp by bucketListId and bucketListExpId
        BucketListExp bucketListExp = bucketListExpRepository.findById(bucketListExpId)
                .orElseThrow(() -> new ResourceNotFoundException("BucketListExp", "bucketListExpId", bucketListExpId));

        // Extra check to make sure the experience belongs to this user's bucket list
        if (!bucketListExp.getBucketList().getBucketListId().equals(bucketListId)) {
            throw new APIException("This experience does not belong to your bucket list.");
        }

        // Update the status
        bucketListExp.setCompleted(completed);
        bucketListExpRepository.save(bucketListExp);

        // Map the updated bucket list
        BucketListDTO bucketListDTO = modelMapper.map(userBucketList, BucketListDTO.class);

        List<BucketListExpDTO> bucketListExpDTOs = userBucketList.getBucketListExps().stream()
                .map(exp -> {
                    ExperienceDTO experienceDTO = modelMapper.map(exp.getExperience(), ExperienceDTO.class);
                    BucketListExpDTO bucketListExpDTO = new BucketListExpDTO();
                    bucketListExpDTO.setBucketListExperienceId(exp.getBucketListExpId());
                    bucketListExpDTO.setBucketList(null); // prevent recursion
                    bucketListExpDTO.setExperience(experienceDTO);
                    bucketListExpDTO.setDateSaved(exp.getDateSaved());
                    bucketListExpDTO.setCompleted(exp.isCompleted());
                    return bucketListExpDTO;
                })
                .collect(Collectors.toList());

        bucketListDTO.setBucketListExps(bucketListExpDTOs);

        return bucketListDTO;
    }

    @Override
    @Transactional
    public String deleteExpFromBucketList(Long bucketListId, Long bucketListExpId) {
        User currentUser = authUtil.loggedInUser();

        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "bucketListId", bucketListId));

        // Permission check
        if (!bucketList.getUser().getUserId().equals(currentUser.getUserId()) &&
                !currentUser.getRole().getRoleName().equals(AppRole.ROLE_ADMIN)) {
            throw new PermissionDeniedException("User", currentUser.getUserId(), "bucket list", "delete experience from");
        }

        BucketListExp bucketListExp = bucketListExpRepository.findById(bucketListExpId)
                .orElseThrow(() -> new ResourceNotFoundException("BucketListExp", "bucketListExpId", bucketListExpId));

        if (!bucketListExp.getBucketList().getBucketListId().equals(bucketListId)) {
            throw new APIException("This experience does not belong to the specified bucket list.");
        }

        // Use custom delete query
        bucketListExpRepository.deleteByBucketListExpIdAndBucketListId(bucketListExpId, bucketListId);

        return "Experience " + bucketListExp.getExperience().getExperienceName() + " removed from the bucket list.";
    }

//    @Override
//    public String deleteExpFromBucketList(Long bucketListId, Long experienceId) {
//        User currentUser = authUtil.loggedInUser();
//
//        BucketList bucketList = bucketListRepository.findById(bucketListId)
//                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "bucketListId", bucketListId));
//
//        // Check if the current user owns the bucket list or is an admin
//        if (!bucketList.getUser().getUserId().equals(currentUser.getUserId()) &&
//                !currentUser.getRole().getRoleName().equals(AppRole.ROLE_ADMIN)) {
//            throw new PermissionDeniedException("User", currentUser.getUserId(), "bucket list", "delete experience from");
//        }
//
//        BucketListExp bucketListExp = bucketListExpRepository.findByBucketListExpIdAndBucketListId(bucketListId, experienceId);
//
//        if (bucketListExp == null) {
//            throw new ResourceNotFoundException("Experience", "experienceId", experienceId);
//        }
//
//        bucketListExpRepository.deleteBucketListExpByExpIdAndBucketListId(bucketListId, experienceId);
//
//        return "Experience " + bucketListExp.getExperience().getExperienceName() + " removed from the bucket list.";
//    }

    private BucketList createBucketList() {
        BucketList userBucketList = bucketListRepository.findBucketListByEmail((authUtil.loggedInEmail()));
        if(userBucketList != null){
            return userBucketList;
        }

        BucketList bucketList = new BucketList();
        bucketList.setUser(authUtil.loggedInUser());
        BucketList newBucketList = bucketListRepository.save(bucketList);

        return newBucketList;
    }
}
