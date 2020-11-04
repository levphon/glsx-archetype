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
import com.glsx.plat.jwt.base.ComJwtUser;
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
import java.time.Instant;
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

    private Class<T> jwtUserClass;

    public final String BEARER = "Bearer ";

    /**
     * 定义JWT的发布者，这里可以起项目的拥有者
     */
    private final static String TOKEN_ISSUER = "issuer";

    /**
     * 根据用户的登录时间生成动态私钥
     *
     * @param instant 用户的登录时间，也就是申请令牌的时间
     * @return
     */
    public String genSecretKey(Instant instant) {
        return String.valueOf(instant.getEpochSecond());
    }

    public String create(Map<String, Object> claims) {
        return create(new HashMap<>(), claims, properties.getTtl());
    }

    public String create(Map<String, Object> header, Map<String, Object> claims) {
        return create(header, claims, properties.getTtl());
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
            JWTCreator.Builder builder = JWT.create()
                    .withIssuer(TOKEN_ISSUER)
                    .withHeader(header)
                    .withExpiresAt(new Date(System.currentTimeMillis() + timeout * 1000));
            for (String key : claims.keySet()) {
                if ("class".equals(key)) {
                    continue;//跳过转换时多加的类型字段，claims.get("class")为java.lang.Class类型
                }
                buildClaims(builder, claims);
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
     * 构建Claims
     *
     * @param builder
     * @param claims
     */
    private void buildClaims(JWTCreator.Builder builder, Map<String, Object> claims) {
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
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT decode(String token) {
        if (token.startsWith(BEARER)) {
            token = token.replace(BEARER, "");
        }
        DecodedJWT jwtToken = null;
        try {
            jwtToken = JWT.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwtToken;
    }

    /**
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT verify(String token) {
        if (token.startsWith(BEARER)) {
            token = token.replace(BEARER, "");
        }
        Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
        Verification verification = JWT.require(algorithm);
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
        if (token.startsWith(BEARER)) {
            token = token.replace(BEARER, "");
        }
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
     * @param userMap
     * @return
     */
    public String createToken(Map<String, Object> userMap) {
        return create(userMap);
    }

    /**
     * 创建缓存到redis的token
     *
     * @param jwtId
     * @param userMap
     * @return
     */
    public String createCachedToken(String jwtId, Map<String, Object> userMap) {

        stringRedisTemplate.delete(jwtId);

        String token = createToken(userMap);

        stringRedisTemplate.opsForValue().set(jwtId, token, properties.getTtl(), TimeUnit.SECONDS);

        return token;
    }

    /**
     * 验证token
     *
     * @param token
     */
    public boolean verifyToken(String token) {
        log.debug("verify token [" + token + "]");
        Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
        //校验Token
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(TOKEN_ISSUER).build();
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error(e.getMessage());
        } catch (Exception e) {//捕捉到其他任何异常都视为校验失败
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 校验token是否正确,以及验证缓存是否存在
     * 1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
     * 2 . 然后再对redisToken进行解密，解密成功则 继续流程 和 进行token续期
     *
     * @param token 密钥
     * @return 返回是否校验通过
     */
    public boolean verifyCachedToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        if (token.startsWith(BEARER)) {
            token = token.replace(BEARER, "");
        }

        try {
            Class clazz = getJwtUserClass(token);

            //解析token，反转成JwtUser对象
            Map<String, Object> userMap = parseClaim(token, clazz);

            T user = (T) ObjectUtils.mapToObject(userMap, clazz);

            //1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get(user.getJwtId());

            if (!token.equals(redisToken)) {
                return false;
            }

            //2 . 验证token
            DecodedJWT decodedJWT = verify(token);

            //3 . Redis缓存JWT续期
            stringRedisTemplate.opsForValue().set(user.getJwtId(), redisToken, properties.getTtl(), TimeUnit.SECONDS);

            return true;
        } catch (TokenExpiredException e) {
            log.error(e.getMessage());
        } catch (Exception e) {//捕捉到其他任何异常都视为校验失败
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    //刷新Token
    public String getRefreshToken(String secretKey, DecodedJWT jwtToken) {
        return getRefreshToken(secretKey, jwtToken, properties.getTtl());
    }

    //重载的刷新Token
    public String getRefreshToken(String secretKey, DecodedJWT jwtToken, Long validityTime) {
        return getRefreshToken(secretKey, jwtToken, validityTime, properties.getRefreshTtl());
    }

    /**
     * 根据要过期的token获取新token
     *
     * @param jwtToken         上次的JWT经过解析后的对象
     * @param validityTime     有效时间
     * @param allowExpiresTime 允许过期的时间
     * @return
     */
    public String getRefreshToken(DecodedJWT jwtToken, long validityTime, Long allowExpiresTime) {
        Instant now = Instant.now();
        Instant exp = jwtToken.getExpiresAt().toInstant();
        //如果当前时间减去JWT过期时间，大于可以重新申请JWT的时间，说明不可以重新申请了，就得重新登录了，此时返回null，否则就是可以重新申请，开始在后台重新生成新的JWT。
        if (allowExpiresTime != null && (now.getEpochSecond() - exp.getEpochSecond()) > allowExpiresTime) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(properties.getKey());
        //在原有的JWT的过期时间的基础上，加上这次的有效时间，得到新的JWT的过期时间
        Instant newExp = exp.plusSeconds(validityTime);
        //创建JWT
        String token = JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withClaim("sub", jwtToken.getSubject())
                .withClaim("iat", Date.from(exp))
                .withClaim("exp", Date.from(newExp))
                .sign(algorithm);
        log.trace("create refresh token [" + token + "]; iat: " + Date.from(exp) + " exp: " + Date.from(newExp));
        return token;
    }

    /**
     * 根据要过期的token获取新token
     *
     * @param secretKey        根据用户上次登录时的时间，生成的密钥
     * @param jwtToken         上次的JWT经过解析后的对象
     * @param validityTime     有效时间
     * @param allowExpiresTime 允许过期的时间
     * @return
     */
    public String getRefreshToken(String secretKey, DecodedJWT jwtToken, long validityTime, Long allowExpiresTime) {
        Instant now = Instant.now();
        Instant exp = jwtToken.getExpiresAt().toInstant();
        //如果当前时间减去JWT过期时间，大于可以重新申请JWT的时间，说明不可以重新申请了，就得重新登录了，此时返回null，否则就是可以重新申请，开始在后台重新生成新的JWT。
        if (allowExpiresTime != null && (now.getEpochSecond() - exp.getEpochSecond()) > allowExpiresTime) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        //在原有的JWT的过期时间的基础上，加上这次的有效时间，得到新的JWT的过期时间
        Instant newExp = exp.plusSeconds(validityTime);
        //创建JWT
        String token = JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withClaim("sub", jwtToken.getSubject())
                .withClaim("iat", Date.from(exp))
                .withClaim("exp", Date.from(newExp))
                .sign(algorithm);
        log.trace("create refresh token [" + token + "]; iat: " + Date.from(exp) + " exp: " + Date.from(newExp));
        return token;
    }

    /**
     * 删除缓存token
     *
     * @param jwtId
     */
    public void removeCachedToken(String jwtId) {
        stringRedisTemplate.delete(jwtId);
    }

    public static void main(String[] args) {
        String secretKey = "5371f568a45e5ab1f442c38e0932aef24447139c";

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJiZWxvbmciOiLlub_ogZTotZvorq8iLCJhcHBsaWNhdGlvbiI6Imdsc3gtbmUtc2hpZWxkLXVzZXJjZW50ZXIiLCJkZXB0SWQiOjEsImV4cCI6MTYwNDU1OTgzNywidXNlcklkIjoxLCJjbGF6eiI6ImNvbS5nbHN4LnBsYXQuand0LmJhc2UuQ29tSnd0VXNlciIsImFjY291bnQiOiJhZG1pbiIsImp3dElkIjoiZ2xzeC1uZS1zaGllbGQtdXNlcmNlbnRlcjo2MDc5NzE3Yy03NTA1LTQxNzQtYTgxZi02MzEwMDMyY2NlM2NfSldULVNFU1NJT04tMSJ9.y6nc342oSqB48pNBuuiX7nShx0wjs_S-QUK4AgX4JYE";

        JwtUtils<ComJwtUser> jwtUtils = new JwtUtils<>();

        Map<String, Object> claimMap = jwtUtils.parseClaim(token, ComJwtUser.class);

        System.out.println(claimMap.size());

        DecodedJWT decodedJWT = jwtUtils.decode(token);

        String newToken = jwtUtils.getRefreshToken(secretKey, decodedJWT, 28800L, 86400L);

        System.out.printf(newToken);
    }

}