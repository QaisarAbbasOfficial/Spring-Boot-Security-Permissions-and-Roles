package com.springsecuritydemo.application.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.springsecuritydemo.application.constants.SecurityConstants;
import com.springsecuritydemo.data.entities.Privilege;
import com.springsecuritydemo.data.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JwtOutils {

    @Value("${springbootwebfluxjjwt.jjwt.secret}")
    private String secret;
    @Value("${springbootwebfluxjjwt.jjwt.expiration}")
    private long expiration;


    public DecodedJWT verify(String token) throws JWTVerificationException {
        return JWT.require(
//                Algorithm.RSA512(new )
                Algorithm.HMAC512(secret.getBytes())
        )
                .build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
    }

    public String create(User user) throws JWTVerificationException {
        Collection<Privilege> authorities = user.getAuthorities();
        String[] privileges = authorities.stream()
                .map(privilege -> privilege.getAuthority())
                .distinct().toArray(String[]::new);
        return JWT.create()
                .withSubject(user.getUsername())
                .withArrayClaim("privileges", privileges)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(HMAC512(secret.getBytes()));
//                .sign(HMAC512(secret.getBytes()));
    }
}
