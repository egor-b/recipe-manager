package com.foodcrunch.foodster.recipemanager.auth.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class User {

    private String uid;
    private String name;
    private String lName;
    private String username;
    private String email;
    private String country;
    private String phone;
    private String pic = "pic_template.jpeg";
    private boolean disable = false;
    private Set<RoleModel> role;

}
