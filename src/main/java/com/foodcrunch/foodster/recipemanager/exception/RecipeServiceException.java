package com.foodcrunch.foodster.recipemanager.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecipeServiceException extends Exception {

    private static final long serialVersionUID = 8354279296660607949L;

    public RecipeServiceException(String message) { super(message); }

}
