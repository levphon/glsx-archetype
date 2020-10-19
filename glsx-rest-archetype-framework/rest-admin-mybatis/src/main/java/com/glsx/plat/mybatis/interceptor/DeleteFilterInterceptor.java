package com.glsx.plat.mybatis.interceptor;

import com.glsx.plat.core.enums.SysConstants;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.Properties;

/**
 * @author payu
 */
@Slf4j
//@Component
//@Intercepts({@Signature(type = Executor.class,
//        method = "select",
//        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class DeleteFilterInterceptor implements Interceptor {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler handler = (StatementHandler) invocation.getTarget();
        //由于mappedStatement为protected的，所以要通过反射获取
        MetaObject statementHandler = SystemMetaObject.forObject(handler);
        //mappedStatement中有我们需要的方法id
        MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");
        //获取方法id
        String statementId = mappedStatement.getId();
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
        Alias alias = plainSelect.getFromItem().getAlias();
        if (alias != null) {
            equalsTo.setLeftExpression(new StringValue(alias.getName() + ".del_flag"));
        } else {
            equalsTo.setLeftExpression(new StringValue("del_flag"));
        }
        equalsTo.setRightExpression(new StringValue(SysConstants.DeleteStatus.normal.getCode().toString()));
        AndExpression andExpression = new AndExpression(parenthesis, equalsTo);
        plainSelect.setWhere(andExpression);
        //将增强后的sql放回
        statementHandler.setValue("delegate.boundSql.sql", select.toString());
        log.info(statementId + ": " + select.toString());
        return invocation.proceed();

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public static void main(String[] args) throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        String sql = "select * from t_user t1,t_user t2 ";
        Select select = (Select) parserManager.parse(new StringReader(sql));
        SelectBody selectBody = select.getSelectBody();
        PlainSelect plainSelect = (PlainSelect) selectBody;
        Expression where = plainSelect.getWhere();
        if (where == null) {
            // 如果 没有 where ,添加一个为真为表达式
            where = new StringValue("1=1");
        }
        Expression parenthesis = new Parenthesis(where);
        EqualsTo equalsTo = new EqualsTo();
        Alias alias = plainSelect.getFromItem().getAlias();
        if (alias != null) {
            equalsTo.setLeftExpression(new StringValue(alias.getName() + ".del_flag"));
        } else {
            equalsTo.setLeftExpression(new StringValue("del_flag"));
        }
        equalsTo.setRightExpression(new StringValue(SysConstants.DeleteStatus.normal.getCode().toString()));
        AndExpression andExpression = new AndExpression(parenthesis, equalsTo);
        plainSelect.setWhere(andExpression);
        System.out.println(select.toString());
    }

}
