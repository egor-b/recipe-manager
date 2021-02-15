package com.foodcrunch.foodster.recipemanager.config.service;


import com.foodcrunch.foodster.recipemanager.auth.model.RoleModel;
import com.foodcrunch.foodster.recipemanager.auth.model.User;
import com.foodcrunch.foodster.recipemanager.auth.model.UserEntity;
import com.foodcrunch.foodster.recipemanager.auth.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityService {

    public static final String TOKEN_PREFIX = "Bearer ";
    private final UserService userService;

    public String getBearerToken(HttpServletRequest request) {
        String bearerToken = null;
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(TOKEN_PREFIX)) {
            bearerToken = authorization.split(" ")[1].trim();;
        }
        return bearerToken;
    }

    public final FirebaseToken userTokenVerification(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException err) {
            System.out.println(err.getMessage());
            return null;
        }
    }

    public final User getUserProfile(FirebaseToken token) {
        UserEntity user = userService.loadUserByUid(token.getUid()).blockFirst();
        Set<RoleModel> roleModels = user.getRole().stream().map(m ->
                RoleModel.builder().name(m.getName()).build()).collect(Collectors.toSet()
        );
        return User.builder()
                .phone(user.getPhone())
                .country(user.getCountry())
                .disable(user.isDisable())
                .email(user.getEmail())
                .lName(user.getLName())
                .name(user.getName())
                .pic(user.getPic())
                .uid(user.getUid())
                .username(user.getUsername())
                .role(roleModels)
                .build();
    }
}
