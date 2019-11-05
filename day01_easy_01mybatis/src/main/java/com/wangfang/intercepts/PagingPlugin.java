package com.wangfang.intercepts;

import com.wangfang.domain.PageParams;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class,Integer.class}
)})
public class PagingPlugin implements Interceptor{
    /**
     * 默认参数
     */
    private Integer defaultPage;
    private Integer defaultPageSize;
    private Boolean defaultUseFlag;
    private Boolean defaultCheckFlag;

    private static final Logger log = Logger.getLogger(PagingPlugin.class);


    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler stmtHandler = getUnProxyObject(invocation);

        MetaObject metaStateHandler = SystemMetaObject.forObject(stmtHandler);

        String sql = (String) metaStateHandler.getValue("delegate.boundSql.sql");

        if (!checkSelect(sql)){
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaStateHandler.getValue("delegate.boundSql");

        Object parameterObject = boundSql.getParameterObject();

        PageParams pageParams = getPageParams(parameterObject);

        if(pageParams == null){
            return invocation.proceed();
        }

        Integer pageNum = pageParams.getPage() == null ? this.defaultPage:pageParams.getPage();
        Integer pageSize = pageParams.getPageSize() == null ? this.defaultPageSize:pageParams.getPageSize();
        Boolean useFlag = pageParams.getUseFlag() == null ? this.defaultUseFlag:pageParams.getUseFlag();
        Boolean useFlagCheck = pageParams.getCheckFlag() == null ? this.defaultCheckFlag:pageParams.getCheckFlag();

        if (!useFlag){
            return invocation.proceed();
        }

        int total = getTatol(invocation,metaStateHandler,boundSql);

        setTotalToPageParam(pageParams,total,pageSize);

        checkPage(useFlagCheck,pageNum,pageParams.getTotalPage());
        return changeSql(invocation,metaStateHandler,boundSql,pageNum,pageSize);
    }

    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    public void setProperties(Properties properties) {
        String strDefaultPage = properties.getProperty("default.page","1");
        String strDefaultPageSize = properties.getProperty("default.pageSize","4");
        String strDefaultUserFlag = properties.getProperty("defaultUseFlag","false");
        String strDefaultCheckFlag = properties.getProperty("defaultCheckFlag","false");
        this.defaultCheckFlag = Boolean.parseBoolean(strDefaultCheckFlag);
        this.defaultPage = Integer.parseInt(strDefaultPage);
        this.defaultPageSize = Integer.parseInt(strDefaultPageSize);
        this.defaultUseFlag = Boolean.parseBoolean(strDefaultUserFlag);


    }

    /**
     * 从代理对象中分离出真实对象
     * @param ivt
     * @return
     */
    private StatementHandler getUnProxyObject(Invocation ivt){
        StatementHandler statementHandler = (StatementHandler) ivt.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        Object object = null;
        while (metaStatementHandler.hasGetter("h")){
            object = metaStatementHandler.getValue("h");
        }
        if (object == null)
            return statementHandler;
        return (StatementHandler) object;
    }

    /**
     * 判断是否时select语句
     * @param sql
     * @return
     */
    private boolean checkSelect(String sql){
        String trimSql = sql.trim();
        int index = trimSql.toLowerCase().indexOf("select");
        return index == 0;
    }

    /**
     * 分解分页参数
     * @param paramterObject --sql允许参数
     * @return 分页参数
     */
    private PageParams getPageParams(Object paramterObject){
        if (paramterObject == null)
            return null;
        PageParams pageParams = null;
        if (paramterObject instanceof Map){
            Map<String,Object> paramMap = (Map<String, Object>) paramterObject;

            Set<String> keySet = paramMap.keySet();

            Iterator<String> iterator = keySet.iterator();

            while (iterator.hasNext()){
                String key = iterator.next();
                Object value = paramMap.get(key);
                if (value instanceof PageParams){
                    return (PageParams) value;
                }
            }
        }else if (paramterObject instanceof PageParams){
            return (PageParams) paramterObject;
        }
        return pageParams;
    }

    /**
     * 获取返回总条数
     * @param ivt
     * @param metaStatementHandler
     * @param boundSql
     * @return
     */
    private int getTatol(Invocation ivt, MetaObject metaStatementHandler, BoundSql boundSql){
        //获取当前的mappedStatement
        MappedStatement mappedStatement =
                (MappedStatement) metaStatementHandler.getValue("delegate.mapperStatement");
        //配置对象
        Configuration configuration = mappedStatement.getConfiguration();
        //获取当前执行的sql'
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        //改写为统计总数的sql'
        String countSql = "select count(*) as total from("+sql+") user $_paging";
        //获取拦截方法参数,connection
        Connection connection = (Connection) ivt.getArgs()[0];

        PreparedStatement ps = null;
        int total = 0;
        //预编译总计总数sql
        try {
            ps = connection.prepareStatement(countSql);
            //构建统计总数的Boundsql
            BoundSql countBoundSql = new BoundSql(configuration,countSql,
                    boundSql.getParameterMappings(),boundSql.getParameterObject());
            //构建mybatis的parameterHandler用来设置总数sql的参数
            ParameterHandler parameterHandler =
                    new DefaultParameterHandler(mappedStatement,boundSql.getParameterObject(),countBoundSql);

            parameterHandler.setParameters(ps);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            log.info("总条数:"+total);
            return total;
        }

    }

    /**
     * 计算总页数
     * @param pageParams
     * @param total
     * @param pageSize
     */
    private void setTotalToPageParam(PageParams pageParams,int total,int pageSize){
        pageParams.setTotal(total);
        int totalPage = total % pageSize == 0 ? total/pageSize : total/pageSize+1;

        pageParams.setTotalPage(totalPage);
    }

    /**
     * 检查页码的正确性
     * @param checkFlag
     * @param pageNum
     * @param pageTotal
     * @throws Exception
     */
    private void checkPage(Boolean checkFlag,Integer pageNum,Integer pageTotal) throws Exception {
        if (checkFlag){
            if (pageNum > pageTotal){
                throw new Exception("查询失败 当前页码大于总条数");
            }
        }
    }

    /**
     * 改写sql以满足sql分页要求
     * @param invocation
     * @param metaStatementHandler
     * @param boundSql
     * @param page
     * @param pageSize
     * @return
     */
    private Object changeSql(Invocation invocation,MetaObject metaStatementHandler,
                             BoundSql boundSql,int page,int pageSize){
        //获取当前执行的sql
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql,sql");

        //修改sql
        String newSql = "select * from ("+sql +")" + "user limit ?,?";

        //修改当前要执行的sql
        metaStatementHandler.setValue("delegate.boundSql,sql",newSql);

        try {
            PreparedStatement ps = (PreparedStatement) invocation.proceed();
            //计算SQL总参数个数
            int count = ps.getParameterMetaData().getParameterCount();
            //设置两个分页参数
            ps.setInt(count -1,(page -1)*pageSize);
            ps.setInt(count,pageSize);
            return ps;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
