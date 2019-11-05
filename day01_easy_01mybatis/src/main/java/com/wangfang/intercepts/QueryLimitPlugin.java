package com.wangfang.intercepts;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

/**
 * ���Ʒ�������������
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
        //ȡ�������ض���
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        //����������,�Ӷ��γɶ�δ���,ͨ������ѭ����ԭʼ�ı�������,Mybtisʹ�õ�jdk����
        MetaObject metaHandler = SystemMetaObject.forObject(handler);

        while (metaHandler.hasGetter("h")){
            Object object = metaHandler.getValue("h");
            metaHandler = SystemMetaObject.forObject(object);
        }
        //�������һ����������Ŀ����
        while (metaHandler.hasGetter("target")){
            Object object = metaHandler.getValue("target");
            metaHandler = SystemMetaObject.forObject(object);
        }
        //ȡ������Ҫִ�е�SQL
        String sql = (String) metaHandler.getValue("delegate.boundSql.sql");

        String limitSql;

        if ("mysql".equals(this.dbType)){
            sql = sql.trim();
            //������д��sql
            limitSql = "select * from ("+sql+")"+"user "+"limit "+limit;
            //��д��Ҫִ�е�sql
            metaHandler.setValue("delegate.boundSql.sql",limitSql);
        }
        //����ԭ������Ĵ�����,��������������һ�㼶
        return invocation.proceed();
    }

    /**
     * ʹ��Ĭ�ϵ�plugin���ɴ������
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
