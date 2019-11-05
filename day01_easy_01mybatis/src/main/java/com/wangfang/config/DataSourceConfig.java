package com.wangfang.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

public class DataSourceConfig extends UnpooledDataSourceFactory {
    private final DataSource dataSource;
    public DataSourceConfig() {
        this.dataSource = new DruidDataSource();
    }
}
