package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.entity.StepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StepRepository extends JpaRepository<StepEntity, Long> {

    @Query(value = "SELECT ID FROM RECIPE.STEP WHERE recipe_id = :id", nativeQuery = true)
    List<Long> findAllByRecipeId(@Param(value = "id") long recipeId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE RECIPE.STEP SET IMAGE_REFERENCE = :reference, step = :step, step_number = :sNum WHERE id = :id", nativeQuery = true)
    void updateStep(@Param(value = "reference") String reference, @Param(value = "step") String step, @Param(value = "sNum") int sNum, @Param(value = "id") long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RECIPE.STEP WHERE id IN (:id) ", nativeQuery = true)
    void deleteFoodByListId(@Param(value = "id") List<Long> id);

}
