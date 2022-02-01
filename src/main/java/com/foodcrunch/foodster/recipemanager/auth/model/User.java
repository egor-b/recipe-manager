package com.foodcrunch.foodster.recipemanager.auth.model;

import com.foodcrunch.foodster.recipemanager.auth.model.enumaration.Role;
import lombok.Data;

import java.util.Set;

@Data
public class User {

    private String uid;
    private String name;
    private String lName;
    private String username;
    private String email;
    private String country;
    private String phone;
    private String issuer;
    private String pic;
    private Role role;
    private String accountType;

}
