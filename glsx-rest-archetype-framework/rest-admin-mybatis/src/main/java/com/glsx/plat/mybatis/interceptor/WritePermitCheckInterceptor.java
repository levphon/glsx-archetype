package com.glsx.plat.mybatis.interceptor;

import com.glsx.plat.common.annotation.ReadWrite;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Properties;

//@Component
//@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class WritePermitCheckInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        ReadWrite readWrite = method.getAnnotation(ReadWrite.class);
        if (readWrite == null){
            return invocation.proceed();
        }

        StatementHandler handler = (StatementHandler) invocation.getTarget();

        MetaObject statementHandler = SystemMetaObject.forObject(handler);

        MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (sqlCommandType == SqlCommandType.SELECT) {
            return invocation.proceed();
        }



        return null;
    }

    @Override
    public Object plugin(Object target) {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
