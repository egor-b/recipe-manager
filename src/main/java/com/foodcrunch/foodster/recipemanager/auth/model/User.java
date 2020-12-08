package com.foodcrunch.foodster.recipemanager.auth.model;

import lombok.Data;

import java.util.Set;

@Data
public class User {

    private String uid;
    private String name;
    private String lName;
    private String username;
    private String password;
    private String email;
    private String country;
    private String phone;
    private String pic = "pic_template.jpeg";
    private boolean disable = false;
    private Set<RoleModel> role;

}
