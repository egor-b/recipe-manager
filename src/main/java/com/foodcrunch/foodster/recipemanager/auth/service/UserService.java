package com.foodcrunch.foodster.recipemanager.auth.service;

import com.foodcrunch.foodster.recipemanager.auth.exception.UserNotFoundException;
import com.foodcrunch.foodster.recipemanager.auth.model.RoleEntity;
import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Flux<UserEntity> loadUserByUid(String uid) {
        UserEntity userEntity = userRepository.findByUid(uid);
        if (userEntity == null) {
            String message = String.format("User was not found by uid {}", uid);
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

//    public void createNewUser(User user) {
//        try {
//            UserRecord.CreateRequest userRequest = new UserRecord.CreateRequest()
//                    .setEmail(user.getEmail())
//                    .setPassword(user.getPassword())
//                    .setPhoneNumber(user.getPhone())
//                    .setDisplayName(user.getName() + " " + user.getLName())
//                    .setPhotoUrl(user.getPic())
//                    .setDisabled(user.isDisable());
//
//            UserRecord userRecord = FirebaseAuth.getInstance().createUser(userRequest);
//
//            user.setUid(userRecord.getUid());
//
//            userRepository.save(convertUserModelToEntity(user));
//            emailVerificationRequest(user.getEmail());
//        } catch (FirebaseAuthException err) {
//            err.getStackTrace();
//        }
//    }

    public void updateUser(User user) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getUid())
                    .setEmail(user.getEmail())
                    .setDisplayName(user.getName() + " " + user.getLName())
                    .setPhotoUrl(user.getPic())
                    .setDisabled(user.isDisable());
            UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
            userRepository.save(convertUserModelToEntity(user));
        } catch (FirebaseAuthException err) {
            err.getStackTrace();
        }
    }

    public final void changePasswordRequest(User user) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getUid());
            FirebaseAuth.getInstance().updateUser(request);
        } catch (FirebaseAuthException err) {
            err.getStackTrace();
        }
    }

    public final void changeEmailRequest(User user) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getUid())
                    .setEmail(user.getEmail())
                    .setEmailVerified(false);
            UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
            userRepository.updateUserEmail(user.getEmail(), user.getUid());
            emailVerificationRequest(user.getEmail());
            log.info("New email was setted: " + userRecord.getUid());
        } catch (FirebaseAuthException err) {
            err.getStackTrace();
        }
    }

    public final void emailVerificationRequest(String email) {
        try {
            FirebaseAuth.getInstance().generateEmailVerificationLink(email);
        } catch (FirebaseAuthException err) {
            err.getStackTrace();
        }
    }

    public final void userTokenVerification(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            System.out.println(decodedToken.getClaims());
        } catch (FirebaseAuthException err) {
            err.getStackTrace();
        }
    }

    protected UserEntity convertUserModelToEntity(User user) {
        UserEntity userEntity = new UserEntity();
        Set<RoleEntity> roleEntity = new HashSet<>();

        userEntity.setCountry(user.getCountry());
        userEntity.setDisable(user.isDisable());
        userEntity.setEmail(user.getEmail());
        userEntity.setLName(user.getLName());
        userEntity.setName(user.getName());
        userEntity.setPhone(user.getPhone());
        userEntity.setPic(user.getPic());
        userEntity.setUid(user.getUid());
        userEntity.setUsername(user.getUsername());
        userEntity.setRole(roleEntity);

        return userEntity;
    }

}
