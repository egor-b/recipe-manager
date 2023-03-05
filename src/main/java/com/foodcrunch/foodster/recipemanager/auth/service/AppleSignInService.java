package com.foodcrunch.foodster.recipemanager.auth.service;

import com.foodcrunch.foodster.recipemanager.auth.model.TokenResponse;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AppleSignInService {

    @Value("${service.apple.apple-auth-url}")
    private String APPLE_AUTH_URL;
    @Value("${service.apple.apple-revoke-url}")
    private String APPLE_REVOKE_URL;
    @Value("${service.apple.key-id}")
    private String KEY_ID;
    @Value("${service.apple.team-id}")
    private String TEAM_ID;
    @Value("${service.apple.client-id}")
    private String CLIENT_ID;

    private PrivateKey pKey;

    private PrivateKey getPrivateKey() throws IOException {
        //read your key
        File file = ResourceUtils.getFile("/mnt/AuthKey_89S3CH632C.p8");
        final PEMParser pemParser = new PEMParser(new FileReader(file));
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        final PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        final PrivateKey pKey = converter.getPrivateKey(object);
        pemParser.close();
        return pKey;
    }

    private String generateJWT() throws IOException {
        if (pKey == null) {
            pKey = getPrivateKey();
        }

        String token = Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, KEY_ID)
                .setIssuer(TEAM_ID)
                .setAudience("https://appleid.apple.com")
                .setSubject(CLIENT_ID)
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 2)))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(pKey, SignatureAlgorithm.ES256)
                .compact();

        return token;
    }

    /*
     * Returns unique user id from apple
     * */
    public String appleAuth(String authorizationCode, String jwt) throws UnirestException {
        HttpResponse<String> response = Unirest.post(APPLE_AUTH_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", CLIENT_ID)
                .field("client_secret", jwt)
                .field("grant_type", "authorization_code")
                .field("code", authorizationCode)
                .asString();

        TokenResponse tokenResponse = new Gson().fromJson(response.getBody(),TokenResponse.class);
        return tokenResponse.getRefresh_token();
    }

    public void revokeAppleAccount(String authorizationCode) throws IOException, UnirestException {
        String jwt = generateJWT();
        String refreshToken = appleAuth(authorizationCode, jwt);
        Unirest.post(APPLE_REVOKE_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", CLIENT_ID)
                .field("client_secret", jwt)
                .field("token", refreshToken)
                .field("token_type_hint", "refresh_token")
                .asString();
    }
}
