package com.bucketlist.project.service;

import com.bucketlist.project.exceptions.APIException;
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
        List<BucketListExpDTO> bucketListExpDTOs = mapBucketListExps(bucketList.getBucketListExps());

        bucketListDTO.setBucketListExps(bucketListExpDTOs);

        return bucketListDTO;
    }

    @Override
    public List<BucketListDTO> getBucketLists() {
        authUtil.checkAdmin("bucket lists", "view all");

        List<BucketList> bucketLists = bucketListRepository.findAll();

        if (bucketLists.isEmpty()) {
            throw new APIException("No bucket lists exist");
        }

        List<BucketListDTO> bucketListDTOs = bucketLists.stream().map(bucketList -> {
            BucketListDTO dto = modelMapper.map(bucketList, BucketListDTO.class);
            dto.setBucketListExps(mapBucketListExps(bucketList.getBucketListExps()));
            return dto;
        }).collect(Collectors.toList());

        return bucketListDTOs;
    }

    @Override
    public BucketListDTO getUserBucketList() {
        String loggedInEmail = authUtil.loggedInEmail();
        BucketList bucketList = bucketListRepository.findBucketListByEmail(loggedInEmail);

        if (bucketList == null) {
            throw new ResourceNotFoundException("BucketList", "email", loggedInEmail);
        }

        BucketListDTO bucketListDTO = modelMapper.map(bucketList, BucketListDTO.class);
        bucketListDTO.setBucketListExps(mapBucketListExps(bucketList.getBucketListExps()));
        return bucketListDTO;
    }

    @Transactional
    @Override
    public BucketListDTO updateBucketListExpStatus(Long bucketListId, Long bucketListExpId, boolean completed) {
        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "bucketListId", bucketListId));

        authUtil.checkOwnerOrAdmin(authUtil.loggedInUser(), bucketList.getUser(), "bucket list", "update experience status");

        BucketListExp bucketListExp = bucketListExpRepository.findById(bucketListExpId)
                .orElseThrow(() -> new ResourceNotFoundException("BucketListExp", "bucketListExpId", bucketListExpId));

        if (!bucketListExp.getBucketList().getBucketListId().equals(bucketListId)) {
            throw new APIException("This experience does not belong to the specified bucket list.");
        }

        bucketListExp.setCompleted(completed);
        bucketListExpRepository.save(bucketListExp);

        BucketListDTO bucketListDTO = modelMapper.map(bucketList, BucketListDTO.class);
        bucketListDTO.setBucketListExps(mapBucketListExps(bucketList.getBucketListExps()));
        return bucketListDTO;
    }


    @Override
    @Transactional
    public String deleteExpFromBucketList(Long bucketListId, Long bucketListExpId) {
        User currentUser = authUtil.loggedInUser();

        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "bucketListId", bucketListId));

        // Permission check
        authUtil.checkOwnerOrAdmin(currentUser, bucketList.getUser(), "bucket list", "delete experience from");

        BucketListExp bucketListExp = bucketListExpRepository.findById(bucketListExpId)
                .orElseThrow(() -> new ResourceNotFoundException("BucketListExp", "bucketListExpId", bucketListExpId));

        if (!bucketListExp.getBucketList().getBucketListId().equals(bucketListId)) {
            throw new APIException("This experience does not belong to the specified bucket list.");
        }

        // Use custom delete query
        bucketListExpRepository.deleteByBucketListExpIdAndBucketListId(bucketListExpId, bucketListId);

        return "Experience " + bucketListExp.getExperience().getExperienceName() + " removed from the bucket list.";
    }

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

    private List<BucketListExpDTO> mapBucketListExps(List<BucketListExp> exps) {
        return exps.stream().map(exp -> {
            ExperienceDTO experienceDTO = modelMapper.map(exp.getExperience(), ExperienceDTO.class);
            BucketListExpDTO dto = new BucketListExpDTO();
            dto.setBucketListExperienceId(exp.getBucketListExpId());
            dto.setBucketList(null); // prevent recursion
            dto.setExperience(experienceDTO);
            dto.setDateSaved(exp.getDateSaved());
            dto.setCompleted(exp.isCompleted());
            return dto;
        }).collect(Collectors.toList());
    }
}
