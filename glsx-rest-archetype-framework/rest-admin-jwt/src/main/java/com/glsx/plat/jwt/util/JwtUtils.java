package com.glsx.plat.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.glsx.plat.jwt.base.BaseJwtUser;
import com.glsx.plat.jwt.base.ComJwtUser;
import com.glsx.plat.jwt.config.JwtConfigProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author payu
 * @desc jwt工具类
 */
@Slf4j
@Getter
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

    private final static String PAYLOAD_MAP = "payloadMap";

    private Class<T> jwtUserClass;


    /**
     * 根据用户的登录时间生成动态私钥
     *
     * @param instant 用户的登录时间，也就是申请令牌的时间
     * @return
     */
    public String genSecretKey(Instant instant) {
        return String.valueOf(instant.getEpochSecond());
    }

    /**
     * 创建token
     *
     * @param userMap
     * @return
     */
    public String createToken(Map<String, Object> userMap) {
        String jwtId = userMap.get("jwtId") == null ? UUID.randomUUID().toString() : (String) userMap.get("jwtId");
        String token = create(jwtId, userMap, Instant.now());
        log.debug("create id {} token [" + token + "]", jwtId);
        return token;
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

        String token = create(jwtId, userMap, Instant.now());

        stringRedisTemplate.opsForValue().set(jwtId, token, properties.getTtl(), TimeUnit.SECONDS);
        log.debug("create id {} token [" + token + "]", jwtId);
        return token;
    }

    /**
     * 生成token
     *
     * @param jwtId   JWT中自定义的id
     * @param claims  JWT中payload部分自定义的内容
     * @param issueAt 用户登录的时间，也就是申请令牌的时间
     * @return
     */
    private String create(String jwtId, Map<String, Object> claims, Instant issueAt) {
        return create(properties.getKey(), jwtId, claims, issueAt, properties.getTtl());
    }

    /**
     * 生成token
     *
     * @param secretKey    密钥
     * @param jwtId        JWT中自定义的id
     * @param claims       JWT中payload部分自定义的内容
     * @param issueAt      用户登录的时间，也就是申请令牌的时间
     * @param validityTime 令牌有效时间
     * @return
     */
    private String create(String secretKey, String jwtId, Map<String, Object> claims, Instant issueAt, Long validityTime) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Instant exp = issueAt.plusSeconds(validityTime);

        Map<String, Object> newClaims = new HashMap<>();

        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("class".equals(key) || Objects.isNull(value)) {
                continue;//跳过转换时多加的类型字段，claims.get("class")为java.lang.Class类型
            }
            newClaims.put(key, value);
        }
        JWTCreator.Builder builder = JWT.create()
                .withJWTId(jwtId)
                .withClaim("iat", Date.from(issueAt))
                .withClaim("exp", Date.from(exp))
                .withClaim(PAYLOAD_MAP, newClaims);

        if (newClaims.get("account") != null) {
            builder.withSubject(newClaims.get("account").toString());
        }

        String token = builder.sign(algorithm);
        log.trace("create token [" + token + "]; iat: " + Date.from(exp) + " exp: " + Date.from(exp));
        return token;
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
        return JWT.decode(token);
    }

    /**
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT verify(String token) {
        return verify(properties.getKey(), token);
    }

    /**
     * 验证jwt
     *
     * @param token
     * @return
     */
    public DecodedJWT verify(String secretKey, String token) {
        if (token.startsWith(BEARER)) {
            token = token.replace(BEARER, "");
        }
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT jwt = null;
        try {
            jwt = verifier.verify(token);
        } catch (JWTVerificationException ve) {
            log.error(ve.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return jwt;
    }

    /**
     * 根据Token 获取Claim Map
     */
    public Map<String, Object> parseClaim(String token) {
        return this.decode(token).getClaim(PAYLOAD_MAP).asMap();
    }

    /**
     * 获取jwt用户类
     *
     * @param token
     * @return
     * @throws Exception
     */
    public Class getJwtUserClass(String token) throws ClassNotFoundException {
        String jwtUserClazz = (String) parseClaim(token).get(CLAZZ);
        log.debug("token jwt user clazz [" + jwtUserClazz + "]");
        Class clazz = Class.forName(jwtUserClazz);
        return clazz;
    }


    /**
     * 验证token
     *
     * @param token
     */
    public boolean verifyToken(String token) {
        log.debug("verify token [" + token + "]");
        try {
            DecodedJWT decodedJWT = this.verify(token);
            return true;
        } catch (JWTVerificationException ve) {
            log.error(ve.getMessage());
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
     * @param token
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
            //1 . 验证token
            DecodedJWT decodedJWT = verify(token);

            //2 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get(decodedJWT.getId());

            if (!token.equals(redisToken)) {
                return false;
            }

            //3 . Redis缓存JWT续期
            stringRedisTemplate.opsForValue().set(decodedJWT.getId(), redisToken, properties.getTtl(), TimeUnit.SECONDS);

            return true;
        } catch (TokenExpiredException e) {
            log.error(e.getMessage());
        } catch (Exception e) {//捕捉到其他任何异常都视为校验失败
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 判断是否超出允许续期时间
     *
     * @param jwtToken
     * @param instant
     * @return
     */
    public boolean isTokenOutAllowExpires(DecodedJWT jwtToken, Instant instant) {
        Instant exp = jwtToken.getExpiresAt().toInstant();
        Long allowExpiresTime = properties.getRefreshTtl();
        return allowExpiresTime != null && (instant.getEpochSecond() - exp.getEpochSecond()) > allowExpiresTime;
    }

    /**
     * 判断token是否过期
     *
     * @param jwtToken
     * @param instant
     * @return
     */
    public boolean isTokenOutExpiresAt(DecodedJWT jwtToken, Instant instant) {
        Instant exp = jwtToken.getExpiresAt().toInstant();
        return instant.compareTo(exp) >= 0;
    }

    /**
     * 判断是否需要刷新token
     *
     * @param jwtToken
     * @return
     */
    public boolean isNeedRefreshToken(DecodedJWT jwtToken) {
        Instant now = Instant.now();
        Instant iat = jwtToken.getIssuedAt().toInstant();
        //Instant exp = jwtToken.getExpiresAt().toInstant();
        //ExpiresAt之后允许刷新的标识
        boolean outAllowExpiredFlag = isTokenOutAllowExpires(jwtToken, now);
        if (!outAllowExpiredFlag) {
            boolean outExpiredAtFlag = isTokenOutExpiresAt(jwtToken, now);
            if (!outExpiredAtFlag) {
                // 判断时间,如果token达到exp的4分之3，就刷新token
                Long ttl = properties.getTtl();
                Float ratioTtl = ttl * 0.75F;
                Long diffEpochSeconds = now.getEpochSecond() - iat.getEpochSecond();
                log.debug("ttl={}, ratioTtl={}, differenceEpochSeconds={}", ttl, ratioTtl, diffEpochSeconds);
                if (diffEpochSeconds > ratioTtl) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重载的刷新Token
     *
     * @param jwtToken
     * @return
     */
    public String refreshToken(DecodedJWT jwtToken) {
        return refreshToken(properties.getKey(), jwtToken, properties.getTtl(), properties.getRefreshTtl());
    }

    /**
     * 重载的刷新Token
     *
     * @param jwtToken
     * @return
     */
    public String refreshCachedToken(DecodedJWT jwtToken) {
        String jwtId = jwtToken.getId();

        String refreshToken = refreshToken(properties.getKey(), jwtToken, properties.getTtl(), properties.getRefreshTtl());

        stringRedisTemplate.opsForValue().set(jwtId, refreshToken, properties.getTtl(), TimeUnit.SECONDS);

        return refreshToken;
    }

    /**
     * 根据要过期的token获取新token
     *
     * @param secretKey        密钥
     * @param jwtToken         上次的JWT经过解析后的对象
     * @param validityTime     有效时间
     * @param allowExpiresTime 允许过期的时间
     * @return
     */
    public String refreshToken(String secretKey, DecodedJWT jwtToken, Long validityTime, Long allowExpiresTime) {
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
                .withJWTId(jwtToken.getId())
                .withClaim("sub", jwtToken.getSubject())
                .withClaim("iat", Date.from(exp))
                .withClaim("exp", Date.from(newExp))
                .withClaim(PAYLOAD_MAP, jwtToken.getClaim(PAYLOAD_MAP).asMap())
                .sign(algorithm);
        log.debug("create refresh token [" + token + "]; iat: " + Date.from(exp) + " exp: " + Date.from(newExp));
        return token;
    }

    /**
     * 删除缓存token
     *
     * @param token
     */
    public void destroyToken(String token) {
        DecodedJWT decodedJWT = this.decode(token);
    }

    /**
     * 删除缓存token
     *
     * @param token
     */
    public void destroyCachedToken(String token) {
        DecodedJWT decodedJWT = this.decode(token);
        stringRedisTemplate.delete(decodedJWT.getId());
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String secretKey = "5371f568a45e5ab1f442c38e0932aef24447139c";

        JwtUtils<BaseJwtUser> jwtUtils = new JwtUtils<>();

        ComJwtUser jwtUser = new ComJwtUser();
        jwtUser.setApplication("test");
        jwtUser.setUserId("1");
        jwtUser.setJwtId(UUID.randomUUID().toString());
//        Map<String, Object> userMap = (Map<String, Object>) ObjectUtils.objectToMap(jwtUser);

//        String token = jwtUtils.create(secretKey, jwtUser.getJwtId(), userMap, Instant.now(), 100L);
//
//        System.out.println(token);
//
//        jwtUtils.verify(secretKey, token);

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXlsb2FkTWFwIjp7ImNsYXp6IjoiY29tLmdsc3gucGxhdC5qd3QuYmFzZS5Db21Kd3RVc2VyIiwidXNlcklkIjoiMSIsImp3dElkIjoiMTIyMzE5ZTAtMDQyMS00MDNjLTkyYzctMGU0MzU2MWU0ZjNkIn0sImV4cCI6MTYwNDU2NTQzNCwiaWF0IjoxNjA0NTY1MzM0LCJqdGkiOiIxMjIzMTllMC0wNDIxLTQwM2MtOTJjNy0wZTQzNTYxZTRmM2QifQ.MH_ie-zX6GqM4JWrCEevgNRorMwhzNurL60t6RoiYZs";

//        Map<String, Object> claimMap = jwtUtils.parseClaim(token);
//        Class clazz = jwtUtils.getJwtUserClass(token);

        DecodedJWT decodedJWT = jwtUtils.decode(token);

        if (jwtUtils.isNeedRefreshToken(decodedJWT)) {
            token = jwtUtils.refreshToken(secretKey, decodedJWT, 100L, 200L);

            System.out.println(token);
        }
        jwtUtils.verify(secretKey, token);
    }

}