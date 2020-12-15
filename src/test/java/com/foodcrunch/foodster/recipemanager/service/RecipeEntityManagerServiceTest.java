package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.repository.RecipeInterface;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RecipeEntityManagerServiceTest {

    @InjectMocks
    private RecipeManagerService recipeManagerService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeInterface recipeInterface;

    @Test
    public void whenGetNumberOfPage_thenReturnListOfRecipes() {
        PageRequest page = PageRequest.of(1, 20);
        final Page<RecipeEntity> recipesPage = new PageImpl<>(TestValue.getListValidRecipes(2, 5, 5));
        when(recipeRepository.findAll(page)).thenReturn(recipesPage);
        Flux<RecipeEntity> result = recipeManagerService.getAllRecipesInRowByPageNumber(1);
        StepVerifier.create(result)
                .expectNextMatches(r -> r.getName().equals("Meatball"))
                .expectNextMatches(r -> r.getName().equals("Meatball"))
                .verifyComplete();
        verify(recipeRepository, times(1)).findAll(page);
    }

    @Test
    public void whenReceiveValidRecipeId_thenGetRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(TestValue.getValidRecipe()));
        Flux<RecipeEntity> result = recipeManagerService.retrieveRecipeById(1L);
        StepVerifier.create(result)
                .expectNextMatches(r -> {
                    Assertions.assertThat(r.getId()).isEqualTo(0L);
                    return true;
                })
                .expectComplete()
                .verify();
        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void whenReceiveInvalidRecipe_thenThrowException() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());
        Flux<RecipeEntity> result = recipeManagerService.retrieveRecipeById(1L);
        StepVerifier.create(result).expectError(NotFoundException.class).verify();
    }

    @Test
    public void whenGetCriteria_thenReturnPageWithRecipes() {
        PageRequest page = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "date"));
        final Page<RecipeEntity> recipesPage = new PageImpl<>(TestValue.getListValidRecipes(2,0, 0));

        when(recipeInterface.findByPagingCriteria(page, TestValue.getBodyOfCriteria())).thenReturn(recipesPage);
        Flux<RecipeEntity> result = recipeManagerService.findRecipesByCriteria(0,20, "date", Sort.Direction.ASC, TestValue.getBodyOfCriteria());
        StepVerifier.create(result)
                .expectNextMatches(r -> r.getName().equals("Meatball"))
                .expectNextMatches(r -> r.getName().equals("Meatball"))
                .verifyComplete();
        verify(recipeInterface, times(1)).findByPagingCriteria(page, TestValue.getBodyOfCriteria());
    }

    @Test
    public void whenGetInvalidNumberOfRowsRecipes_thenReturnBadRequestException() {
        PageRequest page = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "date"));
        Flux<RecipeEntity> result = recipeManagerService.findRecipesByCriteria(0,9999, "date", Sort.Direction.ASC, TestValue.getBodyOfCriteria());
        StepVerifier.create(result).expectError(BadRequestException.class).verify();
        verify(recipeInterface, times(0)).findByPagingCriteria(page, TestValue.getBodyOfCriteria());
    }

}
