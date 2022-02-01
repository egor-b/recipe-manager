package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.model.entity.FoodstuffEntity;
import com.foodcrunch.foodster.recipemanager.repository.FoodstuffRepository;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RecipeEntityFoodManagerServiceTest {

    @InjectMocks
    private RecipeFoodManagerService recipeFoodManagerService;

    @Mock
    private FoodstuffRepository foodstuffRepository;

    @Mock
    private LogService logService;

    @Test
    public void whenGetValidString_thenReturnFoodList() {
        PageRequest page = PageRequest.of(0, 15, Sort.by(Sort.Direction.ASC, "name"));
        final Page<FoodstuffEntity> recipePage = new PageImpl<>(TestValue.getFoodList());
        when(foodstuffRepository.findByNameContainingIgnoreCase(page,"String")).thenReturn(recipePage);
        Flux<FoodstuffEntity> result = recipeFoodManagerService.getFoodByName("String",0,15, Sort.Direction.ASC, "name");
        StepVerifier.create(result)
                .expectNextMatches(r -> r.getName().equals("Berry"))
                .expectNextMatches(r -> r.getName().equals("Butter"))
                .verifyComplete();
        verify(foodstuffRepository, times(1)).findByNameContainingIgnoreCase(page, "String");
    }

    @Test
    public void whenGetValidStringAndInvalidPageSize_thenReturnBadRequestException() {
        PageRequest page = PageRequest.of(0, 999, Sort.by(Sort.Direction.ASC, "name"));
        Flux<FoodstuffEntity> result = recipeFoodManagerService.getFoodByName("String",0,999, Sort.Direction.ASC, "name");
        StepVerifier.create(result).expectError(BadRequestException.class).verify();
        verify(foodstuffRepository, times(0)).findByNameContainingIgnoreCase(page, "String");
    }
}
