package com.glsx.plat.mybatis.interceptor;

//import com.glsx.plat.common.annotation.LinkPermitQuery;
import com.glsx.plat.common.annotation.ReadWrite;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Set;

//@Component
//@Intercepts({@org.apache.ibatis.plugin.Signature(type = Executor.class, method = "query",
//        args = {
//                MappedStatement.class,
//                Object.class,
//                RowBounds.class,
//                ResultHandler.class,
//                CacheKey.class,
//                BoundSql.class})})
public class QueryPermitLinkInterceptor implements Interceptor {


//    private SubTableHandler subTableHandler;

    private Set<String> handleSqlIds;

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    private String resetSql(String sql) {
        return "";
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Method method = invocation.getMethod();

//        LinkPermitQuery readWrite = method.getAnnotation(LinkPermitQuery.class);
//        if (readWrite == null){
//            return invocation.proceed();
//        }

        StatementHandler handler = (StatementHandler) invocation.getTarget();

        MetaObject statementHandler = SystemMetaObject.forObject(handler);
        //mappedStatement中有我们需要的方法id
        MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        //如果不是 select ,也不需要增强
        if (sqlCommandType != SqlCommandType.SELECT) {
            return invocation.proceed();
        }

        //增强 sql 功能 ,增加数据权限
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        SelectBody selectBody = select.getSelectBody();
        PlainSelect plainSelect = (PlainSelect) selectBody;

        //增强 sql
        Expression where = plainSelect.getWhere();
        if (where == null) {
            // 如果 没有 where ,添加一个为真为表达式
            where = new StringValue("1");
        }
        Expression parenthesis = new Parenthesis(where);
        EqualsTo equalsTo = new EqualsTo();
//        equalsTo.accept();
        AndExpression andExpression = new AndExpression(parenthesis, equalsTo);
        plainSelect.setWhere(andExpression);
        //将增强后的sql放回
        statementHandler.setValue("delegate.boundSql.sql", select.toString());

        resetSql(invocation);
        return invocation.proceed();
    }
    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }
    @Override
    public void setProperties(Properties properties) {

    }
    private void resetSql(Invocation invocation) {
        final Object[] args = invocation.getArgs();
        BoundSql boundSql = (BoundSql) args[5];
        if(!StringUtils.isEmpty(boundSql.getSql())) {
            modify(boundSql,"sql",resetSql(boundSql.getSql()));
        }
    }
    private static void modify(Object object, String fieldName, Object newFieldValue){
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            if(!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(object, newFieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
