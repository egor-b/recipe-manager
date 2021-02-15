package com.foodcrunch.foodster.recipemanager.auth.model;

import com.foodcrunch.foodster.recipemanager.auth.model.enumaration.Role;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
public class RoleModel {

    @Enumerated(EnumType.STRING)
    private Role name;

}