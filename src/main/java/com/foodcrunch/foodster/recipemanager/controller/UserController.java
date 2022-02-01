package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.service.UserService;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping(path = "/create")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void createUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @PutMapping(path = "/updateEmail")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void updateUserEmail(@RequestBody User user) {
        userService.updateUserEmail(user);
    }

    @PutMapping(path = "/updateName")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void updateUserName(@RequestBody User user) {
        userService.updateUserName(user);
    }

    @GetMapping(path = "/find/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})

    public Flux<UserEntity> findUserByUid(@PathVariable(value = "uid") String uid) {
        return userService.loadUserByUid(uid).doOnNext(success ->
                        log.info("User was found"))
                .doOnError(error ->
                        log.debug(error.getStackTrace().toString()));
    }

//    @PostMapping(path = "/find")
//    @ResponseStatus(HttpStatus.OK)
//    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
//            @ApiResponse(code = 404, message = "Recipes not found"),
//            @ApiResponse(code = 400, message = "Missing or invalid request body"),
//            @ApiResponse(code = 500, message = "Internal Server error")})
//
//    public void findUser(@RequestBody User user) {
//
//    }
}
