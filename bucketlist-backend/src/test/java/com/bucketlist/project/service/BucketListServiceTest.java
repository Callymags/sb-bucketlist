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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketListServiceTest {

    @Mock private BucketListRepository bucketListRepository;
    @Mock private ExperienceRepository experienceRepository;
    @Mock private BucketListExpRepository bucketListExpRepository;
    @Mock private AuthUtil authUtil;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private BucketListServiceImpl bucketListService;

    private User mockUser;
    private Experience experience;
    private BucketList bucketList;
    private BucketListExp bucketListExp;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("john");

        experience = new Experience();
        experience.setExperienceId(1L);
        experience.setExperienceName("Hike");
        experience.setCreatedBy(mockUser);
        experience.setLastModifiedBy(mockUser);
        experience.setCategory(new Category(1L, "Nature"));

        bucketList = new BucketList();
        bucketList.setBucketListId(1L);
        bucketList.setUser(mockUser);
        bucketList.setBucketListExps(new ArrayList<>());

        bucketListExp = new BucketListExp(1L, bucketList, experience, LocalDate.now(), false);
    }

    @Test
    void testAddExpToBucketList_Success() {
        when(authUtil.loggedInEmail()).thenReturn("john@example.com");
        when(bucketListRepository.findBucketListByEmail("john@example.com")).thenReturn(null);
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        when(bucketListRepository.save(any(BucketList.class))).thenReturn(bucketList);
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));
        when(bucketListExpRepository.findByBucketListExpIdAndBucketListId(anyLong(), anyLong())).thenReturn(null);
        when(modelMapper.map(bucketList, BucketListDTO.class)).thenReturn(new BucketListDTO());
        when(modelMapper.map(experience, ExperienceDTO.class)).thenReturn(new ExperienceDTO()); // ✅ FIX

        BucketListDTO result = bucketListService.addExpToBucketList(1L);

        assertNotNull(result);
        verify(bucketListExpRepository).save(any());
    }

    @Test
    void testAddExpToBucketList_AlreadyExists_ThrowsAPIException() {
        when(authUtil.loggedInEmail()).thenReturn("john@example.com");
        when(bucketListRepository.findBucketListByEmail("john@example.com")).thenReturn(bucketList);
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));
        when(bucketListExpRepository.findByBucketListExpIdAndBucketListId(bucketList.getBucketListId(), 1L)).thenReturn(bucketListExp);

        assertThrows(APIException.class, () -> bucketListService.addExpToBucketList(1L));
    }

    @Test
    void testGetBucketLists_Success() {
        doNothing().when(authUtil).checkAdmin("bucket lists", "view all");
        when(bucketListRepository.findAll()).thenReturn(List.of(bucketList));
        when(modelMapper.map(bucketList, BucketListDTO.class)).thenReturn(new BucketListDTO());

        List<BucketListDTO> results = bucketListService.getBucketLists();
        assertEquals(1, results.size());
    }

    @Test
    void testGetBucketLists_Empty_ThrowsAPIException() {
        doNothing().when(authUtil).checkAdmin("bucket lists", "view all");
        when(bucketListRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(APIException.class, () -> bucketListService.getBucketLists());
    }

    @Test
    void testGetUserBucketList_Success() {
        when(authUtil.loggedInEmail()).thenReturn("john@example.com");
        when(bucketListRepository.findBucketListByEmail("john@example.com")).thenReturn(bucketList);
        when(modelMapper.map(bucketList, BucketListDTO.class)).thenReturn(new BucketListDTO());

        BucketListDTO result = bucketListService.getUserBucketList();
        assertNotNull(result);
    }

    @Test
    void testGetUserBucketList_NotFound_ThrowsException() {
        when(authUtil.loggedInEmail()).thenReturn("john@example.com");
        when(bucketListRepository.findBucketListByEmail("john@example.com")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bucketListService.getUserBucketList());
    }

    @Test
    void testUpdateBucketListExpStatus_Success() {
        when(bucketListRepository.findById(1L)).thenReturn(Optional.of(bucketList));
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        doNothing().when(authUtil).checkOwnerOrAdmin(eq(mockUser), eq(mockUser), any(), any());
        when(bucketListExpRepository.findById(1L)).thenReturn(Optional.of(bucketListExp));
        when(modelMapper.map(bucketList, BucketListDTO.class)).thenReturn(new BucketListDTO());

        BucketListDTO result = bucketListService.updateBucketListExpStatus(1L, 1L, true);

        assertNotNull(result);
        verify(bucketListExpRepository).save(any());
    }

    @Test
    void testUpdateBucketListExpStatus_Mismatch_ThrowsAPIException() {
        bucketListExp.setBucketList(new BucketList(2L, mockUser, List.of()));

        when(bucketListRepository.findById(1L)).thenReturn(Optional.of(bucketList));
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        doNothing().when(authUtil).checkOwnerOrAdmin(eq(mockUser), eq(mockUser), any(), any());
        when(bucketListExpRepository.findById(1L)).thenReturn(Optional.of(bucketListExp));

        assertThrows(APIException.class, () -> bucketListService.updateBucketListExpStatus(1L, 1L, true));
    }

    @Test
    void testDeleteExpFromBucketList_Success() {
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        when(bucketListRepository.findById(1L)).thenReturn(Optional.of(bucketList));
        doNothing().when(authUtil).checkOwnerOrAdmin(mockUser, mockUser, "bucket list", "delete experience from");
        when(bucketListExpRepository.findById(1L)).thenReturn(Optional.of(bucketListExp));
        bucketListExp.setExperience(experience); // Needed for return string

        String result = bucketListService.deleteExpFromBucketList(1L, 1L);

        assertTrue(result.contains("removed from the bucket list"));
        verify(bucketListExpRepository).deleteByBucketListExpIdAndBucketListId(1L, 1L);
    }

    @Test
    void testDeleteExpFromBucketList_Mismatch_ThrowsAPIException() {
        bucketListExp.setBucketList(new BucketList(2L, mockUser, List.of()));

        when(authUtil.loggedInUser()).thenReturn(mockUser);
        when(bucketListRepository.findById(1L)).thenReturn(Optional.of(bucketList));
        doNothing().when(authUtil).checkOwnerOrAdmin(mockUser, mockUser, "bucket list", "delete experience from");
        when(bucketListExpRepository.findById(1L)).thenReturn(Optional.of(bucketListExp));

        assertThrows(APIException.class, () -> bucketListService.deleteExpFromBucketList(1L, 1L));
    }
}
