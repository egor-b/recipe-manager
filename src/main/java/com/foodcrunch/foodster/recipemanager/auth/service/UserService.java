package com.foodcrunch.foodster.recipemanager.auth.service;

import com.foodcrunch.foodster.recipemanager.auth.exception.UserNotFoundException;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void registerNewUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public void updateUser(UserEntity userEntity) {

    }

    public Flux<UserEntity> findUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            String message = String.format("User was not found by username {}", username);
            log.info(message);
            return Flux.error(new UserNotFoundException(message));
        }
        return Flux.just(userEntity);
    }

    public Flux<UserEntity> findUserByUserId(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity == null) {
            String message = String.format("User was not found by id {}", id);
            log.info(message);
            return Flux.error(new UserNotFoundException(message));
        }
        return Flux.just(userEntity);
    }

}
