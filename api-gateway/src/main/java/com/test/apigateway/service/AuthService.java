package com.test.apigateway.service;

import com.test.apigateway.client.UserClient;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordService passwordService;
    private final UserClient userClient;
    private final MessageSource messageSource;

    @Value("${security.jwt.token.secret}")
    private String SECRET_KEY;

    public String login(String username, String password) {
        var user = userClient.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException(messageSource.getMessage("auth.wrong.user.password", null, null));
        }

        if (!passwordService.matches(password, user.password())) {
            throw new IllegalArgumentException(messageSource.getMessage("auth.wrong.user.password", null, null));
        }

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
}
