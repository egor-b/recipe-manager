package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
//    Page<Recipe> findByUserIdEquals(Pageable pageable, long id);
}
