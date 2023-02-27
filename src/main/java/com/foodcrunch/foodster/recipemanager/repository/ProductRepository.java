package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findByNameContainingIgnoreCase (Pageable pageable, String text);
    ProductEntity findByNameEqualsIgnoreCase (String text);

}
