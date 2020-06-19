package com.glsx.plat.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.common.utils.ObjectUtils;
import com.glsx.plat.common.utils.StringUtils;
import com.glsx.plat.core.constant.BasicConstants;
import com.glsx.plat.core.web.R;
import com.glsx.plat.exception.SystemMessage;
import com.glsx.plat.jwt.base.BaseJwtUser;
import com.glsx.plat.jwt.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录拦截器
 * @author payu
 */
@Slf4j
@Component
public class VisitInterceptor<T extends BaseJwtUser> implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean needLoginFlag = true; // 默认需要登录

        NoLogin noLogin = null;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 1.先判断类上是否存在@NoLogin注解[默认无需登录注解]
        Class<?> targetBean = handlerMethod.getBeanType();
        noLogin = targetBean.getAnnotation(NoLogin.class);
        if (null != noLogin) {
            // 如果存在,则根据注解的value()确定是否需要登录访问
            needLoginFlag = !noLogin.value();
        }
        // 2.再判断调用方法上是否存在@NoLogin注解[默认无需登录注解]
        noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
        if (null != noLogin) {
            // 如果存在,则根据注解的value()确定是否需要登录访问[方法覆盖类]
            needLoginFlag = !noLogin.value();
        }
        // 3.需要登录访问则去验证token,反之,则通过
        if (!needLoginFlag) return true;

        // 判断是否登录
        String token = request.getHeader(BasicConstants.REQUEST_HEADERS_TOKEN);
        // 1.判断请求是否携带token
        if (StringUtils.isBlank(token)) {
            // 不存在token数据
            log.warn("【访问拦截】用户的请求Header中未包含token信息.[请求头Authorization]");
            needLogin(response);
            return false;
        }

        // 2.验证token
        if (verifyToken(token)) return true;

        // 需要登录
        needLogin(response);

        return false;
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
        if (token.startsWith("Bearer")) token = token.replace("Bearer ", "");
        try {
            Class clazz = jwtUtils.getJwtUserClass(token);

            //解析token，反转成JwtUser对象
            Map<String, Object> userMap = jwtUtils.parseClaim(clazz, token);
            T user = (T) ObjectUtils.mapToObject(userMap, clazz);

            //1 . 根据token解密，解密出jwt-id , 先从redis中查找出redisToken，匹配是否相同
            String redisToken = stringRedisTemplate.opsForValue().get(jwtUtils.getJWT_SESSION_PREFIX() + user.getJwtId());
            if (!token.equals(redisToken)) return false;

            //2 . 验证token
            DecodedJWT decodedJWT = jwtUtils.verify(token, userMap);

            //3 . Redis缓存JWT续期
            stringRedisTemplate.opsForValue().set(jwtUtils.getJWT_SESSION_PREFIX() + user.getJwtId(), redisToken, jwtUtils.getTtl(), TimeUnit.MILLISECONDS);

            return true;
        } catch (Exception e) { //捕捉到任何异常都视为校验失败
            log.error(e.getMessage());
            return false;
        }
    }

    private void needLogin(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSON.toJSONString(R.error(SystemMessage.NOT_LOGIN.getCode(), SystemMessage.NOT_LOGIN.getMsg())));
            writer.flush();
        }
    }

}
