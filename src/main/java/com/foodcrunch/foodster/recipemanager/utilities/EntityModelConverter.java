package com.foodcrunch.foodster.recipemanager.utilities;

import com.foodcrunch.foodster.recipemanager.model.entity.FoodEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ProductEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.model.recipeModel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityModelConverter {

    public RecipeEntity recipeModelToEntity(Recipe recipe) {
        RecipeEntity recipeEntity = new RecipeEntity();

        recipeEntity.setId(recipe.getId());
        recipeEntity.setName(recipe.getName());
        recipeEntity.setTime(recipe.getTime());
        recipeEntity.setServe(recipe.getServe());
        recipeEntity.setLevel(recipe.getLevel());
        recipeEntity.setType(recipe.getType());
        recipeEntity.setAbout(recipe.getAbout());
//        recipeEntity.setDate(recipe.getTimestamp());
        recipeEntity.setUserId(recipe.getUserId());
//        recipeEntity.setVisible(recipe.isVisible());
        recipeEntity.setLang(recipe.getLang());
//        recipeEntity.setFoodEntity(foodModelToEntity(recipe.getFood()));
//        recipeEntity.setStepEntity();;
//        recipeEntity.setImageEntity();

        return recipeEntity;
    }

    public Set<FoodEntity> foodModelToEntity(Set<Food> food) {
        Set<FoodEntity> foodEntities = new HashSet<>();
        food.forEach( f -> {
            FoodEntity fe = new FoodEntity();
            fe.setId(f.getId());
            fe.setUnit(f.getMeasure());
//            fe.setSize(f.getSize());
            ProductEntity fse = foodStuffModelToEntity(f.getFoodstuff());
            fe.setProductEntity(fse);
            foodEntities.add(fe);
        });
        return foodEntities;
    }
    public ProductEntity foodStuffModelToEntity(Foodstuff foodstuff) {
        ProductEntity foodStuffEntity = new ProductEntity();
        foodStuffEntity.setId(foodstuff.getId());
        foodStuffEntity.setImage(foodstuff.getPic());
        foodStuffEntity.setName(foodstuff.getName());
        return foodStuffEntity;
    }
}
