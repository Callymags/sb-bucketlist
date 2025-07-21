package com.bucketlist.project.service;

import com.bucketlist.project.exceptions.APIException;
import com.bucketlist.project.exceptions.ResourceNotFoundException;
import com.bucketlist.project.model.Category;
import com.bucketlist.project.payload.CategoryDTO;
import com.bucketlist.project.payload.CategoryResponse;
import com.bucketlist.project.repositories.CategoryRepository;
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
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private AuthUtil authUtil;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setup() {
        category = new Category(1L, "Adventure");
        categoryDTO = new CategoryDTO(1L, "Adventure");
    }

    @Test
    void testGetAllCategories_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryName").ascending());
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryResponse response = categoryService.getAllCategories(0, 10, "categoryName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testGetAllCategories_Empty_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryName").ascending());
        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList());

        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        assertThrows(APIException.class, () -> categoryService.getAllCategories(0, 10, "categoryName", "asc"));
    }

    @Test
    void testCreateCategory_Success() {
        doNothing().when(authUtil).checkAdmin("category", "create");
        when(categoryRepository.findByCategoryName("Adventure")).thenReturn(null);
        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertNotNull(result);
        assertEquals("Adventure", result.getCategoryName());
    }

    @Test
    void testCreateCategory_Duplicate_ThrowsException() {
        doNothing().when(authUtil).checkAdmin("category", "create");
        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(categoryRepository.findByCategoryName("Adventure")).thenReturn(category);

        assertThrows(APIException.class, () -> categoryService.createCategory(categoryDTO));
    }

    @Test
    void testDeleteCategory_Success() {
        doNothing().when(authUtil).checkAdmin("category", "delete");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO deleted = categoryService.deleteCategory(1L);

        assertNotNull(deleted);
        verify(categoryRepository).delete(category);
    }

    @Test
    void testDeleteCategory_NotFound_ThrowsException() {
        doNothing().when(authUtil).checkAdmin("category", "delete");
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void testUpdateCategory_Success() {
        Category updated = new Category(1L, "Updated");
        CategoryDTO updatedDTO = new CategoryDTO(1L, "Updated");

        doNothing().when(authUtil).checkAdmin("category", "update");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(updated);
        when(categoryRepository.save(updated)).thenReturn(updated);
        when(modelMapper.map(updated, CategoryDTO.class)).thenReturn(updatedDTO);

        CategoryDTO result = categoryService.updateCategory(categoryDTO, 1L);

        assertEquals("Updated", result.getCategoryName());
    }

    @Test
    void testUpdateCategory_NotFound_ThrowsException() {
        doNothing().when(authUtil).checkAdmin("category", "update");
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryDTO, 1L));
    }
}
