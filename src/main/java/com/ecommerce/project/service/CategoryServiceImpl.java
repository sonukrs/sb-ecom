package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

//    List<Category> categories=new ArrayList<>();
//    Long nextId= (long) 1D;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Optional<Category> currCategory=categoryRepository.findById(categoryId);
        if(currCategory.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not Found");
        }
        categoryRepository.deleteById(categoryId);
        return "Category with Id: "+categoryId+" deleted";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> currCategory=categoryRepository.findById(categoryId);
        Category presentCategory=currCategory.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Not Found"));
        presentCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(presentCategory);
        return presentCategory;
    }
}
