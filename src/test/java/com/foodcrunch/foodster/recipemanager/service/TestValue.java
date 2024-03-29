package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.model.entity.FoodEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.StepEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ProductEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ImageEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

public class TestValue {

    public static List<RecipeEntity> getListValidRecipes(Integer count, Integer numOfIng, Integer numOfSteps) {

        List<RecipeEntity> recipeEntities = new ArrayList<>();

        for(int i = 0; i < count; i++) {
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
            recipeEntity.setUserId("1");
            recipeEntity.setLang("EN");
            recipeEntity.setVisible("true");

            for(int ri = 0; ri < numOfIng; ri++) {
                ProductEntity productEntity = new ProductEntity();
                FoodEntity cookFoodEntitie = new FoodEntity();
                productEntity.setName("bla" + ri);
                productEntity.setImage("");
                cookFoodEntitie.setUnit("mgr");
//                cookFoodEntitie.setSize(2+ri);
//                cookFoodEntitie.setFoodstuffEntity(productEntity);
                cookFoodEntities.add(cookFoodEntitie);
            }

            recipeEntity.setFoodEntity(cookFoodEntities);

            for(int rs = 0; rs < numOfSteps; rs++) {
                StepEntity stepEntity = new StepEntity();
                stepEntity.setStepNumber(rs);
                stepEntity.setStep("Step by step " + rs);
                stepEntity.setImage("");
                cookStepEntities.add(stepEntity);
            }

            recipeEntity.setStepEntity(cookStepEntities);

            for (int rp = 0; rp < 3; rp++) {
                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setImage("");
                imageEntities.add(imageEntity);
            }

            recipeEntity.setImageEntity(imageEntities);

            recipeEntities.add(recipeEntity);
        }

        return recipeEntities;
    }

    public static List<ProductEntity> getFoodList() {
        List<ProductEntity> productEntityList = new ArrayList<>();

        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setId(1L);
        productEntity1.setName("Berry");

        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setId(2L);
        productEntity2.setName("Butter");

        productEntityList.add(productEntity1);
        productEntityList.add(productEntity2);

        return productEntityList;
    }

    public static RecipeEntity getValidRecipe() {
        return getListValidRecipes(1, 5, 5).get(0);
    }

    public static Map<String, String> getBodyOfCriteria() {
        Map<String, String> map = new HashMap<>();
        map.put("ingredient", "onion");
        map.put("maxServe", "4");

        return map;
    }

}
