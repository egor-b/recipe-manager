package com.foodcrunch.foodster.recipemanager.model;

import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import lombok.Data;

import java.util.List;

@Data
public class ResponseRecipe {

    private List<RecipeEntity> recipeList;
    private Integer totalPages;
}
