package com.wangfang.intercepts;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

/**
 * 限制返回行数拦截器
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class,Integer.class}
        )})
public class QueryLimitPlugin implements Interceptor {

    private int limit;
    private String dbType;

    /**
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    public Object intercept(Invocation invocation) throws Throwable {
        //取出被拦截对象
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        //分离代理对象,从而形成多次代理,通过两次循环最原始的被代理类,Mybtis使用的jdk代理
        MetaObject metaHandler = SystemMetaObject.forObject(handler);

        while (metaHandler.hasGetter("h")){
            Object object = metaHandler.getValue("h");
            metaHandler = SystemMetaObject.forObject(object);
        }
        //分离最后一个代理对象的目标类
        while (metaHandler.hasGetter("target")){
            Object object = metaHandler.getValue("target");
            metaHandler = SystemMetaObject.forObject(object);
        }
        //取出即将要执行的SQL
        String sql = (String) metaHandler.getValue("delegate.boundSql.sql");

        String limitSql;

        if ("mysql".equals(this.dbType)){
            sql = sql.trim();
            //将参数写入sql
            limitSql = "select * from ("+sql+")"+"user "+"limit "+limit;
            //重写将要执行的sql
            metaHandler.setValue("delegate.boundSql.sql",limitSql);
        }
        //调用原来对象的代理方法,进入责任链的下一层级
        return invocation.proceed();
    }

    /**
     * 使用默认的plugin生成代理对象
     * @param o
     * @return
     */
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    public void setProperties(Properties properties) {
        String strLimit = properties.getProperty("limit","50");
        this.limit = Integer.parseInt(strLimit);
        this.dbType = properties.getProperty("dbType","mysql");
    }
}
