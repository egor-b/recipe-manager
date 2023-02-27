package com.foodcrunch.foodster.recipemanager.model.recipeModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

public class Purchase {

    private long id;

    private String recipeName;

    private int serve;

    private Double size;

    private boolean isAvailable;

    private long recipeId;

    private long foodId;

    private String userId;

}
