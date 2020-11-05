package com.glsx.plat.context.aop;

import com.glsx.plat.common.annotation.NoResubmit;
import com.glsx.plat.core.web.R;
import com.glsx.plat.web.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交切面
 *
 * @author payu
 */
@Slf4j
@Aspect
@Component
public class NoResubmitAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * <p> 【环绕通知】 用于拦截指定方法，判断用户表单保存操作是否属于重复提交 <p>
     * <p>
     * 定义切入点表达式： execution(public * (…))
     * 表达式解释： execution：主体 public:可省略 *：标识方法的任意返回值 任意包+类+方法(…) 任意参数
     * <p>
     * com.zhengqing.demo.modules.*.api ： 标识AOP所切服务的包名，即需要进行横切的业务类
     * .*Controller ： 标识类名，*即所有类
     * .*(..) ： 标识任何方法名，括号表示参数，两个点表示任何参数类型
     *
     * @param pjp：切入点对象
     * @param noResubmit:自定义的注解对象
     * @return: java.lang.Object
     */
    @Around("@annotation(noResubmit)") //execution(* com.glsx.*.controller.*Controller.*(..))
    public Object doAround(ProceedingJoinPoint pjp, NoResubmit noResubmit) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

            // 拿到ip地址、请求路径、token
            String ip = IpUtils.getIpAddr(request);
            String url = request.getRequestURL().toString();
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);

            // 现在时间
            long now = System.currentTimeMillis();

            // 设置重复提交限制为3秒内
            final int timeout = 3;

            // TODO 自定义key值方式,带上操作用户信息token


            String key = "REQUEST_FORM_" + ip;
            if (stringRedisTemplate.hasKey(key)) {
                // 上次表单提交时间
                long lastTime = stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
                // 如果现在距离上次提交时间小于设置的默认时间 则 判断为重复提交 否则 正常提交 -> 进入业务处理
                if ((now - lastTime) > noResubmit.time()) {
                    // 非重复提交操作 - 重新记录操作时间
                    stringRedisTemplate.opsForValue().set(key, String.valueOf(now), timeout);
                    // 进入处理业务
                    R result = (R) pjp.proceed();
                    return result;
                } else {
                    return R.error("请勿重复提交!");
                }
            } else {
                // 这里是第一次操作
                stringRedisTemplate.opsForValue().set(key, String.valueOf(now), timeout);
                R result = (R) pjp.proceed();
                return result;
            }
        } catch (Throwable e) {
            log.error("校验表单重复提交时异常: {}", e.getMessage());
            return R.error("校验表单重复提交时异常!");
        }
    }

}
