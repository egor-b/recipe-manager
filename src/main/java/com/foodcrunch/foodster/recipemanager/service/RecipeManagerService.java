package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.constant.LogLevel;
import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.model.Recipe;
import com.foodcrunch.foodster.recipemanager.repository.RecipeInterface;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.text.MessageFormat;
import java.util.Map;

import static com.foodcrunch.foodster.recipemanager.constant.ExceptionsConstants.RECIPE_NOT_FOUND;
import static com.foodcrunch.foodster.recipemanager.constant.ExceptionsConstants.LOW_FOOD;
import static com.foodcrunch.foodster.recipemanager.constant.ExceptionsConstants.LOW_STEPS;
import static com.foodcrunch.foodster.recipemanager.constant.ExceptionsConstants.TOO_MANY_RECIPES;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeManagerService {

    private final RecipeRepository recipeRepository;

    private final RecipeInterface recipeInterface;

    public Flux<Recipe> getAllRecipesInRowByPageNumber(Integer pageNumber) {
        PageRequest page = PageRequest.of(pageNumber, 20);
        return Flux.fromIterable(recipeRepository.findAll(page).getContent());
    }

    public Flux<Recipe> retrieveRecipeById(Long id) {
        final Recipe recipe = recipeRepository.findById(id).orElseGet(() -> null);

        if (recipe == null) {
            String message = buildLogEvent(RECIPE_NOT_FOUND, LogLevel.ERROR, null, id);
            return Flux.error(new NotFoundException(message));
        }
        return Flux.just(recipe);
    }

    public Flux<Recipe> findRecipesByCriteria(int pageNumber, int pageSize, String sortKey, Sort.Direction sortOrder, Map<String, String> criterias) {
        if (pageSize>100) {
            String message = buildLogEvent(TOO_MANY_RECIPES, LogLevel.ERROR, null, pageSize);
            return Flux.error(new BadRequestException(message));
        }
        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder, sortKey));
        return Flux.fromIterable(recipeInterface.findByPagingCriteria(page, criterias));
    }

    public Flux<Recipe> getRecipesByUserId(Long userId, int pageNumber, int pageSize, String sortKey, Sort.Direction sortOrder/*, Map<String, String> criterias*/) {
        if (pageSize>100) {
            String message = buildLogEvent(TOO_MANY_RECIPES, LogLevel.ERROR, null, pageSize);
            return Flux.error(new BadRequestException(message));
        }
        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder, sortKey));
        return Flux.fromIterable(recipeRepository.findByUserIdEquals(page, userId));
    }

    public Flux<Object> saveRecipe(Recipe recipe) {
        if (recipe.getCookFoodEntity().size()<=2) {
            return Flux.error(new BadRequestException(buildLogEvent(LOW_FOOD, LogLevel.ERROR, null, recipe.getCookFoodEntity().size())));
        }
        if (recipe.getCookStepEntity().size()<=3) {
            return Flux.error(new BadRequestException(buildLogEvent(LOW_STEPS, LogLevel.ERROR, null, recipe.getCookStepEntity().size())));
        }
        recipeInterface.saveRecipe(recipe);
        return null;
    }

    private String buildLogEvent(String message, LogLevel logLevel, Exception e, Object... args) {
        String notification = MessageFormat.format(message, args);
        if (logLevel.equals(LogLevel.INFO)) {
            log.info("{}", notification);
        }
        if (logLevel.equals(LogLevel.ERROR)) {
            if (e != null) {
                log.error("{} \n {}", notification, e.getLocalizedMessage());
            } else {
                log.error("{}", notification);
            }
        }
        return notification;
    }
}
