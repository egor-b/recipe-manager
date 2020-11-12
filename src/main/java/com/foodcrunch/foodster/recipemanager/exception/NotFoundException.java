package com.foodcrunch.foodster.recipemanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RecipeServiceException {

    private static final long serialVersionUID = 8354279296660607949L;

    @Getter
    private final String id;

    public NotFoundException(String id) {
        super("Recipe with id " + id + " isn't found");
        this.id = id;
    }

}
