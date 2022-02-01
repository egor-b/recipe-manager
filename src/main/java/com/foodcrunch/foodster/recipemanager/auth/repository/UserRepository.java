package com.foodcrunch.foodster.recipemanager.auth.repository;

import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUid(String uid);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE UserEntity ue SET ue.email = ?1 WHERE ue.uid = ?2")
    void updateUserEmail(String email, String uid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE users.user SET name = :name, lname = :lname WHERE uid = :uid", nativeQuery = true)
    void updateUserName(@Param("name") String name, @Param("lname") String lName, @Param("uid") String uid);

}
