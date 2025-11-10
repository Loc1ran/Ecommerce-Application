package com.loctran.store.services;

import com.loctran.store.entities.Category;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findById(Byte id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }
}
