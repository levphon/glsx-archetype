package cn.com.glsx.canal.common.bussinessFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author zhouhaibao
 * @date 2021/3/8 11:31
 */
@Service
public class BussinessFactory implements ApplicationContextAware {

    //ApplicationContext对象是Spring开源框架的上下文对象实例，在项目运行时自动装载Handler内的所有信息到内存。
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BussinessFactory.applicationContext == null) {
            BussinessFactory.applicationContext = applicationContext;
        }
    }

    /**
     * 获取service子类
     *
     * @param bustype
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getInstance(String bustype, Class<T> requiredType) {
        String handerType = BussinessType.getHanderType(bustype);
        T bean = applicationContext.getBean(handerType, requiredType);
        return bean;
    }

}
