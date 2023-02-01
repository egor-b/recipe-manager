package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.model.recipeModel.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface RecipeRepositoryInterface {

    Page<RecipeEntity> findByPagingCriteria(Pageable pageable, Map<String, String> param);

    void saveRecipe(RecipeEntity recipeEntity);

    void updateRecipe(RecipeEntity recipeEntity);

}
