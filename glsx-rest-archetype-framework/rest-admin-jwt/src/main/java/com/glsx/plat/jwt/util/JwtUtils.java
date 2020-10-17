package com.glsx.plat.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.glsx.plat.jwt.base.BaseJwtUser;
import com.glsx.plat.jwt.config.JwtConfigProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public String create(Map<String, Object> header, Map<String, Object> claims) {
        return create(header, claims, properties.getTtl());
    }

    public String create(Map<String, Object> header, long timeout) {
        return create(header, new HashMap<>(0), timeout);
    }

    /**
     * 获取jwt用户类
     *
     * @param token
     * @return
     * @throws Exception
     */
    public Class getJwtUserClass(String token) throws Exception {
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
    public String create(Map<String, Object> header, Map<String, Object> claims, long timeout) {
        String token;
        try {
            Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
            Date date = new Date(System.currentTimeMillis() + timeout * 1000);
            JWTCreator.Builder builder = JWT.create()
                    .withHeader(header)
                    .withExpiresAt(date);
            for (String key : claims.keySet()) {
                if ("class".equals(key)) continue;//跳过转换时多加的类型字段，claims.get("class")为java.lang.Class类型
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    if (entry.getValue() != null) {
                        Object val = entry.getValue();
                        Class type = val.getClass();
                        if (type == boolean.class || type == Boolean.class) {
                            builder.withClaim(entry.getKey(), (Boolean) entry.getValue());
                        } else if (type == int.class || type == Integer.class) {
                            builder.withClaim(entry.getKey(), (Integer) entry.getValue());
                        } else if (type == long.class || type == Long.class) {
                            builder.withClaim(entry.getKey(), (Long) entry.getValue());
                        } else if (type == double.class || type == Double.class) {
                            builder.withClaim(entry.getKey(), (Double) entry.getValue());
                        } else if (type == String.class) {
                            builder.withClaim(entry.getKey(), (String) entry.getValue());
                        } else if (type == Date.class) {
                            builder.withClaim(entry.getKey(), (Date) entry.getValue());
                        } else if (type == Map.class) {

                        } else if (type == List.class) {
//                            builder.withClaim(entry.getKey(), claim.asList(type.getComponentType()));
                        } else if (type.isArray()) {
                            Class componentType = type.getComponentType();
                            if (componentType == int.class || componentType == Integer.class) {
                                builder.withArrayClaim(entry.getKey(), (Integer[]) entry.getValue());
                            } else if (componentType == long.class || componentType == Long.class) {
                                builder.withArrayClaim(entry.getKey(), (Long[]) entry.getValue());
                            } else if (componentType == String.class) {
                                builder.withArrayClaim(entry.getKey(), (String[]) entry.getValue());
                            }
                        }
                    }
                }
            }
            token = builder.sign(algorithm);
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            log.error(e.getMessage());
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
    public DecodedJWT decode(String token) throws Exception {
        if (token.startsWith(BEARER)) token = token.replace(BEARER, "");
        Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
        JWTVerifier verifier = JWT.require(algorithm)
//                    .acceptLeeway(1)
//                    .acceptExpiresAt(1)
//                    .acceptIssuedAt(1)
//                    .acceptNotBefore(1)
                .build();
        return verifier.verify(token);
    }

    /**
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT verify(String token, Map<String, Object> claims) {
        if (token.startsWith(BEARER)) token = token.replace(BEARER, "");
        Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
        Verification verification = JWT.require(algorithm);
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            if (entry.getValue() != null) {
                Object val = entry.getValue();
                Class type = val.getClass();
                if (type == boolean.class || type == Boolean.class) {
                    verification.withClaim(entry.getKey(), (Boolean) entry.getValue());
                } else if (type == int.class || type == Integer.class) {
                    verification.withClaim(entry.getKey(), (Integer) entry.getValue());
                } else if (type == long.class || type == Long.class) {
                    verification.withClaim(entry.getKey(), (Long) entry.getValue());
                } else if (type == double.class || type == Double.class) {
                    verification.withClaim(entry.getKey(), (Double) entry.getValue());
                } else if (type == String.class) {
                    verification.withClaim(entry.getKey(), (String) entry.getValue());
                } else if (type == Date.class) {
                    verification.withClaim(entry.getKey(), (Date) entry.getValue());
                } else if (type == Map.class) {

                } else if (type == List.class) {

                } else if (type.isArray()) {
                    Class componentType = type.getComponentType();
                    if (componentType == int.class || componentType == Integer.class) {
                        verification.withArrayClaim(entry.getKey(), (Integer[]) entry.getValue());
                    } else if (componentType == long.class || componentType == Long.class) {
                        verification.withArrayClaim(entry.getKey(), (Long[]) entry.getValue());
                    } else if (componentType == String.class) {
                        verification.withArrayClaim(entry.getKey(), (String[]) entry.getValue());
                    }
                }
            }
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
    public Map<String, Object> parseClaim(String token, Class clazz) {
        if (token.startsWith(BEARER)) token = token.replace(BEARER, "");
        Map<String, Object> claimMap = new HashMap<>();
        Class superClazz = clazz.getSuperclass();
        Field[] fields1 = superClazz.getDeclaredFields();
        Field[] fields2 = clazz.getDeclaredFields();
        Field[] fields = ArrayUtils.addAll(fields1, fields2);
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            Claim claim = JWT.decode(token).getClaim(fieldName);
            if (!claim.isNull()) {
                Class type = field.getType();
                if (type == boolean.class || type == Boolean.class) {
                    claimMap.put(fieldName, claim.asBoolean());
                } else if (type == int.class || type == Integer.class) {
                    claimMap.put(fieldName, claim.asInt());
                } else if (type == long.class || type == Long.class) {
                    claimMap.put(fieldName, claim.asLong());
                } else if (type == double.class || type == Double.class) {
                    claimMap.put(fieldName, claim.asDouble());
                } else if (type == String.class) {
                    claimMap.put(fieldName, claim.asString());
                } else if (type == Date.class) {
                    claimMap.put(fieldName, claim.asDate());
                } else if (type == Map.class) {
                    claimMap.put(fieldName, claim.asMap());
//                } else if (type == List.class) {
//                    claimMap.put(fieldName, claim.asList(type.getComponentType()));
                } else if (type.isArray()) {
                    claimMap.put(fieldName, claim.asArray(type.getComponentType()));
                }
            }
        }
        return claimMap;
    }

    /**
     * 创建token
     *
     * @param jwtId
     * @param userMap
     * @return
     */
    public String createToken(String jwtId, Map<String, Object> userMap) {

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
        if (StringUtils.isEmpty(token)) return false;

        if (token.startsWith(BEARER)) token = token.replace(BEARER, "");
        try {
            Class clazz = getJwtUserClass(token);

            //解析token，反转成JwtUser对象
            Map<String, Object> userMap = parseClaim(token, clazz);
            T user = (T) ObjectUtils.mapToObject(userMap, clazz);

            //1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get(JWT_SESSION_PREFIX + user.getJwtId());
            if (!token.equals(redisToken)) return false;

            //2 . 验证token
            DecodedJWT decodedJWT = verify(token, userMap);

            //3 . Redis缓存JWT续期
            stringRedisTemplate.opsForValue().set(JWT_SESSION_PREFIX + user.getJwtId(), redisToken, properties.getTtl(), TimeUnit.SECONDS);

            return true;
        } catch (TokenExpiredException e) {
            log.error(e.getMessage());
        } catch (Exception e) {//捕捉到其他任何异常都视为校验失败
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    public void refreshToken(String token) {

    }

}