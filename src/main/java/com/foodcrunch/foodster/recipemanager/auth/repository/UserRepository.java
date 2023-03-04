package com.foodcrunch.foodster.recipemanager.auth.repository;

import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUid(String uid);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE UserEntity ue SET ue.violation = ?2 WHERE ue.uid = ?1")
    void updateUserByUID(String uid, Integer violation);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE UserEntity ue SET ue.email = ?1 WHERE ue.uid = ?2")
    void updateUserEmail(String email, String uid);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM USERS.USER WHERE uid IN (:uid) ", nativeQuery = true)
    void deleteUser(@Param(value = "uid") String uid);

}
