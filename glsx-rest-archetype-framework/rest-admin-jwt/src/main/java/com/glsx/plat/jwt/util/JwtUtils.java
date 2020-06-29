package com.glsx.plat.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.glsx.plat.jwt.base.BaseJwtUser;
import com.glsx.plat.jwt.config.JwtConfigProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author payu
 * @desc jwt工具类
 */
@Data
@Slf4j
@Component
public class JwtUtils<T extends BaseJwtUser> {

    @Value("${spring.application.name}")
    private String application;

    @Autowired
    private JwtConfigProperties properties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * jwt-session-前缀
     */
    public final String JWT_SESSION_PREFIX = "JWT-SESSION-";

    public final String CLAZZ = "clazz";
    public final String BEARER = "Bearer ";

    /**
     * 生成jwt
     *
     * @param header
     * @return
     */
    public String create(Map<String, Object> header) {
        return create(header, new HashMap<>(0), properties.getTtl());
    }

    public String create(Map<String, Object> header, Map<String, String> claims) {
        return create(header, claims, properties.getTtl());
    }

    public String create(Map<String, Object> header, long timeout) {
        return create(header, new HashMap<>(0), timeout);
    }

    public Class getJwtUserClass(String token) throws ClassNotFoundException {
        DecodedJWT jwt = decode(token);
        String jwtUserClazz = jwt.getClaim(CLAZZ).asString();
        Class clazz = Class.forName(jwtUserClazz);
        return clazz;
    }

    /**
     * 生成jwt
     *
     * @param header
     * @param claims
     * @param timeout
     * @return
     */
    public String create(Map<String, Object> header, Map<String, String> claims, long timeout) {
        String token;
        try {
            Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
            Date date = new Date(System.currentTimeMillis() + timeout * 1000);
            JWTCreator.Builder builder = JWT.create()
                    .withHeader(header)
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
            Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
            JWTVerifier verifier = JWT.require(algorithm)
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
        Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
        Verification verification = JWT.require(algorithm);
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            verification.withClaim(entry.getKey(), (String) entry.getValue());
        }
        //JWT 正确的配置续期姿势
        JWTVerifier verifier = verification.acceptExpiresAt(System.currentTimeMillis() + properties.getTtl() * 1000)
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

    public String createToken(String jwtId, Map<String, String> userMap) {

        stringRedisTemplate.delete(JWT_SESSION_PREFIX + jwtId);

        String token = create(new HashMap<>(), userMap);

        stringRedisTemplate.opsForValue().set(JWT_SESSION_PREFIX + jwtId, token, properties.getTtl(), TimeUnit.SECONDS);

        return token;
    }

    /**
     * 校验token是否正确
     * 1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
     * 2 . 然后再对redisToken进行解密，解密成功则 继续流程 和 进行token续期
     *
     * @param token 密钥
     * @return 返回是否校验通过
     */
    public boolean verifyToken(String token) {
        //postman
        if (token.startsWith(BEARER)) token = token.replace(BEARER, "");
        try {
            Class clazz = getJwtUserClass(token);

            //解析token，反转成JwtUser对象
            Map<String, Object> userMap = parseClaim(clazz, token);
            T user = (T) ObjectUtils.mapToObject(userMap, clazz);

            //1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get(JWT_SESSION_PREFIX + user.getJwtId());
            if (!token.equals(redisToken)) return false;

            //2 . 验证token
            DecodedJWT decodedJWT = verify(token, userMap);

            //3 . Redis缓存JWT续期
            stringRedisTemplate.opsForValue().set(JWT_SESSION_PREFIX + user.getJwtId(), redisToken, properties.getTtl(), TimeUnit.SECONDS);

            return true;
        } catch (Exception e) { //捕捉到任何异常都视为校验失败
            log.error(e.getMessage());
            return false;
        }
    }

}
