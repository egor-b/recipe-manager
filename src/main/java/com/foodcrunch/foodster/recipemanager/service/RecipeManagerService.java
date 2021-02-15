package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.service.UserService;
import com.foodcrunch.foodster.recipemanager.constant.LogLevel;
import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepositoryInterface;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
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
    private final RecipeRepositoryInterface recipeRepositoryInterface;
    private final LogService logService;
    private final ImageManagerService imageManagerService;
    private final UserService userService;


    public Flux<RecipeEntity> getAllRecipesInRowByPageNumber(Integer pageNumber) {
        PageRequest page = PageRequest.of(pageNumber, 20);
        return Flux.fromIterable(recipeRepository.findAll(page).getContent());
    }

    public Flux<RecipeEntity> retrieveRecipeById(Long id) {
        final RecipeEntity recipeEntity = recipeRepository.findById(id).orElseGet(() -> null);

        if (recipeEntity == null) {
            String message = logService.buildLogEvent(RECIPE_NOT_FOUND, LogLevel.ERROR, null, id);
            return Flux.error(new NotFoundException(message));
        }
        return Flux.just(recipeEntity);
    }

    public Flux<RecipeEntity> findRecipesByCriteria(int pageNumber, int pageSize, String sortKey, Sort.Direction sortOrder, Map<String, String> criterias) {
        if (pageSize>100) {
            String message = logService.buildLogEvent(TOO_MANY_RECIPES, LogLevel.ERROR, null, pageSize);
            return Flux.error(new BadRequestException(message));
        }
        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder, sortKey));
        return Flux.fromIterable(recipeRepositoryInterface.findByPagingCriteria(page, criterias));
    }

    public Flux<RecipeEntity> getRecipesByUserId(Long userId, int pageNumber, int pageSize, String sortKey, Sort.Direction sortOrder/*, Map<String, String> criterias*/) {
        if (pageSize>100) {
            String message = logService.buildLogEvent(TOO_MANY_RECIPES, LogLevel.ERROR, null, pageSize);
            return Flux.error(new BadRequestException(message));
        }
        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder, sortKey));
        return Flux.fromIterable(recipeRepository.findByUserIdEquals(page, userId));
    }

    public Flux<Object> saveRecipe(RecipeEntity recipeEntity) {
        if (recipeEntity.getFoodEntity().size()<=2) {
            return Flux.error(new BadRequestException(logService.buildLogEvent(LOW_FOOD, LogLevel.ERROR, null, recipeEntity.getFoodEntity().size())));
        }
        if (recipeEntity.getStepEntity().size()<=3) {
            return Flux.error(new BadRequestException(logService.buildLogEvent(LOW_STEPS, LogLevel.ERROR, null, recipeEntity.getStepEntity().size())));
        }
        recipeRepositoryInterface.saveRecipe(recipeEntity);
        return null;
    }

}
