package com.brenno.expensecontrol.mappers.categories;

import com.brenno.expensecontrol.dto.categories.CategoriesResponse;
import com.brenno.expensecontrol.entity.Categories;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriesMapper {

    List<CategoriesResponse>categoriesEntityToCategoriesResponse(List<Categories> categories);
}
