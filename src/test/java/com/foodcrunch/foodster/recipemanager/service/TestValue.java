package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestValue {

    public static List<Recipe> getListValidRecipes(Integer count) {

        List<Recipe> recipes = new ArrayList<>();

        for(int i = 0; i < count; i++) {
            Recipe recipe = new Recipe();
            recipe.setId(i);
            recipe.setName("Meatball_" + i);
            recipe.setTime(65132+i);
            recipe.setServe(i);
            recipe.setLevel(3.5);
            recipe.setType("type_" + i);
            recipe.setAbout("About_" + i);
            recipe.setUser_id(i);
            recipe.setLang("EN");
            recipe.isVisible();
            recipes.add(recipe);
        }

        return recipes;
    }

    public static Recipe getValidRecipe() {
        return getListValidRecipes(1).get(0);
    }

    public static Map<String, String> getBodyOfCriteria() {
        Map<String, String> map = new HashMap<>();
        map.put("ingredient", "onion");
        map.put("maxServe", "4");

        return map;
    }

}
