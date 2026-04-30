package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.categories.CategoriesResponse;
import com.brenno.expensecontrol.service.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;


    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriesResponse>> getCategories(){
        return ResponseEntity.ok(categoriesService.getCategories());
    }
}
