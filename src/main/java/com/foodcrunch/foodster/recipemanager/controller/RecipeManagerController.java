package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.ErrorResponse;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.exception.RecipiesLockedException;
import com.foodcrunch.foodster.recipemanager.model.ResponseRecipe;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.ReportEntity;
import com.foodcrunch.foodster.recipemanager.service.RecipeManagerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/recipe")
public class RecipeManagerController {

    private final RecipeManagerService recipeManagerService;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Retrieve recipes in a row")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Flux<ResponseRecipe> getRecipes(@RequestParam(value = "page", defaultValue = "0") int page) {
        return recipeManagerService.getAllRecipesInRowByPageNumber(page)
                .doOnError(error ->
                        log.debug(error.getStackTrace().toString()));

    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipe", notes = "Search recipe by ID")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Flux<RecipeEntity> getRecipeById(@PathVariable(value = "id") Long recipeId) {
        return recipeManagerService.retrieveRecipeById(recipeId)
                .doOnNext(success ->
                        log.info("Recipe '{}' was returned. Id is {}", success.getName(), success.getId()))
                .doOnError(error ->
                        log.debug(error.getStackTrace().toString()));

    }

    @PostMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by filter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipes not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})

    public Flux<ResponseRecipe> findAllByCriteria(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "sort_field", defaultValue = "date") String sortKey,
                                                @RequestParam(value = "page_size", defaultValue = "20") int pageSize,
                                                @RequestParam(value = "order", defaultValue = "DESC") Sort.Direction sortOrder,
                                                @RequestBody Map<String, String> body) {
        return recipeManagerService.findRecipesByCriteria(pageNumber,pageSize, sortKey, sortOrder, body)
                .doOnError(error ->
                        log.debug(error.getStackTrace().toString()));
    }

    @PostMapping(path = "/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve recipes", notes = "Recipes search by user ID")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found"),
            @ApiResponse(code = 400, message = "Missing or invalid request body"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Flux<ResponseRecipe> getRecipeByUserId(@PathVariable(value = "id") String userId,
                                                @RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "page_size", defaultValue = "20") int pageSize,
                                                @RequestParam(value = "sort_field", defaultValue = "date") String sortKey,
                                                @RequestParam(value = "order", defaultValue = "ASC") Sort.Direction sortOrder,
                                                @RequestBody Map<String, String> body) {
        return recipeManagerService.getRecipesByUserId(userId, pageNumber, pageSize, sortKey, sortOrder, body)
                .doOnError(error ->
                        log.debug(error.getStackTrace().toString()));
    }

    @PostMapping(path = "report")
    @ApiOperation(value = "Submit report", notes = "Submit a bad recipe report")
    public void reportRecipe(@Valid @RequestBody ReportEntity report) {

    }
    @Transactional
    @PostMapping(path = "save")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux saveRecipe(@Valid @RequestBody RecipeEntity recipeEntity) {
        return recipeManagerService.saveRecipe(recipeEntity);
    }

    @Transactional
    @PostMapping(path = "update")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux updateRecipe(@Valid @RequestBody RecipeEntity recipeEntity) {
        return recipeManagerService.updateRecipe(recipeEntity);
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentTypeMismatchException.class})
    public final ResponseEntity<RecipeEntity> badRequestHandleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("Bad request", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecipiesLockedException.class)
    public final ResponseEntity<RecipeEntity> recipesLockedException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("Bad request", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.LOCKED);
    }

    @ExceptionHandler({NullPointerException.class})
    public final ResponseEntity<RecipeEntity> processNulPointerThenReturnBadRequestException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("Bad request", "Was sent incorrect request.");
        return new ResponseEntity(errorResponse, HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<RecipeEntity> notFoundHandleException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse("Record not found", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
    }
}
