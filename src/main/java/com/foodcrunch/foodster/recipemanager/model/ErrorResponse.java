package com.foodcrunch.foodster.recipemanager.model;

import lombok.Data;

@Data
public class ErrorResponse {

    private String message;
    private String details;

    public ErrorResponse(String message, String details) {
        super();
        this.message = message;
        this.details = details;
    }

}
