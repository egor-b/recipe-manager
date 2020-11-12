package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface RecipeInterface {
    Page<Recipe> findByPagingCriteria(Pageable pageable, Map<String, String> param);
    void saveRecipe(Recipe recipe);
}
