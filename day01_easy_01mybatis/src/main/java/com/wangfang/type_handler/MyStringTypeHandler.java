package com.wangfang.type_handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(String.class)
public class MyStringTypeHandler implements TypeHandler<String>{

    private Logger log = Logger.getLogger(MyStringTypeHandler.class);

    public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        log.info("-----使用自定义的typeHandler----");
        preparedStatement.setString(i,s);
    }

    public String getResult(ResultSet resultSet, String s) throws SQLException {
        log.info("-----使用自定义的typeHandler,resultSet列名获取字符串----");
        return resultSet.getString(s);
    }

    public String getResult(ResultSet resultSet, int i) throws SQLException {
        log.info("-----使用自定义的typeHandler,resultSet列下标获取字符串----");
        return resultSet.getString(i);
    }

    public String getResult(CallableStatement callableStatement, int i) throws SQLException {
        log.info("-----使用自定义的typeHandler,callableStatement,resultSet列下标获取字符串----");
        return callableStatement.getString(i);
    }
}
