package com.wangfang.intercepts;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.log4j.Logger;

import java.util.Properties;

@Intercepts({@Signature(
        type = Executor.class,//确定要拦截的对象
        method = "update",
        args = {MappedStatement.class,Object.class}
)})
public class MyPlugin implements Interceptor{
    private Properties props = null;
    private static final Logger log = Logger.getLogger(MyPlugin.class);
    /**
     * 代替拦截对象方法的内容
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
     * 生成对象的代理,这里常用Mybatis提供的Plugin类的wrap方法
     * @param o
     * @return
     */
    public Object plugin(Object o) {
        log.info("调用plugin类生成代理对象");
        return Plugin.wrap(o,this);
    }

    /**
     * 获取插件配置的属性,配置在mybatis的配置文件中
     * @param properties
     */
    public void setProperties(Properties properties) {
        log.info(properties.getProperty("dbType"));
        this.props = properties;
    }
}
