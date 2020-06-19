package com.glsx.plat.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author payu
 * @desc jwt工具类
 */
@Data
@Slf4j
@Component
@ConfigurationProperties("jwt.config")
public class JwtUtils {

    @Value("${spring.application.name}")
    private String application;

    /**
     * 签名私钥
     */
    private String key;

    /**
     * 签名的失效时间
     */
    private Long ttl;

    /**
     * jwt生成方
     */
    private String issuer;

    /**
     * jwt-session-前缀
     */
    public final String JWT_SESSION_PREFIX = "JWT-SESSION-";

    public final String clazz = "clazz";

    /**
     * 生成jwt
     *
     * @param header
     * @return
     */
    public String create(Map<String, Object> header) {
        return create(header, new HashMap<>(0), issuer, ttl);
    }

    public String create(Map<String, Object> header, Map<String, String> claims) {
        return create(header, claims, issuer, ttl);
    }

    public String create(Map<String, Object> header, long timeout) {
        return create(header, new HashMap<>(0), issuer, timeout);
    }

    public Class getJwtUserClass(String token) throws ClassNotFoundException {
        DecodedJWT jwt = decode(token);
        String jwtUserClazz = jwt.getClaim(clazz).asString();
        Class clazz = Class.forName(jwtUserClazz);
        return clazz;
    }

    /**
     * 生成jwt
     *
     * @param header
     * @param issuer
     * @return
     */
    public String create(Map<String, Object> header, Map<String, String> claims, String issuer, long timeout) {
        String token;
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            Date date = new Date(System.currentTimeMillis() + timeout);
            JWTCreator.Builder builder = JWT.create()
                    .withHeader(header)
                    .withIssuer(issuer)
                    .withExpiresAt(date);
            for (String key : claims.keySet()) {
                if ("class".equals(key)) continue;//跳过转换时多加的类型字段，claims.get("class")为java.lang.Class类型
                builder.withClaim(key, claims.get(key));
            }
            token = builder.sign(algorithm);
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return null;
        }
        return token;
    }

    /**
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT decode(String token) {
        DecodedJWT jwt;
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
//                    .acceptLeeway(1)
//                    .acceptExpiresAt(1)
//                    .acceptIssuedAt(1)
//                    .acceptNotBefore(1)
                    .build();
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return null;
        }
        return jwt;
    }

    /**
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT verify(String token, Map<String, Object> claims) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Verification verification = JWT.require(algorithm).withIssuer(issuer);
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            verification.withClaim(entry.getKey(), (String) entry.getValue());
        }
        JWTVerifier verifier = verification
                .acceptExpiresAt(System.currentTimeMillis() + ttl * 1000)  //JWT 正确的配置续期姿势
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt;
    }

    /**
     * 根据Token 获取Claim Map
     */
    public Map<String, Object> parseClaim(Class clazz, String token) {
        Map<String, Object> claim = new HashMap<>();
        Class superClazz = clazz.getSuperclass();
        Field[] fields1 = superClazz.getDeclaredFields();
        Field[] fields2 = clazz.getDeclaredFields();
        Field[] fields = ArrayUtils.addAll(fields1, fields2);
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = JWT.decode(token).getClaim(fieldName).asString();
            if ("null".equals(value)) value = null;
            claim.put(fieldName, value);
        }
        return claim;
    }

    /**
     * 根据Token 获取Claim Map
     */
    public Map<String, String> parseStringClaim(Class clazz, String token) {
        Map<String, String> claim = new HashMap<>();
        Class superClazz = clazz.getSuperclass();
        Field[] fields1 = superClazz.getDeclaredFields();
        Field[] fields2 = clazz.getDeclaredFields();
        Field[] fields = ArrayUtils.addAll(fields1, fields2);
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String value = JWT.decode(token).getClaim(fieldName).asString();
            if ("null".equals(value)) value = null;
            claim.put(fieldName, value);
        }
        return claim;
    }

}
