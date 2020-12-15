package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.service.RecipeManagerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@RunWith(SpringJUnit4ClassRunner.class)
@WebFluxTest(RecipeManagerController.class)
public class RecipeEntityManagerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RecipeManagerService recipeManagerService;

    @Test
    @WithMockUser
    public void whenGetValidRecipeId_ThenReturnOk() {
        when(recipeManagerService.retrieveRecipeById(anyLong())).thenReturn(Flux.just(TestValue.getValidRecipe()));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
        verify(recipeManagerService, times(1)).retrieveRecipeById(TestValue.getValidId);
    }

    @Test
    @WithMockUser
    public void whenReceiveBadRequest_thenReturn400() {
        when(recipeManagerService.retrieveRecipeById(anyLong())).thenReturn(Flux.error(new BadRequestException("1")));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe/{id}", "id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(recipeManagerService, times(0)).retrieveRecipeById(TestValue.getValidId);
    }

    @Test
    @WithMockUser
    public void whenGetValidRecipeId_ThenReturnNotFoundException() {
        when(recipeManagerService.retrieveRecipeById(anyLong())).thenReturn(Flux.error(new NotFoundException("1")));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe/{id}", 9999999)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
        verify(recipeManagerService, times(0)).retrieveRecipeById(TestValue.getValidId);
    }

    @Test
    @WithMockUser
    public void whenValidSearchCriteria_thenReturnListOfRecipe() {
        when(recipeManagerService.findRecipesByCriteria(0,10,"date", Sort.Direction.DESC, TestValue.getRequestBody()))
                .thenReturn(Flux.fromIterable(TestValue.getListValidRecipes(3)));
        webTestClient.mutateWith(csrf())
                .post()
                .uri("/v1/recipe/search?page=0&page_size=10")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(TestValue.getRequestBody())
                .exchange()
                .expectStatus()
                .isOk();
        verify(recipeManagerService, times(1)).findRecipesByCriteria(0,10,"date", Sort.Direction.DESC, TestValue.getRequestBody());
    }

    @Test
    @WithMockUser
    public void whenInvalidCountOfRecipesPerPage_thenReturnBadRequest() {
        when(recipeManagerService.findRecipesByCriteria(0,9999,"date", Sort.Direction.DESC, TestValue.getRequestBody()))
                .thenReturn(Flux.error(new BadRequestException("board")));
        webTestClient.mutateWith(csrf())
                .post()
                .uri("/v1/recipe/search?order=DESC&page_size=9999")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(TestValue.getRequestBody())
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(recipeManagerService, times(1)).findRecipesByCriteria(0,9999,"date", Sort.Direction.DESC, TestValue.getRequestBody());
    }

    @Test
    @WithMockUser
    public void whenValidPageNumber_thenReturnListOfRecipe() {
        when(recipeManagerService.getAllRecipesInRowByPageNumber(0)).thenReturn(Flux.fromIterable(TestValue.getListValidRecipes(10)));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
        verify(recipeManagerService, times(1)).getAllRecipesInRowByPageNumber(0);
    }

    @Test
    @WithMockUser
    public void whenInvalidPageNumber_thenReturnBadRequestException() {
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe?page=h")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(recipeManagerService, times(0)).getAllRecipesInRowByPageNumber(0);
    }

    @Test
    @WithMockUser
    public void whenValidRecipe_thenSaveToDb() {
        RecipeEntity recipeEntity = TestValue.getRecipe(0, 5);
        webTestClient.mutateWith(csrf())
                .post()
                .uri("/v1/recipe/save")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(recipeEntity)
                .exchange()
                .expectStatus()
                .isCreated();
        verify(recipeManagerService, times(1)).saveRecipe(recipeEntity);
    }

    @Test
    @WithMockUser
    public void whenValidUserId_thenReturnListOfRecipe() {
        when(recipeManagerService.getRecipesByUserId(TestValue.getValidId, 0,20,"date", Sort.Direction.ASC))
                .thenReturn(Flux.fromIterable(TestValue.getListValidRecipes(3)));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe/user/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
        verify(recipeManagerService, times(1)).getRecipesByUserId(TestValue.getValidId, 0,20,"date", Sort.Direction.ASC);
    }

    @Test
    @WithMockUser
    public void whenInvalidValidUserAndInvalidCriteria_thenReturnBadRequest() {
        when(recipeManagerService.getRecipesByUserId(TestValue.getValidId, 0,9999,"date", Sort.Direction.DESC))
                .thenReturn(Flux.error(new BadRequestException("board")));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/recipe/user/{id}?order=DESC&page_size=9999", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(recipeManagerService, times(1)).getRecipesByUserId(TestValue.getValidId, 0,9999,"date", Sort.Direction.DESC);
    }

}
