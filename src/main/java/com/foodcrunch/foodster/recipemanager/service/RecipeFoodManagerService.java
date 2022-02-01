package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.constant.LogLevel;
import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.model.entity.FoodstuffEntity;
import com.foodcrunch.foodster.recipemanager.repository.FoodstuffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.foodcrunch.foodster.recipemanager.constant.ExceptionsConstants.TOO_MANY_RECIPES;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeFoodManagerService {

    private final FoodstuffRepository foodstuffRepository;
    private final LogService logService;

    public Flux<FoodstuffEntity> getFoodByName(String name, Integer pageNumber, Integer pageSize, Sort.Direction sortOrder, String sortKey) {
        if (pageSize>100) {
            String message = logService.buildLogEvent(TOO_MANY_RECIPES, LogLevel.ERROR, null, pageSize);
            return Flux.error(new BadRequestException(message));
        }
        PageRequest page = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder,sortKey));
        return Flux.fromIterable(foodstuffRepository.findByNameContainingIgnoreCase(page, name));
    }

}
