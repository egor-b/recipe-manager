package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {
    List<PurchaseEntity> findByUserId(String id);
    PurchaseEntity findByFoodAndUserIdAndRecipeId(long foodId, String userId, long recipeId);

    @Query(value = "UPDATE RECIPE.PURCHASE p set p.is_available = :isAdd where p.id = :id", nativeQuery = true)
    void updateCart(@Param(value = "isAdd") boolean isAdd, @Param(value = "id") long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RECIPE.PURCHASE p WHERE p.food_id = :fid AND p.recipe_id = :rid AND p.user_id = :uid", nativeQuery = true)
    void deleteFromCart(@Param(value = "fid") long foodId, @Param(value = "rid") long recipeId, @Param(value = "uid") String userId);
}
