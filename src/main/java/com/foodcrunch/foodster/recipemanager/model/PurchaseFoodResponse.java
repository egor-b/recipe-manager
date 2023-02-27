package com.foodcrunch.foodster.recipemanager.model;

import lombok.Data;

@Data
public class PurchaseFoodResponse {

    private long id;
    private String name;
    private String amount;
    private String unit;
    private Boolean isAvailable;

}
