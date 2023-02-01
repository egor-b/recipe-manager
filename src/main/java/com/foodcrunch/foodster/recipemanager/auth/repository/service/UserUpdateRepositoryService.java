package com.foodcrunch.foodster.recipemanager.auth.repository.service;

import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.repository.UserUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

@Service
@RequiredArgsConstructor
public class UserUpdateRepositoryService implements UserUpdateRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void updateUser(User user) {
        CriteriaBuilder criteriaBuilder =  em.getCriteriaBuilder();
        CriteriaUpdate<UserEntity> userUpdate = criteriaBuilder.createCriteriaUpdate(UserEntity.class);

        Root root = userUpdate.from(UserEntity.class);

        if (!ObjectUtils.isEmpty(user.getName())) {
            userUpdate.set("name", user.getName());
        }
        if (!ObjectUtils.isEmpty(user.getLName())) {
            userUpdate.set("lName", user.getLName());
        }
        if (!ObjectUtils.isEmpty(user.getUsername())) {
            userUpdate.set("username", user.getUsername());
        }
        if (!ObjectUtils.isEmpty(user.getCountry())) {
            userUpdate.set("country", user.getCountry());
        }
        if (!ObjectUtils.isEmpty(user.getPhone())) {
            userUpdate.set("phone", user.getPhone());
        }
        if (!ObjectUtils.isEmpty(user.getAccountType())) {
            userUpdate.set("accountType", user.getAccountType());
        }
//        if (!ObjectUtils.isEmpty(user.getName())) {
//            userUpdate.set("IS_DISABLE", user.ge());
//        }
        userUpdate.set("pic", user.getPic());
        userUpdate.where(criteriaBuilder.equal(root.get("uid"), user.getUid()));
        em.createQuery(userUpdate).executeUpdate();
    }

}
