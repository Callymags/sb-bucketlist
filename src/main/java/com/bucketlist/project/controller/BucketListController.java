package com.bucketlist.project.controller;

import com.bucketlist.project.model.BucketList;
import com.bucketlist.project.model.Experience;
import com.bucketlist.project.payload.BucketListDTO;
import com.bucketlist.project.payload.BucketListExpDTO;
import com.bucketlist.project.repositories.BucketListRepository;
import com.bucketlist.project.service.BucketListService;
import com.bucketlist.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BucketListController {

    @Autowired
    private BucketListService bucketListService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private BucketListRepository bucketListRepository;

    @PostMapping("/bucketLists/experiences/{experienceId}")
    public ResponseEntity<BucketListDTO> addExperienceToBucketList(@PathVariable Long experienceId) {
        BucketListDTO bucketListDTO = bucketListService.addExpToBucketList(experienceId);
        return new ResponseEntity<BucketListDTO>(bucketListDTO, HttpStatus.CREATED);
    }

    @GetMapping("/bucketLists")
    public ResponseEntity<List<BucketListDTO>> getBucketLists() {
        List<BucketListDTO> bucketListDTOs = bucketListService.getBucketLists();
        return new ResponseEntity<List<BucketListDTO>>(bucketListDTOs, HttpStatus.FOUND);
    }

//    @GetMapping("/bucketLists/users/bucketList")
//    public ResponseEntity<BucketListDTO> getUserBucketList(){
//        String emailId = authUtil.loggedInEmail();
//        BucketList bucketList = bucketListRepository.findBucketListByEmail(emailId);
//        Long bucketListId = bucketList.getBucketListId();
//        BucketListDTO bucketListDTO = bucketListService.getUserBucketList(emailId, bucketListId);
//        return new ResponseEntity<BucketListDTO>(bucketListDTO, HttpStatus.OK);
//    }

    @GetMapping("/bucketLists/users/bucketList")
    public ResponseEntity<BucketListDTO> getUserBucketList(){
        BucketListDTO bucketListDTO = bucketListService.getUserBucketList();
        return new ResponseEntity<BucketListDTO>(bucketListDTO, HttpStatus.OK);
    }


    @PutMapping("/bucketLists/{bucketListId}/bucketListExps/{bucketListExpId}/status")
    public ResponseEntity<BucketListDTO> updateBucketListExpStatus(@PathVariable Long bucketListId,
                                                                   @PathVariable Long bucketListExpId,
                                                                   @RequestBody BucketListExpDTO bucketListExpDTO) {
        BucketListDTO bucketListDTO = bucketListService.updateBucketListExpStatus(bucketListId, bucketListExpId, bucketListExpDTO.isCompleted());
        return new ResponseEntity<>(bucketListDTO, HttpStatus.OK);
    }


    @DeleteMapping("/bucketLists/{bucketListId}/bucketListExps/{bucketListExpId}")
    public ResponseEntity<String> deleteExperienceFromBucketList(@PathVariable Long bucketListId,
                                                                 @PathVariable Long bucketListExpId) {
        String status = bucketListService.deleteExpFromBucketList(bucketListId, bucketListExpId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }



}
