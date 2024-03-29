package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.service.UserService;
import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.ErrorResponse;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.google.firebase.auth.FirebaseAuthException;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping(path = "/create")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Create User", notes = "Create new user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User succefulluly created", responseContainer = "User"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void createUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @PutMapping(path = "/updateEmail")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "updated", responseContainer = "User")})
    public void updateUserEmail(@RequestBody User user) {
        userService.updateUserEmail(user);
    }

    @GetMapping(path = "/find/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Find User", notes = "Find user bu UID")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = UserEntity.class, responseContainer = "User"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})

    public Flux<UserEntity> findUserByUid(@PathVariable(value = "uid") String uid) {
        return userService.loadUserByUid(uid).doOnNext(success ->
                        log.info("User {} was found", success.getName()))
                .doOnError(error ->
                        log.debug(error.getStackTrace().toString()));
    }

    @PutMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update", notes = "Update user information")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User is updated", responseContainer = "Recipe")})
    public void findUser(@RequestBody User user) {
        userService.updateUserPic(user);
    }

    @DeleteMapping(path = "/{uid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteUser(@PathVariable(value = "uid") String uid) {
        userService.deleteUser(uid);
    }

    @DeleteMapping(path = "/apple/{authCode}/{uid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteAppleUser(@PathVariable(value = "authCode") String authCode, @PathVariable(value = "uid") String uid) {
        userService.deleteAppleUser(authCode, uid);
    }

    @ExceptionHandler({UnirestException.class, IOException.class, FirebaseAuthException.class})
    public final ResponseEntity general(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("ERROR", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
