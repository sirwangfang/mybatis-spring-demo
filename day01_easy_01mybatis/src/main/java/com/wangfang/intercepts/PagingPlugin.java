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
     * Ĭ�ϲ���
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
     * �Ӵ�������з������ʵ����
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
     * �ж��Ƿ�ʱselect���
     * @param sql
     * @return
     */
    private boolean checkSelect(String sql){
        String trimSql = sql.trim();
        int index = trimSql.toLowerCase().indexOf("select");
        return index == 0;
    }

    /**
     * �ֽ��ҳ����
     * @param paramterObject --sql�������
     * @return ��ҳ����
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
     * ��ȡ����������
     * @param ivt
     * @param metaStatementHandler
     * @param boundSql
     * @return
     */
    private int getTatol(Invocation ivt, MetaObject metaStatementHandler, BoundSql boundSql){
        //��ȡ��ǰ��mappedStatement
        MappedStatement mappedStatement =
                (MappedStatement) metaStatementHandler.getValue("delegate.mapperStatement");
        //���ö���
        Configuration configuration = mappedStatement.getConfiguration();
        //��ȡ��ǰִ�е�sql'
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        //��дΪͳ��������sql'
        String countSql = "select count(*) as total from("+sql+") user $_paging";
        //��ȡ���ط�������,connection
        Connection connection = (Connection) ivt.getArgs()[0];

        PreparedStatement ps = null;
        int total = 0;
        //Ԥ�����ܼ�����sql
        try {
            ps = connection.prepareStatement(countSql);
            //����ͳ��������Boundsql
            BoundSql countBoundSql = new BoundSql(configuration,countSql,
                    boundSql.getParameterMappings(),boundSql.getParameterObject());
            //����mybatis��parameterHandler������������sql�Ĳ���
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
            log.info("������:"+total);
            return total;
        }

    }

    /**
     * ������ҳ��
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
     * ���ҳ�����ȷ��
     * @param checkFlag
     * @param pageNum
     * @param pageTotal
     * @throws Exception
     */
    private void checkPage(Boolean checkFlag,Integer pageNum,Integer pageTotal) throws Exception {
        if (checkFlag){
            if (pageNum > pageTotal){
                throw new Exception("��ѯʧ�� ��ǰҳ�����������");
            }
        }
    }

    /**
     * ��дsql������sql��ҳҪ��
     * @param invocation
     * @param metaStatementHandler
     * @param boundSql
     * @param page
     * @param pageSize
     * @return
     */
    private Object changeSql(Invocation invocation,MetaObject metaStatementHandler,
                             BoundSql boundSql,int page,int pageSize){
        //��ȡ��ǰִ�е�sql
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql,sql");

        //�޸�sql
        String newSql = "select * from ("+sql +")" + "user limit ?,?";

        //�޸ĵ�ǰҪִ�е�sql
        metaStatementHandler.setValue("delegate.boundSql,sql",newSql);

        try {
            PreparedStatement ps = (PreparedStatement) invocation.proceed();
            //����SQL�ܲ�������
            int count = ps.getParameterMetaData().getParameterCount();
            //����������ҳ����
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
