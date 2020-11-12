package com.foodcrunch.foodster.recipemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RecipeServiceException {

    private static final long serialVersionUID = 8354279296660607949L;

    public BadRequestException(String message) {
        super(message);
    }

}
