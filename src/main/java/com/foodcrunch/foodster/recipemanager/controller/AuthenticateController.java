package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.service.UserService;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthenticateController {

    private final UserService userService;

    @PostMapping(path = "/validate")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Validate user", notes = "User validation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void validateUser(@RequestBody Map<String, String> body) {

    }

}
