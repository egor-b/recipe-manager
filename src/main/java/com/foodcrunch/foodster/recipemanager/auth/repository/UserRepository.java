package com.foodcrunch.foodster.recipemanager.auth.repository;

import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
}
