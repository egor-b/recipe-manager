package com.foodcrunch.foodster.recipemanager.model.recipeModel;

import lombok.Data;

@Data
public class Step {

    private long id;
    private int stepNumber;
    private String step;
    private String pic;

}
