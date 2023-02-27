package com.foodcrunch.foodster.recipemanager.model.recipeModel;

import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import lombok.Data;

@Data
public class Food {

    private long id;

    private RecipeEntity recipe;

    private Foodstuff foodstuff;

    private int size;

    private String measure;

}
