package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.model.Recipe;
import com.foodcrunch.foodster.recipemanager.repository.RecipeInterface;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RecipeManagerServiceTest {

    @InjectMocks
    private RecipeManagerService recipeManagerService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeInterface recipeInterface;

    @Test
    public void test() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(TestValue.getValidRecipe()));
        Flux<Recipe> result = recipeManagerService.retrieveRecipeById(1L);
        StepVerifier.create(result).verifyComplete();
        verify(recipeRepository, times(1)).findById(1L);
    }
}
