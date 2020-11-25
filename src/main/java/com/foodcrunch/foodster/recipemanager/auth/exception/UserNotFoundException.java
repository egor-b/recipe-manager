package com.foodcrunch.foodster.recipemanager.auth.exception;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 8354279296660607949L;

    public UserNotFoundException(String exception) {
        super(exception);
    }
}
