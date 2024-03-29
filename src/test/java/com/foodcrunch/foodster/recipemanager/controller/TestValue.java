package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.model.entity.FoodEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.StepEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ProductEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ImageEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

public class TestValue {

    public static String getValidId = "1";
    public static long getValidLongId = 1;

//    public static RecipeEntity getListValidRecipes(Integer count) {
//
//        List<RecipeEntity> recipeEntities = new ArrayList<>();
//
//        for(int i = 0; i < count; i++) {
//            RecipeEntity recipeEntity = new RecipeEntity();
//            recipeEntity.setId(i);
//            recipeEntity.setName("Meatball_" + i);
//            recipeEntity.setTime(65132+i);
//            recipeEntity.setServe(i);
//            recipeEntity.setLevel(3.5);
//            recipeEntity.setType("type_" + i);
//            recipeEntity.setAbout("About_" + i);
//            recipeEntity.setUserId("sds");
//            recipeEntity.setLang("EN");
//            recipeEntity.setVisible("true");
//            recipeEntities.add(recipeEntity);
//        }
//
//        return recipeEntities;
//    }

//    public static RecipeEntity getValidRecipe() {
//        return getListValidRecipes(1).get(0);
//    }

    public static Map<String, String> getRequestBody() {
        Map<String, String> map = new HashMap<>();
        map.put("ingredient", "onion");
        map.put("maxServe", "4");

        return map;
    }

    public static Flux<ProductEntity> getFoodList(Integer count) {
        List<ProductEntity> productEntityList = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setId(i+1);
            productEntity.setName("Berry"+i);
            productEntityList.add(productEntity);
        }

        return Flux.fromIterable(productEntityList);
    }

    public static RecipeEntity getRecipe(Integer numOfIng, Integer numOfSteps) {
        Set<FoodEntity> cookFoodEntities = new HashSet<>();
        Set<StepEntity> cookStepEntities = new HashSet<>();
        Set<ImageEntity> imageEntities = new HashSet<>();

        RecipeEntity recipeEntity = new RecipeEntity();

        recipeEntity.setName("Meatball");
        recipeEntity.setTime(20);
        recipeEntity.setServe(1);
        recipeEntity.setLevel(3.5);
        recipeEntity.setType("type");
        recipeEntity.setAbout("About");
        recipeEntity.setUserId("ropger");
        recipeEntity.setLang("EN");
        recipeEntity.setVisible("true");

        for(int i = 0; i < numOfIng; i++) {
            ProductEntity productEntity = new ProductEntity();
            FoodEntity cookFoodEntitie = new FoodEntity();
            productEntity.setName("bla" + i);
            productEntity.setImage("");
            cookFoodEntitie.setUnit("mgr");
//            cookFoodEntitie.setSize(2+i);
//            cookFoodEntitie.setFoodstuffEntity(productEntity);
            cookFoodEntities.add(cookFoodEntitie);
        }

        recipeEntity.setFoodEntity(cookFoodEntities);

        for(int i = 0; i < numOfSteps; i++) {
            StepEntity stepEntity = new StepEntity();
            stepEntity.setStepNumber(i);
            stepEntity.setStep("Step by step " + i);
            stepEntity.setImage("");
            cookStepEntities.add(stepEntity);
        }

        recipeEntity.setStepEntity(cookStepEntities);

        for (int i = 0; i < 3; i++) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImage("");
            imageEntities.add(imageEntity);
        }

        recipeEntity.setImageEntity(imageEntities);

        return recipeEntity;
    }
}
