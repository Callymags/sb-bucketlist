package com.bucketlist.project.service;

import com.bucketlist.project.exceptions.APIException;
import com.bucketlist.project.exceptions.PermissionDeniedException;
import com.bucketlist.project.exceptions.ResourceNotFoundException;
import com.bucketlist.project.model.AppRole;
import com.bucketlist.project.model.Category;
import com.bucketlist.project.model.User;
import com.bucketlist.project.payload.CategoryDTO;
import com.bucketlist.project.payload.CategoryResponse;
import com.bucketlist.project.repositories.CategoryRepository;
import com.bucketlist.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()){
            throw new APIException("No categories created.");
        }
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        User user = authUtil.loggedInUser();
        if (user.getRole().getRoleName() != AppRole.ROLE_ADMIN) {
            throw new PermissionDeniedException("User", user.getUserId(), "category", "create");
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category dbCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (dbCategory != null){
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        User user = authUtil.loggedInUser();
        if (user.getRole().getRoleName() != AppRole.ROLE_ADMIN) {
            throw new PermissionDeniedException("User", user.getUserId(), "category", "delete");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        User user = authUtil.loggedInUser();
        if (user.getRole().getRoleName() != AppRole.ROLE_ADMIN) {
            throw new PermissionDeniedException("User", user.getUserId(), "category", "update");
        }

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
