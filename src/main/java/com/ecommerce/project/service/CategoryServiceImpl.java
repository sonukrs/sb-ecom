package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize) {
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize);
        Page<Category> categoryPage= categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty()) {
            throw new APIException("No categories created till now");
        }
        List<CategoryDTO> categoryDTOS=categories.stream().map(
                category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setIsLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category=modelMapper.map(categoryDTO, Category.class);
        Category fetchedCategory=categoryRepository.findByCategoryName(category.getCategoryName());
        if(fetchedCategory!=null){
            throw new APIException("Category with: "+categoryDTO.getCategoryName()+" already exists");
        }
        Category savedCategory=categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Optional<Category> currCategory=categoryRepository.findById(categoryId);
        if(currCategory.isEmpty()) {
            throw new ResourceNotFoundException("category","categoryId",categoryId);
        }
        categoryRepository.deleteById(categoryId);
        return modelMapper.map(currCategory.get(), CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Optional<Category> currCategory=categoryRepository.findById(categoryId);
        Category presentCategory=currCategory.orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
        Category passedCategory=modelMapper.map(categoryDTO, Category.class);
        presentCategory.setCategoryName(passedCategory.getCategoryName());
        categoryRepository.save(presentCategory);
        return modelMapper.map(presentCategory, CategoryDTO.class);
    }
}
