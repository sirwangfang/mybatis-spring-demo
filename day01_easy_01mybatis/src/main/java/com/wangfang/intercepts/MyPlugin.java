package com.wangfang.intercepts;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.log4j.Logger;

import java.util.Properties;

@Intercepts({@Signature(
        type = Executor.class,//ȷ��Ҫ���صĶ���
        method = "update",
        args = {MappedStatement.class,Object.class}
)})
public class MyPlugin implements Interceptor{
    private Properties props = null;
    private static final Logger log = Logger.getLogger(MyPlugin.class);
    /**
     * �������ض��󷽷�������
     * @param invocation
     * @return
     * @throws Throwable
     */
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("before ......");
        Object obj = invocation.proceed();
        log.info("after.......");
        return obj;
    }

    /**
     * ���ɶ���Ĵ���,���ﳣ��Mybatis�ṩ��Plugin���wrap����
     * @param o
     * @return
     */
    public Object plugin(Object o) {
        log.info("����plugin�����ɴ������");
        return Plugin.wrap(o,this);
    }

    /**
     * ��ȡ������õ�����,������mybatis�������ļ���
     * @param properties
     */
    public void setProperties(Properties properties) {
        log.info(properties.getProperty("dbType"));
        this.props = properties;
    }
}
