package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.repository.UserRepository;
import com.foodcrunch.foodster.recipemanager.constant.LogLevel;
import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.exception.NotFoundException;
import com.foodcrunch.foodster.recipemanager.exception.RecipiesLockedException;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepositoryInterface;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.text.MessageFormat;
import java.util.Map;

import static com.foodcrunch.foodster.recipemanager.constant.ExceptionsConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeManagerService {

    private final RecipeRepository recipeRepository;
    private final RecipeRepositoryInterface recipeRepositoryInterface;
    private final LogService logService;
    private final UserRepository userRepository;

    @Value("${service.user.restriction.max-incomplete}")
    private Integer maxIncomplete;
    @Value("${service.user.restriction.minIngredients}")
    private Integer minIngredients;
    @Value("${service.user.restriction.minSteps}")
    private Integer minSteps;

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

        if (!StringUtils.isEmpty(criterias.get("userId")) && pageNumber == 0) {
            UserEntity user = userRepository.findByUid(criterias.get("userId"));
            if (user.getViolation() > maxIncomplete) {
                String notice = MessageFormat.format("Sorry {0}, you do not follow community rules. You have {1} incorrect recipes but you can continue use your own recipes. " +
                        "In order to regain access to all recipes, you should have no more than 10 incorrect recipes.", user.getName(), user.getViolation());
                return Flux.error(new RecipiesLockedException(notice));
            }
        }

        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder, sortKey));
        return Flux.fromIterable(recipeRepositoryInterface.findByPagingCriteria(page, criterias));
    }

    public Flux<RecipeEntity> getRecipesByUserId(String userId, int pageNumber, int pageSize, String sortKey, Sort.Direction sortOrder, Map<String, String> criterias) {
        if (pageSize>100) {
            String message = logService.buildLogEvent(TOO_MANY_RECIPES, LogLevel.ERROR, null, pageSize);
            return Flux.error(new BadRequestException(message));
        }
        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder, sortKey));
        if (criterias.isEmpty()) {
            return Flux.fromIterable(recipeRepository.findByUserIdEquals(page, userId));
        } else {
            return Flux.fromIterable(recipeRepositoryInterface.findByPagingCriteria(page, criterias));
        }

    }

    public Flux<Object> saveRecipe(RecipeEntity recipeEntity) {
        if (recipeEntity.getFoodEntity().toArray().length <= minIngredients || recipeEntity.getStepEntity().toArray().length <= minSteps) {
            recipeEntity = updateViolation("false", recipeEntity);
        }
        recipeRepositoryInterface.saveRecipe(recipeEntity);
        return Flux.just();
    }

    public Flux<Object> updateRecipe(RecipeEntity recipeEntity) {
        if (recipeEntity.getVisible().equals("true") && (recipeEntity.getFoodEntity().toArray().length <= minIngredients || recipeEntity.getStepEntity().toArray().length <= minSteps)) {
            recipeEntity = updateViolation("false", recipeEntity);
        }
        if (recipeEntity.getVisible().equals("false") && recipeEntity.getFoodEntity().toArray().length > minIngredients && recipeEntity.getStepEntity().toArray().length > minSteps ) {
            recipeEntity = updateViolation("true", recipeEntity);
        }
        recipeRepositoryInterface.updateRecipe(recipeEntity);
        return Flux.just();
    }

    public void submitReport() {

    }
    private RecipeEntity updateViolation(String isVisible, RecipeEntity recipeEntity) {
        UserEntity user = userRepository.findByUid(recipeEntity.getUserId());
        if (isVisible.equals("false")) {
            userRepository.updateUserByUID(recipeEntity.getUserId(), user.getViolation() + 1);
        }
        if (isVisible.equals("true") && user.getViolation() > 0) {
            userRepository.updateUserByUID(recipeEntity.getUserId(), user.getViolation() - 1);
        }
        recipeEntity.setVisible(isVisible);
        return recipeEntity;
    }

}
