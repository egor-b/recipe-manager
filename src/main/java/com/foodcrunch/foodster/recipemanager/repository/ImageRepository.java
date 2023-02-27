package com.foodcrunch.foodster.recipemanager.repository;

import com.foodcrunch.foodster.recipemanager.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE RECIPE.IMAGE set IMAGE_REFERENCE = :reference WHERE id = :id", nativeQuery = true)
    void updateImage(@Param(value = "reference") String reference, @Param(value = "id") long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RECIPE.IMAGE WHERE id IN (:id) ", nativeQuery = true)
    void deleteImagesByLisId(@Param(value = "id") List<Long> id);
}
