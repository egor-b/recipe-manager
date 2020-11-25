package com.foodcrunch.foodster.recipemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RecipeServiceException {

    private static final long serialVersionUID = 8354279296660607949L;

    public NotFoundException(String exception) {
        super(exception);
    }

}
