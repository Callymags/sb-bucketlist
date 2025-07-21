package com.bucketlist.project.service;

import com.bucketlist.project.exceptions.APIException;
import com.bucketlist.project.exceptions.ResourceNotFoundException;
import com.bucketlist.project.model.*;
import com.bucketlist.project.payload.ExperienceDTO;
import com.bucketlist.project.payload.ExperienceResponse;
import com.bucketlist.project.repositories.*;
import com.bucketlist.project.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock private ExperienceRepository experienceRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private BucketListExpRepository bucketListExpRepository;
    @Mock private FileService fileService;
    @Mock private ModelMapper modelMapper;
    @Mock private AuthUtil authUtil;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    private User mockUser;
    private Category mockCategory;
    private Experience mockExperience;
    private ExperienceDTO mockDto;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testUser");

        mockCategory = new Category();
        mockCategory.setCategoryId(100L);
        mockCategory.setCategoryName("Adventure");

        mockExperience = new Experience();
        mockExperience.setExperienceId(200L);
        mockExperience.setExperienceName("Skydiving");
        mockExperience.setCategory(mockCategory);
        mockExperience.setCreatedBy(mockUser);
        mockExperience.setLastModifiedBy(mockUser);

        mockDto = new ExperienceDTO();
        mockDto.setExperienceName("Skydiving");
    }

    @Test
    void testAddExperience_Success() {
        mockCategory.setExperiences(Collections.emptyList());

        when(authUtil.loggedInUser()).thenReturn(mockUser);
        when(categoryRepository.findById(100L)).thenReturn(Optional.of(mockCategory));
        when(modelMapper.map(mockDto, Experience.class)).thenReturn(mockExperience);
        when(experienceRepository.save(any(Experience.class))).thenReturn(mockExperience);
        when(modelMapper.map(mockExperience, ExperienceDTO.class)).thenReturn(mockDto);

        ExperienceDTO result = experienceService.addExperience(100L, mockDto);

        assertNotNull(result);
        verify(experienceRepository).save(any());
    }

    @Test
    void testAddExperience_AlreadyExists_ThrowsException() {
        mockCategory.setExperiences(List.of(mockExperience));
        mockDto.setExperienceName("Skydiving");

        when(authUtil.loggedInUser()).thenReturn(mockUser);
        when(categoryRepository.findById(100L)).thenReturn(Optional.of(mockCategory));

        assertThrows(APIException.class, () -> experienceService.addExperience(100L, mockDto));
    }

    @Test
    void testGetExperienceById_Success() {
        when(experienceRepository.findById(200L)).thenReturn(Optional.of(mockExperience));

        ExperienceDTO result = experienceService.getExperienceById(200L);

        assertNotNull(result);
        assertEquals("Skydiving", result.getExperienceName());
    }

    @Test
    void testGetExperienceById_NotFound_ThrowsException() {
        when(experienceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> experienceService.getExperienceById(999L));
    }

    @Test
    void testGetAllExperiences_Success() {
        Page<Experience> page = new PageImpl<>(List.of(mockExperience));
        Pageable pageable = PageRequest.of(0, 10);

        when(experienceRepository.findAll(any(Pageable.class))).thenReturn(page);

        ExperienceResponse response = experienceService.getAllExperiences(0, 10, "experienceName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testGetAllExperiences_Empty_ThrowsException() {
        Page<Experience> page = new PageImpl<>(Collections.emptyList());

        when(experienceRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThrows(APIException.class, () -> experienceService.getAllExperiences(0, 10, "experienceName", "asc"));
    }

    @Test
    void testUpdateExperience_Success() {
        mockDto.setExperienceName("Updated Experience");

        when(experienceRepository.findById(200L)).thenReturn(Optional.of(mockExperience));
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        doNothing().when(authUtil).checkOwnerOrAdmin(any(), any(), any(), any());
        when(modelMapper.map(mockDto, Experience.class)).thenReturn(mockExperience);
        when(experienceRepository.save(any())).thenReturn(mockExperience);
        when(modelMapper.map(mockExperience, ExperienceDTO.class)).thenReturn(mockDto);

        ExperienceDTO result = experienceService.updateExperience(200L, mockDto);

        assertEquals("Updated Experience", result.getExperienceName());
    }

    @Test
    void testDeleteExperience_Success() {
        when(experienceRepository.findById(200L)).thenReturn(Optional.of(mockExperience));
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        doNothing().when(authUtil).checkOwnerOrAdmin(any(), any(), any(), any());
        when(modelMapper.map(any(), eq(ExperienceDTO.class))).thenReturn(mockDto);

        ExperienceDTO result = experienceService.deleteExperience(200L);

        verify(bucketListExpRepository).deleteAllByExperienceId(200L);
        verify(experienceRepository).delete(mockExperience);
        assertEquals("Skydiving", result.getExperienceName());
    }

    @Test
    void testSearchByCategory_Success() {
        Page<Experience> page = new PageImpl<>(List.of(mockExperience));

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(mockCategory));
        when(experienceRepository.findByCategory(eq(mockCategory), any(Pageable.class))).thenReturn(page);

        ExperienceResponse response = experienceService.searchByCategory(100L, 0, 10, "experienceName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testSearchByCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> experienceService.searchByCategory(100L, 0, 10, "experienceName", "asc"));
    }

    @Test
    void testSearchByKeyword_Success() {
        Page<Experience> page = new PageImpl<>(List.of(mockExperience));

        when(experienceRepository.findByExperienceNameLikeIgnoreCase(anyString(), any(Pageable.class))).thenReturn(page);

        ExperienceResponse response = experienceService.searchExperienceByKeyword("Sky", 0, 10, "experienceName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testSearchByKeyword_NoResults_ThrowsException() {
        Page<Experience> emptyPage = new PageImpl<>(Collections.emptyList());

        when(experienceRepository.findByExperienceNameLikeIgnoreCase(anyString(), any(Pageable.class))).thenReturn(emptyPage);

        assertThrows(APIException.class, () -> experienceService.searchExperienceByKeyword("Nonexistent", 0, 10, "experienceName", "asc"));
    }

    @Test
    void testGetExperiencesCreatedByUser_Success() {
        when(authUtil.loggedInUser()).thenReturn(mockUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        doNothing().when(authUtil).checkOwnerOrAdmin(any(), any(), any(), any());

        Page<Experience> page = new PageImpl<>(List.of(mockExperience));
        when(experienceRepository.findByCreatedBy(eq(mockUser), any(Pageable.class))).thenReturn(page);

        ExperienceResponse response = experienceService.getExperiencesCreatedByUser(1L, 0, 10, "experienceName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }
}
