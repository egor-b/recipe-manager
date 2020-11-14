package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.model.ErrorResponse;
import com.foodcrunch.foodster.recipemanager.model.Recipe;
import com.foodcrunch.foodster.recipemanager.service.RecipeManagerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/recipe")
public class RecipeManagerController {

    @Autowired
    private RecipeManagerService recipeManagerService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipe", notes = "Search recipe by ID")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = Recipe.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Flux<Recipe> getRecipeById(@PathVariable(value = "id") Long recipeId) {
        return recipeManagerService.retrieveRecipeById(recipeId)
                .doOnNext(success ->
                        log.info("Recipe '{}' was returned. Id is {}", success.getName(), success.getId()))
                .doOnError(error ->
                        log.debug(error.getStackTrace()));

    }

    //SEARCH RECIPE
    @PostMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = Recipe.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})

    public Flux<Recipe> findAllByCriteria(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                          @RequestParam(value = "sort_field", defaultValue = "date") String sortKey,
                                          @RequestParam(value = "number", defaultValue = "10") int number,
                                          @RequestParam(value = "order", defaultValue = "desc") String sort,
                                          @RequestBody Map<String, String> body) {
        return recipeManagerService.findRecipesByCriteria(pageNumber,number, sortKey, sort, body)
                .doOnNext(success ->
                        log.info("Recipe '" + success.getName() + "' was found. id = " + success.getId()))
                .doOnError(error ->
                        log.debug(error.getStackTrace()));
    }

    //SAVE RECIPE
    @Transactional
    @PostMapping(path = "save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void saveRecipe(@Valid @RequestBody Recipe recipe) {
        recipeManagerService.saveRecipe(recipe);
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentTypeMismatchException.class})
    public final ResponseEntity<Recipe> badRequestHandleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("Bad request", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Recipe> notFoundHandleException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse("Record not found", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
    }
}
