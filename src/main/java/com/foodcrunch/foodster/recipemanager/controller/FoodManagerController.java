package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.model.ErrorResponse;
import com.foodcrunch.foodster.recipemanager.model.FoodstuffEntity;
import com.foodcrunch.foodster.recipemanager.model.Recipe;
import com.foodcrunch.foodster.recipemanager.service.RecipeFoodManagerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/food")
public class FoodManagerController {

    private final RecipeFoodManagerService recipeFoodManagerService;

    @GetMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve food", notes = "search food by name")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = FoodstuffEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Flux<FoodstuffEntity> getFoodByName(@RequestParam(value = "name") String name,
                                               @RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "page_size", defaultValue = "15") int pageSize,
                                               @RequestParam(value = "sort_field", defaultValue = "name") String sortKey,
                                               @RequestParam(value = "order", defaultValue = "ASC") Sort.Direction sortOrder) {
        return recipeFoodManagerService.getFoodByName(name,pageNumber,pageSize,sortOrder,sortKey);
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentTypeMismatchException.class})
    public final ResponseEntity<Recipe> badRequestHandleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("Bad request", e.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
