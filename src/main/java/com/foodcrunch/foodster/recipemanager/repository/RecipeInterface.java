package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface RecipeInterface {
    Page<Recipe> findByPagingCriteria(Pageable pageable, Map<String, String> param);
    void saveRecipe(Recipe recipe);
}
