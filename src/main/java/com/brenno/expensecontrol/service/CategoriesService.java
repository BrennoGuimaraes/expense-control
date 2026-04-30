package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.categories.CategoriesResponse;
import com.brenno.expensecontrol.entity.Categories;
import com.brenno.expensecontrol.mappers.categories.CategoriesMapper;
import com.brenno.expensecontrol.repository.CategoriesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {


    private final CategoriesRepository categoriesRepository;

    private final CategoriesMapper categoriesMapper;

    public CategoriesService(CategoriesRepository categoriesRepository, CategoriesMapper categoriesMapper) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesMapper = categoriesMapper;
    }

    public Categories getCategoriesByLabel(String label){

        return categoriesRepository.findByLabel(label).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
    public Categories getCategoriesById(Long idCategory){

        return categoriesRepository.findById(idCategory).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public List<CategoriesResponse> getCategories(){

        return categoriesMapper.categoriesEntityToCategoriesResponse(categoriesRepository.findAll());
    }



}
