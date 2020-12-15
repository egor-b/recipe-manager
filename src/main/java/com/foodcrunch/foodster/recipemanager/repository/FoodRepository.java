package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.entity.FoodstuffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<FoodstuffEntity, Long> {
    Page<FoodstuffEntity> findByNameContainingIgnoreCase (Pageable pageable, String text);
    FoodstuffEntity findByNameEqualsIgnoreCase (String text);
}
