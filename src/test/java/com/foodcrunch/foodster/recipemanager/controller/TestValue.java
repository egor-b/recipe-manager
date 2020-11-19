package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.model.CookFoodEntity;
import com.foodcrunch.foodster.recipemanager.model.CookStepEntity;
import com.foodcrunch.foodster.recipemanager.model.FoodstuffEntity;
import com.foodcrunch.foodster.recipemanager.model.ImageEntity;
import com.foodcrunch.foodster.recipemanager.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestValue {

    public static long getValidId = 1;

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
            recipe.setUserId(i);
            recipe.setLang("EN");
            recipe.setVisible(true);
            recipes.add(recipe);
        }

        return recipes;
    }

    public static Recipe getValidRecipe() {
        return getListValidRecipes(1).get(0);
    }

    public static Map<String, String> getRequestBody() {
        Map<String, String> map = new HashMap<>();
        map.put("ingredient", "onion");
        map.put("maxServe", "4");

        return map;
    }

    public static Recipe getRecipe(Integer numOfIng, Integer numOfSteps) {
        Set<CookFoodEntity> cookFoodEntities = new HashSet<>();
        Set<CookStepEntity> cookStepEntities = new HashSet<>();
        Set<ImageEntity> imageEntities = new HashSet<>();

        Recipe recipe = new Recipe();

        recipe.setName("Meatball");
        recipe.setTime(20);
        recipe.setServe(1);
        recipe.setLevel(3.5);
        recipe.setType("type");
        recipe.setAbout("About");
        recipe.setUserId(1);
        recipe.setLang("EN");
        recipe.setVisible(true);

        for(int i = 0; i < numOfIng; i++) {
            FoodstuffEntity foodstuffEntity = new FoodstuffEntity();
            CookFoodEntity cookFoodEntitie = new CookFoodEntity();
            foodstuffEntity.setName("bla" + i);
            cookFoodEntitie.setMeasure("mgr");
            cookFoodEntitie.setSize(2+i);
            cookFoodEntitie.setImage("https://" + i + ".com");
            cookFoodEntitie.setFoodstuffEntity(foodstuffEntity);
            cookFoodEntities.add(cookFoodEntitie);
        }

        recipe.setCookFoodEntity(cookFoodEntities);

        for(int i = 0; i < numOfSteps; i++) {
            CookStepEntity cookStepEntity = new CookStepEntity();
            cookStepEntity.setStepId(i);
            cookStepEntity.setStep("Step by step " + i);
            cookStepEntity.setImage("https://" + i + ".com");
            cookStepEntities.add(cookStepEntity);
        }

        recipe.setCookStepEntity(cookStepEntities);

        for (int i = 0; i < 3; i++) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImage("https://" + i + ".com");
            imageEntities.add(imageEntity);
        }

        recipe.setImageEntity(imageEntities);

        return recipe;
    }
}
