package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.model.PurchaseResponse;
import com.foodcrunch.foodster.recipemanager.model.entity.PurchaseEntity;
import com.foodcrunch.foodster.recipemanager.repository.PurchaseRepository;
import com.foodcrunch.foodster.recipemanager.service.PurchaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseRepository purchaseRepository;

    @PostMapping(path = "save")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Save purchase", notes = "Save purchase for user")
    @ApiResponses(value = {//@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void savePurchase(@RequestBody PurchaseEntity body) {
        purchaseService.savePurchase(body);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve purchase", notes = "Retrieve list of user's purchase")
    @ApiResponses(value = {//@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Flux<PurchaseResponse> getPurchaseByUserId(@PathVariable(value = "id") String userId) {
        return purchaseService.getPurchaseList(userId)
                .doOnError(error ->
                        log.error(error.getLocalizedMessage()));
    }

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Retrieve purchase", notes = "Retrieve list of user's purchase")
    @ApiResponses(value = {//@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public PurchaseEntity getRecipePurchase(@RequestParam(value = "recipeid") long recipeId,
                                        @RequestParam(value = "userid") String userid,
                                        @RequestParam(value = "foodid") long foodId) {
        return purchaseService.getRecipePurchase(foodId, recipeId, userid);
    }

    @DeleteMapping(path = "delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete purchase", notes = "Delete user's purchase")
    @ApiResponses(value = {//@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void deletePurchase(@PathVariable(value = "id") long id) {
        purchaseRepository.deleteById(id);
    }

    @PutMapping(path = "cart")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete purchase", notes = "Delete user's purchase")
    @ApiResponses(value = {//@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void updateCart(@RequestParam(value = "isadd") boolean isadd,
                               @RequestParam(value = "id") long id,
                           @RequestParam(value = "user") String user) {
        purchaseService.updateCar(isadd, id, user);
    }

    @DeleteMapping(path = "delete/cart")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete purchase", notes = "Delete user's purchase")
    @ApiResponses(value = {//@ApiResponse(code = 200, message = "", response = RecipeEntity.class, responseContainer = "Recipe"),
            @ApiResponse(code = 404, message = "Recipe not found", response = NotFoundException.class),
            @ApiResponse(code = 400, message = "Missing or invalid request body", response = BadRequestException.class),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public void deletePurchaseFromCart(@RequestParam(value = "recipeid") long recipeId,
                               @RequestParam(value = "userid") String userid) {
        purchaseRepository.deleteRecipeFromCart(recipeId, userid);
    }
}
