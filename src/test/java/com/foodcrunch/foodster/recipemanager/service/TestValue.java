package com.foodcrunch.foodster.recipemanager.service;

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

    public static List<Recipe> getListValidRecipes(Integer count, Integer numOfIng, Integer numOfSteps) {

        List<Recipe> recipes = new ArrayList<>();

        for(int i = 0; i < count; i++) {
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

            for(int ri = 0; ri < numOfIng; ri++) {
                FoodstuffEntity foodstuffEntity = new FoodstuffEntity();
                CookFoodEntity cookFoodEntitie = new CookFoodEntity();
                foodstuffEntity.setName("bla" + ri);
                cookFoodEntitie.setMeasure("mgr");
                cookFoodEntitie.setSize(2+ri);
                cookFoodEntitie.setImage("https://" + ri + ".com");
                cookFoodEntitie.setFoodstuffEntity(foodstuffEntity);
                cookFoodEntities.add(cookFoodEntitie);
            }

            recipe.setCookFoodEntity(cookFoodEntities);

            for(int rs = 0; rs < numOfSteps; rs++) {
                CookStepEntity cookStepEntity = new CookStepEntity();
                cookStepEntity.setStepId(rs);
                cookStepEntity.setStep("Step by step " + rs);
                cookStepEntity.setImage("https://" + rs + ".com");
                cookStepEntities.add(cookStepEntity);
            }

            recipe.setCookStepEntity(cookStepEntities);

            for (int rp = 0; rp < 3; rp++) {
                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setImage("https://" + rp + ".com");
                imageEntities.add(imageEntity);
            }

            recipe.setImageEntity(imageEntities);

            recipes.add(recipe);
        }

        return recipes;
    }

    public static Recipe getValidRecipe() {
        return getListValidRecipes(1, 5, 5).get(0);
    }

    public static Map<String, String> getBodyOfCriteria() {
        Map<String, String> map = new HashMap<>();
        map.put("ingredient", "onion");
        map.put("maxServe", "4");

        return map;
    }

}
