package com.foodcrunch.foodster.recipemanager.model.recipeModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodcrunch.foodster.recipemanager.model.entity.FoodEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ImageEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.StepEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
public class Recipe {
    private long id;

    private String name;

    private int time;

    private int serve;

    private double level;

    private String type;

    private String about;

    private String timestamp ;

    private String userId;

    private boolean visible;

    private String lang;

    private Set<Food> food;

    private Set<Step> step;

    private Set<Image> image;
}
