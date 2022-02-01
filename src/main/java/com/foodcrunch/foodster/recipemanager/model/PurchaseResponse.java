package com.foodcrunch.foodster.recipemanager.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseResponse {

    private String recipeName;
    private Integer serve;
    private long recipeId;
    private List<PurchaseFoodResponse> food = new ArrayList<>();

}
