package com.foodcrunch.foodster.recipemanager.auth.service;

import com.foodcrunch.foodster.recipemanager.auth.exception.UserNotFoundException;
import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.repository.UserRepository;
import com.foodcrunch.foodster.recipemanager.auth.repository.UserUpdateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserUpdateRepository userUpdateRepository;
    private final  AppleSignInService appleSignInService;

    public void createUser(User user) {
        UserEntity checkUser = userRepository.findByUid(user.getUid());
        if (ObjectUtils.isEmpty(checkUser)) {
            userRepository.save(convertUserModelToEntity(user));
        }
    }

    public void updateUserEmail(User user) {
        userRepository.updateUserEmail(user.getEmail(), user.getUid());
        emailVerificationRequest(user.getEmail());
    }

    public final void emailVerificationRequest(String email) {
        try {
            FirebaseAuth.getInstance().generateEmailVerificationLink(email);
        } catch (FirebaseAuthException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public Flux<UserEntity> loadUserByUid(String uid) {
        UserEntity userEntity = userRepository.findByUid(uid);
        if (userEntity == null) {
            String message = String.format("User was not found by uid {}", uid);
            log.info(message);
            return Flux.error(new UserNotFoundException(message));
        }
        return Flux.just(userEntity);
    }

    public void deleteUser(String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            userRepository.deleteUser(uid);
        } catch (FirebaseAuthException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public void deleteAppleUser(String authCode, String uid) {
        try {
            appleSignInService.revokeAppleAccount(authCode);
            FirebaseAuth.getInstance().deleteUser(uid);
            userRepository.deleteUser(uid);
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        } catch (FirebaseAuthException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    @Transactional
    public void updateUserPic(User user) {
        userUpdateRepository.updateUser(user);
    }


    protected UserEntity convertUserModelToEntity(User user) {
        UserEntity userEntity = new UserEntity();

        userEntity.setCountry(user.getCountry());
        userEntity.setEmail(user.getEmail());
        userEntity.setLName(user.getLName());
        userEntity.setName(user.getName());
        userEntity.setPhone(user.getPhone());
        userEntity.setPic(user.getPic());
        userEntity.setUid(user.getUid());
        userEntity.setUsername(user.getUsername());
        userEntity.setAccountType(user.getAccountType());
//        userEntity.setRole(roleEntity);

        return userEntity;
    }

}
