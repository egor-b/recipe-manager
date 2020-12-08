package com.foodcrunch.foodster.recipemanager.auth.repository;

import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    @Query("UPDATE UserEntity ue SET ue.email = ?1 WHERE ue.uid = ?2")
    void updateUserEmail(String email, String uid);

}
