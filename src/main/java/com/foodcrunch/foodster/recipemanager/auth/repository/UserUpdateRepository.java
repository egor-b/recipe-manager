package com.foodcrunch.foodster.recipemanager.auth.repository;

import com.foodcrunch.foodster.recipemanager.auth.model.User;

public interface UserUpdateRepository {

    void updateUser(User user);
}
